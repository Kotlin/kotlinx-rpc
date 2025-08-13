/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package kotlinx.rpc.grpc.internal

import cnames.structs.grpc_call
import kotlinx.cinterop.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.Buffer
import kotlinx.rpc.grpc.GrpcTrailers
import kotlinx.rpc.grpc.Status
import libgrpcpp_c.*
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner

internal class NativeClientCall<Request, Response>(
    private val cq: CompletionQueue,
    internal val raw: CPointer<grpc_call>,
    private val methodDescriptor: MethodDescriptor<Request, Response>,
    private val coroutineScope: CoroutineScope,
) : ClientCall<Request, Response>() {

    @Suppress("unused")
    private val rawCleaner = createCleaner(raw) {
        grpc_call_unref(it)
    }

    private var listener: Listener<Response>? = null
    private var halfClosed = false
    private var cancelled = false
    private var closed = false

    override fun start(
        responseListener: Listener<Response>,
        headers: GrpcTrailers,
    ) {
        check(listener == null) { "Already started" }
        check(methodDescriptor.methodType == MethodType.UNARY) { "Currently only unary methods are supported." }

        listener = responseListener

        coroutineScope.launch {
            withArena { arena ->

                // we directly launch the receiveStatus() operation, which will finish ones the call is finished
                launch { receiveStatus() }

                val opsNum = 2uL
                val ops = arena.allocArray<grpc_op>(opsNum.convert())

                // send initial meta data to server
                // TODO: initial metadata
                ops[0].op = GRPC_OP_SEND_INITIAL_METADATA
                ops[0].data.send_initial_metadata.count = 0u

                val meta = arena.alloc<grpc_metadata_array>()
                grpc_metadata_array_init(meta.ptr)
                ops[1].op = GRPC_OP_RECV_INITIAL_METADATA
                ops[1].data.recv_initial_metadata.recv_initial_metadata = meta.ptr

                val err = cq.runBatch(this@NativeClientCall, ops, opsNum)

                if (err != grpc_call_error.GRPC_CALL_OK) {
                    // TODO: How should we handle grpc call errors?
                    error("Failed to start call: $err")
                }

                // TODO: Send headers to listener
                grpc_metadata_array_destroy(meta.ptr)
            }
        }
    }

    private suspend fun receiveStatus() = withArena { arena ->
        val listener = checkNotNull(listener) { "Not yet started" }
        // this must not be canceled as it sets the call status.
        // if the client itself got canceled, this will return fast.
        withContext(NonCancellable) {
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
            listener.onClose(status, trailers)
            closed = true
        }
    }

    override fun request(numMessages: Int) {
        val listener = checkNotNull(listener) { "Not yet started" }
        check(!cancelled) { "Already cancelled" }
        check(!closed) { "Already closed." }

        coroutineScope.launch {
            repeat(numMessages) {
                withArena { arena ->
                    val recvBufferPtr = arena.alloc<CPointerVar<grpc_byte_buffer>>()

                    val op = arena.alloc<grpc_op>() {
                        op = GRPC_OP_RECV_MESSAGE
                        data.recv_message.recv_message = recvBufferPtr.ptr
                    }
                    val err = cq.runBatch(this@NativeClientCall, op.ptr, 1u)

                    if (err != grpc_call_error.GRPC_CALL_OK) {
                        // TODO: How should we handle grpc call errors?
                        error("Failed to call recv message batch: $err")
                    }

                    val recvBuf = recvBufferPtr.value
                    if (recvBuf == null) {
                        TODO("Initiate onClose")
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

    }

    override fun cancel(message: String?, cause: Throwable?) {
        cancelled = true
        if (message != null) {
            grpc_call_cancel(raw, null)
        }
        val message = if (cause != null) "$message: ${cause.message}" else message
        grpc_call_cancel_with_status(raw, grpc_status_code.GRPC_STATUS_CANCELLED, message, null)
    }

    override fun halfClose() {
        check(!halfClosed) { "Already half closed." }
        check(!cancelled) { "Already cancelled." }
        check(!closed) { "Already closed." }
        halfClosed = true

        coroutineScope.launch {
            withArena { arena ->
                val op = arena.alloc<grpc_op>()
                op.op = GRPC_OP_SEND_CLOSE_FROM_CLIENT

                val err = cq.runBatch(this@NativeClientCall, op.ptr, 1u)
                if (err != grpc_call_error.GRPC_CALL_OK) {
                    // TODO: How should we handle grpc call errors?
                    error("Failed to run half close op: $err")
                }
            }
        }
    }

    override fun sendMessage(message: Request) {
        checkNotNull(listener) { "Not yet started" }
        check(!halfClosed) { "Already half closed." }
        check(!closed) { "Already closed." }

        coroutineScope.launch {
            withArena { arena ->

                val inputStream = methodDescriptor.getRequestMarshaller().stream(message)
                // TODO: handle non-byte buffer InputStream sources
                val byteBuffer = (inputStream.source as Buffer).toGrpcByteBuffer()

                val op = arena.alloc<grpc_op> {
                    op = GRPC_OP_SEND_MESSAGE
                    data.send_message.send_message = byteBuffer
                }

                val err = cq.runBatch(this@NativeClientCall, op.ptr, 1u)
                if (err != grpc_call_error.GRPC_CALL_OK) {
                    // TODO: How should we handle grpc call errors?
                    error("Failed to run half close op: $err")
                }

                grpc_byte_buffer_destroy(byteBuffer)
            }
        }
    }
}


