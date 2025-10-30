/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package kotlinx.rpc.grpc.server.internal

import cnames.structs.grpc_call
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.cinterop.Arena
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.get
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.Status
import kotlinx.rpc.grpc.StatusCode
import kotlinx.rpc.grpc.StatusException
import kotlinx.rpc.grpc.descriptor.MethodDescriptor
import kotlinx.rpc.grpc.descriptor.MethodType
import kotlinx.rpc.grpc.descriptor.methodType
import kotlinx.rpc.grpc.internal.BatchResult
import kotlinx.rpc.grpc.internal.CompletionQueue
import kotlinx.rpc.grpc.internal.destroyEntries
import kotlinx.rpc.grpc.internal.internalError
import kotlinx.rpc.grpc.internal.toGrpcByteBuffer
import kotlinx.rpc.grpc.internal.toGrpcSlice
import kotlinx.rpc.grpc.internal.toKotlin
import kotlinx.rpc.grpc.internal.toRaw
import kotlinx.rpc.grpc.statusCode
import kotlinx.rpc.protobuf.input.stream.asInputStream
import kotlinx.rpc.protobuf.input.stream.asSource
import libkgrpc.GRPC_OP_RECV_CLOSE_ON_SERVER
import libkgrpc.GRPC_OP_RECV_MESSAGE
import libkgrpc.GRPC_OP_SEND_INITIAL_METADATA
import libkgrpc.GRPC_OP_SEND_MESSAGE
import libkgrpc.GRPC_OP_SEND_STATUS_FROM_SERVER
import libkgrpc.grpc_byte_buffer
import libkgrpc.grpc_byte_buffer_destroy
import libkgrpc.grpc_call_cancel_with_status
import libkgrpc.grpc_call_unref
import libkgrpc.grpc_op
import libkgrpc.grpc_slice_unref
import libkgrpc.grpc_status_code
import kotlin.concurrent.Volatile
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner

