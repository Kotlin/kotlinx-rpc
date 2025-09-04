/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package kotlinx.rpc.grpc.internal

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
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readValue
import kotlinx.cinterop.value
import kotlinx.rpc.grpc.GrpcTrailers
import kotlinx.rpc.grpc.Status
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
import libkgrpc.grpc_call_ref
import libkgrpc.grpc_call_unref
import libkgrpc.grpc_op
import libkgrpc.grpc_slice
import libkgrpc.grpc_slice_unref
import libkgrpc.grpc_status_code
import kotlin.concurrent.Volatile
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner

internal class NativeServerCall<Request, Response>(
    val raw: CPointer<grpc_call>,
    val request: ServerCallbackRequest<Request, Response>,
    val methodDescriptor: MethodDescriptor<Request, Response>,
) : ServerCall<Request, Response>() {

    private val cq = request.cq

    @Suppress("unused")
    private val rawCleaner = createCleaner(raw) {
        grpc_call_unref(it)
    }

    private var listener = DeferredCallListener<Request>()
    private var callbackMutex = ReentrantLock()
    private var initialized = false
    private var cancelled = false
    private var finalized = atomic(false)

    // we currently don't buffer messages, so after one `sendMessage` call, ready turns false. (KRPC-192)
    private val ready = atomic(true)

    init {
        // take ownership of the raw pointer
        grpc_call_ref(raw)
        initialize()
    }

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
            finalize(true)
        } else {
            initialized = true
            result.future.onComplete {
                finalize(cancelled.value == 1)
            }
        }
    }

    /**
     * Called when the call is closed (both by the client and the server).
     */
    private fun finalize(cancelled: Boolean) {
        if (finalized.compareAndSet(expect = false, update = true)) {
            request.dispose()
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

    private fun cancelCall(status: grpc_status_code, message: String) {
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
        when (val result = cq.runBatch(raw, ops, nOps)) {
            is BatchResult.Submitted -> {
                result.future.onComplete {
                    try {
                        onSuccess()
                    } catch (e: Throwable) {
                        cancelCall(grpc_status_code.GRPC_STATUS_INTERNAL, e.message ?: "Unknown error")
                    } finally {
                        cleanup()
                    }
                }
            }

            BatchResult.CQShutdown -> {
                cleanup()
                cancelCall(grpc_status_code.GRPC_STATUS_UNAVAILABLE, "Server shutdown")
            }

            is BatchResult.SubmitError -> {
                cleanup()
                cancelCall(
                    grpc_status_code.GRPC_STATUS_INTERNAL,
                    "Batch could not be submitted: ${result.error}"
                )
            }
        }
    }

    override fun request(numMessages: Int) {
        check(initialized) { internalError("Call not initialized") }
        // TODO: Remove the num constraint
        require(numMessages == 1) { internalError("numMessages must be 1") }

        val arena = Arena()
        val recvPtr = arena.alloc<CPointerVar<grpc_byte_buffer>>()
        val op = arena.alloc<grpc_op> {
            op = GRPC_OP_RECV_MESSAGE
            data.recv_message.recv_message = recvPtr.ptr
        }

        runBatch(op.ptr, 1u, cleanup = { arena.clear() }) {
            // if the call was successful, but no message was received, we reached the end-of-stream.
            val buf = recvPtr.value
            if (buf == null) {
                callbackMutex.withLock {
                    listener.onHalfClose()
                }
            } else {
                val msg = methodDescriptor.getRequestMarshaller()
                    .parse(buf.toKotlin().asInputStream())
                callbackMutex.withLock {
                    listener.onMessage(msg)
                }
            }
        }
    }

    override fun sendHeaders(headers: GrpcTrailers) {
        check(initialized) { internalError("Call not initialized") }
        val arena = Arena()
        val op = arena.alloc<grpc_op> {
            op = GRPC_OP_SEND_INITIAL_METADATA
            data.send_initial_metadata.count = 0u
            data.send_initial_metadata.metadata = null
        }

        runBatch(op.ptr, 1u, cleanup = { arena.clear() }) {
            // nothing to do here
        }
    }

    override fun sendMessage(message: Response) {
        check(initialized) { internalError("Call not initialized") }
        check(isReady()) { internalError("Not yet ready.") }
        val arena = Arena()
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

    override fun close(status: Status, trailers: GrpcTrailers) {
        check(initialized) { internalError("Call not initialized") }
        val arena = Arena()

        val details = status.getDescription()?.let {
            arena.alloc<grpc_slice> {
                it.toGrpcSlice()
            }
        }
        val op = arena.alloc<grpc_op> {
            op = GRPC_OP_SEND_STATUS_FROM_SERVER
            data.send_status_from_server.status = status.statusCode.toRaw()
            data.send_status_from_server.status_details = details?.ptr
            data.send_status_from_server.trailing_metadata_count = 0u
            data.send_status_from_server.trailing_metadata = null
        }

        runBatch(op.ptr, 1u, cleanup = {
            if (details != null) grpc_slice_unref(details.readValue())
            arena.clear()
        }) {
            // nothing to do here
        }
    }

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun getMethodDescriptor(): MethodDescriptor<Request, Response> {
        return methodDescriptor
    }
}

private class DeferredCallListener<T> : ServerCall.Listener<T>() {
    @Volatile
    private var delegate: ServerCall.Listener<T>? = null
    private val mutex = ReentrantLock()
    private val q = ArrayDeque<(ServerCall.Listener<T>) -> Unit>()

    fun setDelegate(d: ServerCall.Listener<T>) {
        println("setting delegate...")
        mutex.withLock {
            if (delegate != null) return
            delegate = d
        }
        // drain the queue
        q.forEach { it(d) }
        q.clear()
    }

    private inline fun deliver(crossinline f: (ServerCall.Listener<T>) -> Unit) {
        val d = delegate
        if (d != null) {
            // fast path (delegate is already set)
            f(d); return
        }
        println("delivering to queue...")
        // slow path: re-check under lock
        val dd = mutex.withLock {
            val cur = delegate
            if (cur == null) {
                q.addLast { f(it) }
                null
            } else cur
        }
        // if the delegate was already set, call it
        if (dd != null) f(dd)
    }

    override fun onMessage(message: T) = deliver { it.onMessage(message) }
    override fun onHalfClose() = deliver { it.onHalfClose() }
    override fun onCancel() = deliver { it.onCancel() }
    override fun onComplete() = deliver { it.onComplete() }
}