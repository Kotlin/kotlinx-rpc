/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.coroutines.runBlocking
import kotlinx.rpc.grpc.bridge.GrpcByteBuffer
import kotlinx.rpc.grpc.bridge.GrpcClient
import kotlinx.rpc.grpc.bridge.GrpcSlice
import kotlin.test.Test
import libgrpcpp_c.*

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
class BridgeTest {

    @Test
    fun `test basic unary async call`() {
        runBlocking {
            GrpcClient("localhost:50051").use { client ->
                GrpcSlice(byteArrayOf(8, 4)).use { request ->
                    GrpcByteBuffer(request).use { req_buf ->
                        client.callUnary("/Greeter/SayHello", req_buf)
                            .use { result ->
                                result.intoSlice().use { response ->
                                    val value = pb_decode_greeter_sayhello_response(response.cSlice)
                                    println("Response received: $value")
                                }

                            }
                    }
                }
        }
        }
    }
}