internal class NativeServerCall<Request, Response>(
    // ownership is transferred to the call
    val raw: CPointer<grpc_call>,
    val cq: CompletionQueue,
) : ServerCall<Request, Response>() {

    constructor(
        raw: CPointer<grpc_call>,
        cq: CompletionQueue,
        methodDescriptor: MethodDescriptor<Request, Response>,
    ) : this(raw, cq) {
        setMethodDescriptor(methodDescriptor)
    }

    @Suppress("unused")
    private val rawCleaner = createCleaner(raw) {
        grpc_call_unref(it)
    }

    private val listener = DeferredCallListener<Request>()
    private var methodDescriptor: MethodDescriptor<Request, Response>? = null
    private val callbackMutex = ReentrantLock()
    private var initialized = false
    private var cancelled = false
    private var closed = false
    private val finalized = atomic(false)

    // tracks whether the initial metadata has been sent.
    // this is used to determine if we have to send the initial metadata
    // when we try to close the call.
    private var sentInitialMetadata = false

    // tracks whether at least one request message has been received on this call.
    private var receivedFirstMessage = false

    // we currently don't buffer messages, so after one `sendMessage` call, ready turns false. (KRPC-192)
    private val ready = atomic(true)

    init {
        initialize()
    }

    /**
     * Sets the method descriptor for this call.
     * It must be set before invoking [ServerCallHandler.startCall].
     */
    fun setMethodDescriptor(methodDescriptor: MethodDescriptor<Request, Response>) {
        this.methodDescriptor = methodDescriptor
    }

    /**
     * Set the listener created from [ServerCallHandler.startCall].
     * This must be set directly after receiving the listener from the `startCall` invocation.
     */
    fun setListener(listener: Listener<Request>) {
        this.listener.setDelegate(listener)
    }

    private fun initialize() {
        // finishes if the whole connection is closed.
        // this triggers onClose()/onCanceled() callback.
        val arena = Arena()
        val cancelled = arena.alloc<IntVar>()
        val op = arena.alloc<grpc_op> {
            op = GRPC_OP_RECV_CLOSE_ON_SERVER
            data.recv_close_on_server.cancelled = cancelled.ptr
        }
        val result = cq.runBatch(raw, op.ptr, 1u)
        if (result !is BatchResult.Submitted) {
            // we couldn't submit the initialization batch, so nothing can be done.
            arena.clear()
            finalize(true)
        } else {
            initialized = true
            result.future.onComplete {
                val cancelled = cancelled.value == 1
                arena.clear()
                finalize(cancelled)
            }
        }
    }

    /**
     * Called when the call is closed (both by the client and the server).
     */
    private fun finalize(cancelled: Boolean) {
        if (finalized.compareAndSet(expect = false, update = true)) {
            if (cancelled) {
                this.cancelled = true
                callbackMutex.withLock {
                    listener.onCancel()
                }
            } else {
                callbackMutex.withLock {
                    listener.onComplete()
                }
            }
        }
    }

    fun cancel(status: grpc_status_code, message: String) {
        cancelled = true
        grpc_call_cancel_with_status(raw, status, message, null)
    }

    /**
     * Sets the [ready] flag to true and calls the listener's onReady callback.
     * This is called as soon as the RECV_MESSAGE batch is finished (or failed).
     */
    private fun turnReady() {
        callbackMutex.withLock {
            if (ready.compareAndSet(expect = false, update = true)) {
                listener.onReady()
            }
        }
    }

    override fun isReady(): Boolean {
        return ready.value
    }

    private fun runBatch(
        ops: CPointer<grpc_op>,
        nOps: ULong,
        cleanup: () -> Unit = {},
        onSuccess: () -> Unit = {},
    ) {
        // if we are already closed, we cannot run any more batches.
        if (closed || cancelled) return cleanup()

        when (val result = cq.runBatch(raw, ops, nOps)) {
            is BatchResult.Submitted -> {
                result.future.onComplete {
                    try {
                        onSuccess()
                    } catch (e: Throwable) {
                        cancel(grpc_status_code.GRPC_STATUS_INTERNAL, e.message ?: "Unknown error")
                    } finally {
                        cleanup()
                    }
                }
            }

            BatchResult.CQShutdown -> {
                cleanup()
                cancel(grpc_status_code.GRPC_STATUS_UNAVAILABLE, "Server shutdown")
            }

            is BatchResult.SubmitError -> {
                cleanup()
                cancel(
                    grpc_status_code.GRPC_STATUS_INTERNAL,
                    "Batch could not be submitted: ${result.error}"
                )
            }
        }
    }

    override fun request(numMessages: Int) {
        check(initialized) { internalError("Call not initialized") }
        // TODO: Remove the num constraint (KRPC-213)
        require(numMessages == 1) { internalError("numMessages must be 1") }
        val methodDescriptor = checkNotNull(methodDescriptor) { internalError("Method descriptor not set") }

        val arena = Arena()
        val recvPtr = arena.alloc<CPointerVar<grpc_byte_buffer>>()
        val op = arena.alloc<grpc_op> {
            op = GRPC_OP_RECV_MESSAGE
            data.recv_message.recv_message = recvPtr.ptr
        }

        runBatch(op.ptr, 1u, cleanup = {
            arena.clear()
        }) {
            // if the call was successful, but no message was received, we reached the end-of-stream.
            // and thus the client half-closed.
            val buf = recvPtr.value
            if (buf == null) {
                // end-of-stream observed. for UNARY, absence of any request is a protocol violation.
                if (methodDescriptor.methodType == MethodType.UNARY && !receivedFirstMessage) {
                    cancel(
                        grpc_status_code.GRPC_STATUS_INTERNAL,
                        "Unary call half-closed before receiving a request message"
                    )
                } else {
                    callbackMutex.withLock {
                        listener.onHalfClose()
                    }
                }
            } else {
                try {
                    val msg = methodDescriptor.getRequestMarshaller()
                        .parse(buf.toKotlin().asInputStream())
                    // Mark that we have received at least one request message
                    receivedFirstMessage = true
                    callbackMutex.withLock {
                        listener.onMessage(msg)
                    }
                } finally {
                    grpc_byte_buffer_destroy(buf)
                }
            }
        }
    }

    override fun sendHeaders(headers: GrpcMetadata) {
        check(initialized) { internalError("Call not initialized") }
        val arena = Arena()

        val initialMetadata = with(headers) {
            arena.allocRawGrpcMetadata()
        }

        val op = arena.alloc<grpc_op> {
            op = GRPC_OP_SEND_INITIAL_METADATA
            data.send_initial_metadata.count = initialMetadata.count
            data.send_initial_metadata.metadata = initialMetadata.metadata
        }

        sentInitialMetadata = true
        runBatch(op.ptr, 1u, cleanup = {
            initialMetadata.destroyEntries()
            arena.clear()
        }) {
            // nothing to do here
        }
    }

    override fun sendMessage(message: Response) {
        check(initialized) { internalError("Call not initialized") }
        check(isReady()) { internalError("Not yet ready.") }
        val methodDescriptor = checkNotNull(methodDescriptor) { internalError("Method descriptor not set") }

        val arena = Arena()
        tryRun {
            val inputStream = methodDescriptor.getResponseMarshaller().stream(message)
            val byteBuffer = inputStream.asSource().toGrpcByteBuffer()
            ready.value = false

            val op = arena.alloc<grpc_op> {
                op = GRPC_OP_SEND_MESSAGE
                data.send_message.send_message = byteBuffer
            }

            runBatch(op.ptr, 1u, cleanup = {
                arena.clear()
                grpc_byte_buffer_destroy(byteBuffer)
            }) {
                turnReady()
            }
        }
    }

    override fun close(status: Status, trailers: GrpcMetadata) {
        check(initialized) { internalError("Call not initialized") }

        val arena = Arena()
        val trailingMetadata = with(trailers) {
            arena.allocRawGrpcMetadata()
        }

        val details = status.getDescription()?.toGrpcSlice()
        val detailsPtr = details?.getPointer(arena)

        val nOps = if (sentInitialMetadata) 1uL else 2uL

        val ops = arena.allocArray<grpc_op>(nOps.convert())

        ops[0].op = GRPC_OP_SEND_STATUS_FROM_SERVER
        ops[0].data.send_status_from_server.status = status.statusCode.toRaw()
        ops[0].data.send_status_from_server.status_details = detailsPtr
        ops[0].data.send_status_from_server.trailing_metadata_count = trailingMetadata.count
        ops[0].data.send_status_from_server.trailing_metadata = trailingMetadata.metadata

        if (!sentInitialMetadata) {
            // if we haven't sent GRPC_OP_SEND_INITIAL_METADATA yet,
            // so we must do it together with the close operation.
            ops[1].op = GRPC_OP_SEND_INITIAL_METADATA
            ops[1].data.send_initial_metadata.count = 0u
            ops[1].data.send_initial_metadata.metadata = null
        }

        runBatch(ops, nOps, cleanup = {
            if (details != null) grpc_slice_unref(details)
            trailingMetadata.destroyEntries()
            arena.clear()
        }) {
            closed = true
            // nothing to do here
        }
    }

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun getMethodDescriptor(): MethodDescriptor<Request, Response> {
        val methodDescriptor = checkNotNull(methodDescriptor) { internalError("Method descriptor not set") }
        return methodDescriptor
    }


    private inline fun <T> tryRun(crossinline block: () -> T): T {
        try {
            return block()
        } catch (e: Throwable) {
            // TODO: Log internal error as warning
            val status = when (e) {
                is StatusException -> e.getStatus()
                else -> Status(
                    StatusCode.INTERNAL,
                    description = "Internal error, so canceling the stream",
                    cause = e
                )
            }
            cancel(status.statusCode.toRaw(), status.getDescription() ?: "Unknown error")
            throw StatusException(status, trailers = null)
        }
    }
}

