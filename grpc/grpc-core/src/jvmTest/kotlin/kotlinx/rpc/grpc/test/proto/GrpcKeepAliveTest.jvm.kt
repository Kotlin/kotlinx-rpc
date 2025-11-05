/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.proto

import kotlinx.rpc.grpc.client.internal.ManagedChannel
import kotlinx.rpc.grpc.test.EchoRequest
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.withService
import java.lang.reflect.Field
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

actual fun GrpcProtoTest.testKeepAlive(
    time: Duration,
    timeout: Duration,
    withoutCalls: Boolean
) {
    runGrpcTest(
        configure = {
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

private inline fun <reified R> Any.getField(vararg names: String): R {
    var curr: Any = this
    for (name in names) {
        val field = findFieldInHierarchy(curr::class.java, name)
            ?: throw NoSuchFieldException("Field '$name' not found in ${curr::class.java}")
        field.isAccessible = true
        curr = field.get(curr) as Any
    }
    return curr as R
}

private fun findFieldInHierarchy(clazz: Class<*>, name: String): Field? {
    var c: Class<*>? = clazz
    while (c != null) {
        try {
            return c.getDeclaredField(name)
        } catch (_: NoSuchFieldException) {
            c = c.superclass
        }
    }
    return null
}