/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlinx.coroutines.runBlocking
import kotlinx.rpc.grpc.client.internal.ManagedChannel
import kotlinx.rpc.grpc.server.GrpcServer
import kotlinx.rpc.grpc.server.internal.ServerBuilder
import kotlinx.rpc.grpc.test.EchoRequest
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.withService
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

actual fun GrpcTestBase.testKeepAlive(
    time: Duration,
    timeout: Duration,
    withoutCalls: Boolean
) {
    runGrpcTest(
        clientConfiguration = {
            keepAlive {
                this.time = time
                this.timeout = timeout
                this.withoutCalls = withoutCalls
            }
        }
    ) {
        it.withService<EchoService>().UnaryEcho(EchoRequest { message = "Hello" })
        val nettyClientTransport = it.getField<ManagedChannel>("channel")
            .platformApi
            .getField<HashSet<Any>>("delegate", "subchannels")
            .first()
            .getField<List<Any>>("transports").first()
            .getField<Any>("delegate", "delegate")

        val keepAliveTime = nettyClientTransport.getField<Long>("keepAliveTimeNanos").nanoseconds
        val keepAliveTimeout = nettyClientTransport.getField<Long>("keepAliveTimeoutNanos").nanoseconds
        val keepAliveWithoutCalls = nettyClientTransport.getField<Boolean>("keepAliveWithoutCalls")

        assertEquals(time, keepAliveTime)
        assertEquals(timeout, keepAliveTimeout)
        assertEquals(withoutCalls, keepAliveWithoutCalls)
    }
}

actual fun GrpcTestBase.testServerKeepAlive(
    time: Duration,
    timeout: Duration,
) = runBlocking {
    val server = GrpcServer(0) {
        keepAlive {
            this.time = time
            this.timeout = timeout
        }
    }

    try {
        val builder = server.getField<ServerBuilder<*>>("serverBuilder")
        assertEquals(time, builder.getField<Long>("keepAliveTimeInNanos").nanoseconds)
        assertEquals(timeout, builder.getField<Long>("keepAliveTimeoutInNanos").nanoseconds)
    } finally {
        server.shutdownNow()
        server.awaitTermination(30.seconds)
    }
}