/**
 * A listener implementation that defers execution of its methods until a delegate is set.
 *
 * This class extends `ServerCall.Listener`, allowing it to serve as a listener for server calls.
 * Initially, incoming method calls (e.g., `onMessage`, `onHalfClose`, etc.) are queued until a delegate
 * is assigned through the `setDelegate` method. Once the delegate is set, queued methods are delivered
 * in order and all future method calls are forwarded directly to the delegate.
 */
private class DeferredCallListener<T> : ServerCall.Listener<T>() {
    @Volatile
    private var delegate: ServerCall.Listener<T>? = null
    private val mutex = ReentrantLock()
    private val queue = ArrayDeque<(ServerCall.Listener<T>) -> Unit>()

    fun setDelegate(d: ServerCall.Listener<T>) {
        mutex.withLock {
            if (delegate != null) return
            delegate = d
        }
        // drain the queue
        queue.forEach { it(d) }
        queue.clear()
    }

    private inline fun deliver(crossinline invokeListener: (ServerCall.Listener<T>) -> Unit) {
        val currentDelegate = delegate
        if (currentDelegate != null) {
            // fast path (delegate is already set)
            invokeListener(currentDelegate); return
        }
        // slow path: re-check under lock
        val safeCurrentDelegate = mutex.withLock {
            val cur = delegate
            if (cur == null) {
                queue.addLast { invokeListener(it) }
                null
            } else cur
        }
        // if the delegate was already set, call it
        if (safeCurrentDelegate != null) invokeListener(safeCurrentDelegate)
    }

    override fun onMessage(message: T) = deliver { it.onMessage(message) }
    override fun onHalfClose() = deliver { it.onHalfClose() }
    override fun onCancel() = deliver { it.onCancel() }
    override fun onComplete() = deliver { it.onComplete() }
    override fun onReady() = deliver { it.onReady() }
}