/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.grpc.internal.bridge.GrpcByteBuffer
import kotlinx.rpc.grpc.internal.bridge.GrpcClient
import kotlinx.rpc.grpc.internal.bridge.GrpcSlice
import libgrpcpp_c.pb_decode_greeter_sayhello_response
import kotlin.native.runtime.GC
import kotlin.native.runtime.NativeRuntimeApi
import kotlin.test.Test
import kotlin.test.fail

@OptIn(ExperimentalForeignApi::class)
class BridgeTest {

    @OptIn(NativeRuntimeApi::class)
    @Test
    fun testBasicUnaryAsyncCall() = runBlocking {
        try {
            val client = GrpcClient("localhost:50051")
            val request = GrpcSlice(byteArrayOf(8, 4))
            val reqBuf = GrpcByteBuffer(request)
            val result = client.callUnary("/Greeter/SayHello", reqBuf)
            val response = result.intoSlice()
            val value = pb_decode_greeter_sayhello_response(response.cSlice)
            println("Response received: $value")
        } catch (e: Exception) {
            // trigger GC collection, otherwise there will be a leak
            GC.collect()
            fail("Got an exception: ${e.message}", e)
        }
    }

}
