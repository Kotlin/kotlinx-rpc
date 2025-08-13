/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package kotlinx.rpc.grpc.internal

import cnames.structs.grpc_call
import kotlinx.atomicfu.atomic
import kotlinx.cinterop.*
import kotlinx.coroutines.*
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
    private val callScope: CoroutineScope,
) : ClientCall<Request, Response>() {

    @Suppress("unused")
    private val rawCleaner = createCleaner(raw) {
        grpc_call_unref(it)
    }

    // the callJob is completed by the channel on shutdown to prevent start() calls after shutdown
    private val callJob = callScope.coroutineContext[Job]!!

    private var listener: Listener<Response>? = null
    private var halfClosed = false
    private var cancelled = false
    private var closed = atomic(false)

    override fun start(
        responseListener: Listener<Response>,
        headers: GrpcTrailers,
    ) {
        check(listener == null) { "Already started" }
        check(!closed.value) { "Already closed." }
        check(!cancelled) { "Already cancelled." }
        // callJob is completed by the channel on shutdown to prevent start calls after shutdown
        check(callJob.isActive) { "Call is cancelled or completed." }

        listener = responseListener

        // we directly launch the receiveStatus() operation, which will finish ones the call is finished
        callScope.launch { receiveStatus() }
            .invokeOnCompletion {
                when (it) {
                    null -> { /* nothing to do */
                    }

                    is CancellationException -> closeCall(
                        Status(StatusCode.CANCELLED, "Call got cancelled."),
                        GrpcTrailers()
                    )

                    else -> closeCall(Status(StatusCode.INTERNAL, "Call failed.", it), GrpcTrailers())
                }
            }

        callScope.launch {
            withArena { arena ->
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

                runBatch(ops, opsNum) {
                    // TODO: Send headers to listener
                }

                // TODO: destroy with metadata array wrapper (maybe using .use{} )
                grpc_metadata_array_destroy(meta.ptr)
            }
        }
    }

    private suspend fun runBatch(
        ops: CPointer<grpc_op>,
        nOps: ULong,
        onSuccess: suspend () -> Unit = {},
    ) {
        when (val result = cq.runBatch(this, ops, nOps)) {
            BatchResult.Success -> onSuccess()
            BatchResult.ResultError -> {
                // do nothing, the client will receive the status from the completion queue
            }

            BatchResult.CQShutdown -> {
                cancelInternal(grpc_status_code.GRPC_STATUS_UNAVAILABLE, "Channel shutdown")
            }

            is BatchResult.CallError -> {
                cancelInternal(grpc_status_code.GRPC_STATUS_INTERNAL, "Batch could not be submitted: ${result.error}")
            }
        }
    }

    private suspend fun receiveStatus() = withContext(NonCancellable) {
        withArena { arena ->
            checkNotNull(listener) { "Not yet started" }
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

            // will never fail
            cq.runBatch(this@NativeClientCall, op.ptr, 1u)

            val status = Status(errorStr.value?.toKString(), statusCode.value.toKotlin(), null)
            val trailers = GrpcTrailers()
            closeCall(status, trailers)
        }
    }

    override fun request(numMessages: Int) {
        val listener = checkNotNull(listener) { "Not yet started" }
        check(!cancelled) { "Already cancelled" }
        check(!closed.value) { "Already closed." }

        callScope.launch {
            repeat(numMessages) {
                withArena { arena ->
                    val recvBufferPtr = arena.alloc<CPointerVar<grpc_byte_buffer>>()

                    val op = arena.alloc<grpc_op>() {
                        op = GRPC_OP_RECV_MESSAGE
                        data.recv_message.recv_message = recvBufferPtr.ptr
                    }

                    runBatch(op.ptr, 1u) {
                        val recvBuf = recvBufferPtr.value
                        if (recvBuf == null) {
                            println("No more messages to receive")
                            // TODO: what if we have no more messages to receive?
                        } else {
                            val messageBuffer = recvBuf.toKotlin()
                            val message = methodDescriptor.getResponseMarshaller()
                                .parse(messageBuffer.asInputStream())
                            listener.onMessage(message)
                        }
                    }
                }
            }
        }.checkNotCancelled()

    }

    override fun cancel(message: String?, cause: Throwable?) {
        cancelled = true
        if (message != null) {
            grpc_call_cancel(raw, null)
        } else {
            val message = if (cause != null) "$message: ${cause.message}" else message
            cancelInternal(grpc_status_code.GRPC_STATUS_CANCELLED, message ?: "Call cancelled")
        }
    }

    private fun cancelInternal(statusCode: grpc_status_code, message: String) {
        grpc_call_cancel_with_status(raw, statusCode, message, null)
    }

    override fun halfClose() {
        check(!halfClosed) { "Already half closed." }
        check(!cancelled) { "Already cancelled." }
        check(!closed.value) { "Already closed." }
        halfClosed = true

        callScope.launch {
            withArena { arena ->
                val op = arena.alloc<grpc_op>()
                op.op = GRPC_OP_SEND_CLOSE_FROM_CLIENT

                runBatch(op.ptr, 1u) {
                    // nothing to do here
                }
            }
        }.checkNotCancelled()
    }

    override fun sendMessage(message: Request) {
        checkNotNull(listener) { "Not yet started" }
        check(!halfClosed) { "Already half closed." }
        check(!closed.value) { "Already closed." }

        callScope.launch {
            withArena { arena ->
                val inputStream = methodDescriptor.getRequestMarshaller().stream(message)
                // TODO: handle non-byte buffer InputStream sources
                val byteBuffer = (inputStream.source as Buffer).toGrpcByteBuffer()

                try {
                    val op = arena.alloc<grpc_op> {
                        op = GRPC_OP_SEND_MESSAGE
                        data.send_message.send_message = byteBuffer
                    }

                    runBatch(op.ptr, 1u) {
                        // Nothing to do here
                    }

                } finally {
                    grpc_byte_buffer_destroy(byteBuffer)
                }
            }
        }.checkNotCancelled()
    }

    private fun closeCall(status: Status, trailers: GrpcTrailers) {
        // only one close call must proceed
        if (closed.compareAndSet(expect = false, update = true)) {
            val listener = checkNotNull(listener) { "Not yet started" }
            listener.onClose(status, trailers)
        }
    }

    private fun Job.checkNotCancelled() {
        invokeOnCompletion {
            if (it is CancellationException) {
                if (callJob.isCancelled) {
                    error("Call was already cancelled.")
                } else if (callJob.isCompleted) {
                    error("Call was already closed.")
                }
            }
        }
    }
}


