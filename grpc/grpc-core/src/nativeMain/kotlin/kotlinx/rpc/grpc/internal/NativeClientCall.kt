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
import kotlinx.io.Buffer
import kotlinx.rpc.grpc.GrpcTrailers
import kotlinx.rpc.grpc.Status
import kotlinx.rpc.grpc.StatusCode
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

        when (val callResult = cq.runBatch(this@NativeClientCall, ops, nOps)) {
            is BatchResult.Called -> {
                callResult.future.onComplete { success ->
                    if (success) {
                        onSuccess()
                    }
                    // ignore failure, as it is reflected in the client status op
                    cleanup()
                }
            }

            BatchResult.CQShutdown -> {
                cleanup()
                cancelInternal(grpc_status_code.GRPC_STATUS_UNAVAILABLE, "Channel shutdown")
            }

            is BatchResult.CallError -> {
                cleanup()
                cancelInternal(
                    grpc_status_code.GRPC_STATUS_INTERNAL,
                    "Batch could not be submitted: ${callResult.error}"
                )
            }
        }
    }

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
                    val status = Status(errorStr.value?.toKString(), statusCode.value.toKotlin(), null)
                    val trailers = GrpcTrailers()
                    arena.clear()
                    closeCall(status, trailers)
                }
                return true
            }

            BatchResult.CQShutdown -> {
                arena.clear()
                closeCall(Status(StatusCode.UNAVAILABLE, "Channel shutdown"), GrpcTrailers())
                return false
            }

            is BatchResult.CallError -> {
                arena.clear()
                closeCall(Status(StatusCode.INTERNAL, "Failed to start call: ${callResult.error}"), GrpcTrailers())
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
            grpc_metadata_array_init(meta.ptr)
            arena.clear()
        }) {
            // TODO: Send headers to listener
        }
    }

    override fun request(numMessages: Int) {
        check(numMessages > 0) { "numMessages must be > 0" }
        val listener = checkNotNull(listener) { "Not yet started" }
        check(!cancelled) { "Already cancelled" }

        fun once() {
            val arena = Arena()
            val recvPtr = arena.alloc<CPointerVar<grpc_byte_buffer>>()
            val op = arena.alloc<grpc_op> {
                op = GRPC_OP_RECV_MESSAGE
                data.recv_message.recv_message = recvPtr.ptr
            }
            runBatch(op.ptr, 1u, cleanup = { arena.clear() }) {
                val buf = recvPtr.value ?: return@runBatch // EOS
                val msg = methodDescriptor.getResponseMarshaller()
                    .parse(buf.toKotlin().asInputStream())
                listener.onMessage(msg)
                once() // post next only now
            }
        }
        once()
    }

    override fun cancel(message: String?, cause: Throwable?) {
        cancelled = true
        val message = if (cause != null) "$message: ${cause.message}" else message
        cancelInternal(grpc_status_code.GRPC_STATUS_CANCELLED, message ?: "Call cancelled")
    }

    private fun cancelInternal(statusCode: grpc_status_code, message: String) {
        val cancelResult = grpc_call_cancel_with_status(raw, statusCode, message, null)
        if (cancelResult != grpc_call_error.GRPC_CALL_OK) {
            closeCall(Status(StatusCode.INTERNAL, "Failed to cancel call: $cancelResult"), GrpcTrailers())
        }
    }

    override fun halfClose() {
        check(!halfClosed) { "Already half closed." }
        check(!cancelled) { "Already cancelled." }
        halfClosed = true

        val arena = Arena()
        val op = arena.alloc<grpc_op>() {
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
        val byteBuffer = (inputStream.source as Buffer).toGrpcByteBuffer()
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

    private fun closeCall(status: Status, trailers: GrpcTrailers) {
        // only one close call must proceed
        if (closed.compareAndSet(expect = false, update = true)) {
            val listener = checkNotNull(listener) { "Not yet started" }
            callJob.complete()
            listener.onClose(status, trailers)
        }
    }
}


