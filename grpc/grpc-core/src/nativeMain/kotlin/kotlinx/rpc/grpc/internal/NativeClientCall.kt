/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package kotlinx.rpc.grpc.internal

import cnames.structs.grpc_call
import kotlinx.atomicfu.atomic
import kotlinx.cinterop.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableJob
import kotlinx.rpc.grpc.GrpcTrailers
import kotlinx.rpc.grpc.Status
import kotlinx.rpc.grpc.StatusCode
import kotlinx.rpc.protobuf.input.stream.asInputStream
import kotlinx.rpc.protobuf.input.stream.asSource
import libgrpcpp_c.*
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner


internal class NativeClientCall<Request, Response>(
    private val cq: CompletionQueue,
    internal val raw: CPointer<grpc_call>,
    private val methodDescriptor: MethodDescriptor<Request, Response>,
    private val callJob: CompletableJob,
) : ClientCall<Request, Response>() {

    @Suppress("unused")
    private val rawCleaner = createCleaner(raw) {
        grpc_call_unref(it)
    }

    init {
        callJob.invokeOnCompletion {
            when (it) {
                is CancellationException -> {
                    cancelInternal(grpc_status_code.GRPC_STATUS_CANCELLED, "Call got cancelled.")
                }

                is Throwable -> {
                    cancelInternal(grpc_status_code.GRPC_STATUS_INTERNAL, "Call failed: ${it.message}")
                }
            }
        }
    }

    private var listener: Listener<Response>? = null
    private var halfClosed = false
    private var cancelled = false
    private var closed = atomic(false)

    // tracks how many operations are in flight (not yet completed by the listener).
    // if 0, there are no more operations (except for the RECV_STATUS_ON_CLIENT op).
    // in this case, we can safely call onClose on the listener.
    // we need this mechanism to ensure that onClose is not called while any other callback is still running
    // on the listener.
    private val inFlight = atomic(0)

    // holds the received status information returned by the RECV_STATUS_ON_CLIENT batch.
    // if null, the call is still in progress. otherwise, the call can be closed as soon as inFlight is 0.
    private val closeInfo = atomic<Pair<Status, GrpcTrailers>?>(null)

    /**
     * Increments the [inFlight] counter by one.
     * This should be called before starting a batch.
     */
    private fun beginOp() {
        inFlight.incrementAndGet()
    }

    /**
     * Decrements the [inFlight] counter by one.
     * This should be called after a batch has finished (in case of success AND error)
     * AND the corresponding listener callback returned.
     *
     * If the counter reaches 0, no more listener callbacks are executed, and the call can be closed by
     * calling [tryDeliverClose].
     */
    private fun endOp() {
        if (inFlight.decrementAndGet() == 0) {
            tryDeliverClose()
        }
    }

    /**
     * Tries to close the call by invoking the listener's onClose callback.
     *
     * - If the call is already closed, this does nothing.
     * - If the RECV_STATUS_ON_CLIENT batch is still in progress, this does nothing.
     * - If the [inFlight] counter is not 0, this does nothing.
     * - Otherwise, the listener's onClose callback is invoked and the call is closed.
     */
    private fun tryDeliverClose() {
        val s = closeInfo.value ?: return
        if (inFlight.value == 0 && closed.compareAndSet(expect = false, update = true)) {
            val lst = checkNotNull(listener) { "Not yet started" }
            // allows the managed channel to join for the call to finish.
            callJob.complete()
            lst.onClose(s.first, s.second)
        }
    }

    /**
     * Sets the [closeInfo] and calls [tryDeliverClose].
     * This is called as soon as the RECV_STATUS_ON_CLIENT batch is finished.
     */
    private fun markClosePending(status: Status, trailers: GrpcTrailers) {
        if (closeInfo.compareAndSet(null, Pair(status, trailers))) {
            tryDeliverClose()
        }
    }


    override fun start(
        responseListener: Listener<Response>,
        headers: GrpcTrailers,
    ) {
        check(listener == null) { "Already started" }
        check(!cancelled) { "Already cancelled." }

        listener = responseListener

        // start receiving the status from the completion queue,
        // which is bound to the lifecycle of the call.
        val success = initializeCallOnCQ()
        if (!success) return

        sendAndReceiveInitialMetadata()
    }

    private fun runBatch(
        ops: CPointer<grpc_op>,
        nOps: ULong,
        cleanup: () -> Unit = {},
        onSuccess: () -> Unit = {},
    ) {
        // we must not try to run a batch after the call is closed.
        if (closed.value) return cleanup()

        // pre-book the batch, so onClose cannot be called before the batch finished.
        beginOp()

        when (val callResult = cq.runBatch(this@NativeClientCall, ops, nOps)) {
            is BatchResult.Called -> {
                callResult.future.onComplete { success ->
                    try {
                        if (success) {
                            onSuccess()
                        }
                    } finally {
                        // ignore failure, as it is reflected in the client status op
                        cleanup()
                        endOp()
                    }
                }
            }

            BatchResult.CQShutdown -> {
                cleanup()
                endOp()
                cancelInternal(grpc_status_code.GRPC_STATUS_UNAVAILABLE, "Channel shutdown")
            }

            is BatchResult.CallError -> {
                cleanup()
                endOp()
                cancelInternal(
                    grpc_status_code.GRPC_STATUS_INTERNAL,
                    "Batch could not be submitted: ${callResult.error}"
                )
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun initializeCallOnCQ(): Boolean {
        checkNotNull(listener) { "Not yet started" }
        val arena = Arena()
        // this must not be canceled as it sets the call status.
        // if the client itself got canceled, this will return fast.
        val statusCode = arena.alloc<grpc_status_code.Var>()
        val statusDetails = arena.alloc<grpc_slice>()
        val errorStr = arena.alloc<CPointerVar<ByteVar>>()
        val op = arena.alloc<grpc_op> {
            op = GRPC_OP_RECV_STATUS_ON_CLIENT
            data.recv_status_on_client.status = statusCode.ptr
            data.recv_status_on_client.status_details = statusDetails.ptr
            data.recv_status_on_client.error_string = errorStr.ptr
            // TODO: trailing metadata
            data.recv_status_on_client.trailing_metadata = null
        }

        when (val callResult = cq.runBatch(this@NativeClientCall, op.ptr, 1u)) {
            is BatchResult.Called -> {
                callResult.future.onComplete {
                    val details = statusDetails.toByteArray().toKString()
                    val status = Status(statusCode.value.toKotlin(), details, null)
                    val trailers = GrpcTrailers()

                    // cleanup
                    grpc_slice_unref(statusDetails.readValue())
                    if (errorStr.value != null) gpr_free(errorStr.value)
                    arena.clear()

                    // set close info and try to close the call.
                    markClosePending(status, trailers)
                }
                return true
            }

            BatchResult.CQShutdown -> {
                arena.clear()
                markClosePending(Status(StatusCode.UNAVAILABLE, "Channel shutdown"), GrpcTrailers())
                return false
            }

            is BatchResult.CallError -> {
                arena.clear()
                markClosePending(
                    Status(StatusCode.INTERNAL, "Failed to start call: ${callResult.error}"),
                    GrpcTrailers()
                )
                return false
            }
        }
    }

    private fun sendAndReceiveInitialMetadata() {
        // sending and receiving initial metadata
        val arena = Arena()
        val opsNum = 2uL
        val ops = arena.allocArray<grpc_op>(opsNum.convert())

        // send initial meta data to server
        // TODO: initial metadata
        ops[0].op = GRPC_OP_SEND_INITIAL_METADATA
        ops[0].data.send_initial_metadata.count = 0u

        val meta = arena.alloc<grpc_metadata_array>()
        // TODO: make metadata array an object (for lifecycle management)
        grpc_metadata_array_init(meta.ptr)
        ops[1].op = GRPC_OP_RECV_INITIAL_METADATA
        ops[1].data.recv_initial_metadata.recv_initial_metadata = meta.ptr

        runBatch(ops, opsNum, cleanup = {
            grpc_metadata_array_destroy(meta.ptr)
            arena.clear()
        }) {
            // TODO: Send headers to listener
        }
    }

    override fun request(numMessages: Int) {
        check(numMessages > 0) { "numMessages must be > 0" }
        val listener = checkNotNull(listener) { "Not yet started" }
        check(!cancelled) { "Already cancelled" }

        var remainingMessages = numMessages
        fun post() {
            if (remainingMessages-- <= 0) return

            val arena = Arena()
            val recvPtr = arena.alloc<CPointerVar<grpc_byte_buffer>>()
            val op = arena.alloc<grpc_op> {
                op = GRPC_OP_RECV_MESSAGE
                data.recv_message.recv_message = recvPtr.ptr
            }
            runBatch(op.ptr, 1u, cleanup = {
                if (recvPtr.value != null) grpc_byte_buffer_destroy(recvPtr.value)
                arena.clear()
            }) {
                val buf = recvPtr.value ?: return@runBatch // EOS
                val msg = methodDescriptor.getResponseMarshaller()
                    .parse(buf.toKotlin().asInputStream())
                listener.onMessage(msg)
                post() // post next only now
            }
        }
        post()
    }

    override fun cancel(message: String?, cause: Throwable?) {
        cancelled = true
        val message = if (cause != null) "$message: ${cause.message}" else message
        cancelInternal(grpc_status_code.GRPC_STATUS_CANCELLED, message ?: "Call cancelled")
    }

    private fun cancelInternal(statusCode: grpc_status_code, message: String) {
        val cancelResult = grpc_call_cancel_with_status(raw, statusCode, message, null)
        if (cancelResult != grpc_call_error.GRPC_CALL_OK) {
            markClosePending(Status(StatusCode.INTERNAL, "Failed to cancel call: $cancelResult"), GrpcTrailers())
        }
    }

    override fun halfClose() {
        check(!halfClosed) { "Already half closed." }
        check(!cancelled) { "Already cancelled." }
        halfClosed = true

        val arena = Arena()
        val op = arena.alloc<grpc_op> {
            op = GRPC_OP_SEND_CLOSE_FROM_CLIENT
        }

        runBatch(op.ptr, 1u, cleanup = { arena.clear() }) {
            // nothing to do here
        }
    }

    override fun sendMessage(message: Request) {
        checkNotNull(listener) { "Not yet started" }
        check(!halfClosed) { "Already half closed." }
        check(!cancelled) { "Already cancelled." }

        val arena = Arena()
        val inputStream = methodDescriptor.getRequestMarshaller().stream(message)
        val byteBuffer = inputStream.asSource().toGrpcByteBuffer()
        val op = arena.alloc<grpc_op> {
            op = GRPC_OP_SEND_MESSAGE
            data.send_message.send_message = byteBuffer
        }
        runBatch(op.ptr, 1u, cleanup = {
            grpc_byte_buffer_destroy(byteBuffer)
            arena.clear()
        }) {
            // Nothing to do here
        }
    }
}


