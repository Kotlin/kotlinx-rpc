/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */
@file:OptIn(ExperimentalForeignApi::class, ExperimentalStdlibApi::class, ExperimentalNativeApi::class)

package kotlinx.rpc.grpc.internal


import HelloReply
import HelloReplyInternal
import HelloRequest
import HelloRequestInternal
import invoke
import kotlinx.cinterop.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.io.Buffer
import kotlinx.rpc.grpc.GrpcTrailers
import kotlinx.rpc.grpc.Status
import libgrpcpp_c.*
import kotlin.experimental.ExperimentalNativeApi
import kotlin.test.Test
import kotlin.test.fail

class GrpcCoreTest {
    val GRPC_PROPAGATE_DEFAULTS = 0x0000FFFFu

    suspend fun doCall(reqBytes: ByteArray) = withArena { arena ->
        val cq = CompletionQueue()

        val creds = grpc_insecure_credentials_create()!!
        val channel = grpc_channel_create("localhost:50051", creds, null)!!

        val method = grpc_slice_from_copied_string("/helloworld.Greeter/SayHello")

        val call = grpc_channel_create_call(
            channel, null, GRPC_PROPAGATE_DEFAULTS, cq.raw,
            method, null, gpr_inf_future(GPR_CLOCK_REALTIME), null
        )

        println("Request bytes: ${reqBytes.toHexString()}")

        // make a grpc_slice from bytes (copied buffer)
        val buf = Buffer()
        buf.write(reqBytes)
        val req_buf = buf.toGrpcByteBuffer()

        // Use a single batch (no RECV_INITIAL_METADATA to keep it minimal)
        val ops = arena.allocArray<grpc_op>(6)

        // SEND_INITIAL_METADATA
        ops[0].op = GRPC_OP_SEND_INITIAL_METADATA
        ops[0].data.send_initial_metadata.count = 0u

        // SEND_MESSAGE
        ops[1].op = GRPC_OP_SEND_MESSAGE
        ops[1].data.send_message.send_message = req_buf

        // SEND_CLOSE_FROM_CLIENT
        ops[2].op = GRPC_OP_SEND_CLOSE_FROM_CLIENT


        val meta = arena.alloc<grpc_metadata_array>()
        grpc_metadata_array_init(meta.ptr)
        ops[3].op = GRPC_OP_RECV_INITIAL_METADATA
        ops[3].data.recv_initial_metadata.recv_initial_metadata = meta.ptr

        // RECV_MESSAGE -> grpc_byte_buffer**
        val recvBufPtr = arena.alloc<CPointerVar<grpc_byte_buffer>>()
        ops[4].op = GRPC_OP_RECV_MESSAGE
        ops[4].data.recv_message.recv_message = recvBufPtr.ptr

        // RECV_STATUS_ON_CLIENT
        val statusCode = arena.alloc<grpc_status_code.Var>()
        val statusDetails = arena.alloc<grpc_slice>()
        val errorStr = arena.alloc<CPointerVar<ByteVar>>()
        ops[5].op = GRPC_OP_RECV_STATUS_ON_CLIENT
        ops[5].data.recv_status_on_client.status = statusCode.ptr
        ops[5].data.recv_status_on_client.status_details = statusDetails.ptr
        ops[5].data.recv_status_on_client.error_string = errorStr.ptr
        // trailing metadata is optional; leave it null if not used


        val err = cq.runBatch(call!!, ops, 6u)

        if (err != grpc_call_error.GRPC_CALL_OK) {
            fail("Call failed")
        }

        println("Status code: ${statusCode.value}")
        println("Error string: ${errorStr.value?.toKString()}")
        if (statusCode.value != grpc_status_code.GRPC_STATUS_OK) {
            fail("Call failed with status code ${statusCode.value}")
        }

        val messageBuffer = recvBufPtr.value!!.toKotlin()
        val message = HelloReplyInternal.CODEC.decode(messageBuffer)

        println("message: ${message.message}")

        cq.shutdown()

    }

    @Test
    fun grpcCoreDemo() = memScoped {

        grpc_init()

        // --- build protobuf HelloRequest { name = "world" } ---
        // field 1 (tag=1, wire=2) => key = 0x0A
        val name = "world".encodeToByteArray()
        val reqBytes = ByteArray(2 + name.size).apply {
            this[0] = 0x0A                   // field 1, length-delimited
            this[1] = name.size.toByte()     // length (assumes <128)
            name.copyInto(this, 2)
        }

        runBlocking {
            doCall(reqBytes)
        }
        grpc_shutdown()
    }

    @Test
    fun grpcClientTest() {
        grpc_init()

        val fullName = "/helloworld.Greeter/SayHello"
        val descriptor = methodDescriptor(
            fullMethodName = fullName,
            requestCodec = HelloRequestInternal.CODEC,
            responseCodec = HelloReplyInternal.CODEC,
            type = MethodType.UNARY,
            schemaDescriptor = Unit,
            idempotent = true,
            safe = true,
            sampledToLocalTracing = true,
        )

        val cq = CompletionQueue()

        val creds = grpc_insecure_credentials_create()!!
        val channel = grpc_channel_create("localhost:50051", creds, null)!!

        val method = grpc_slice_from_copied_string("/helloworld.Greeter/SayHello")

        val rawCall = grpc_channel_create_call(
            channel, null, GRPC_PROPAGATE_DEFAULTS, cq.raw,
            method, null, gpr_inf_future(GPR_CLOCK_REALTIME), null
        )

        val req = HelloRequest {
            name = "world"
        }

        runBlocking {
            val sem = Semaphore(1, acquiredPermits = 1)

            val call = NativeClientCall(cq, rawCall!!, descriptor, this)
            val listener = object : ClientCall.Listener<HelloReply>() {

                override fun onMessage(message: HelloReply) {
                    println("Received message: ${message.message}")
                }

                override fun onClose(status: Status, trailers: GrpcTrailers) {
                    println("Status: ${status.statusCode}")
                    println("Trailers: $trailers")
                    sem.release()
                }
            }

            println("Start call to $fullName")
            call.start(listener, GrpcTrailers())
            call.sendMessage(req)
            call.halfClose()
            call.request(1)

            sem.acquire()
            cq.shutdown()
        }
    }

}