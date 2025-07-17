/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.coroutines.runBlocking
import kotlinx.rpc.grpc.internal.bridge.GrpcByteBuffer
import kotlinx.rpc.grpc.internal.bridge.GrpcClient
import kotlinx.rpc.grpc.internal.bridge.GrpcSlice
import libgrpcpp_c.pb_decode_greeter_sayhello_response
import kotlin.test.Test

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
class BridgeTest {

    @Test
    fun testBasicUnaryAsyncCall() {
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
