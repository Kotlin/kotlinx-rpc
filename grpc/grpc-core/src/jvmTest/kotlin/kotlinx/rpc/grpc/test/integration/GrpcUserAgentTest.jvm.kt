/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlinx.rpc.grpc.client.internal.ManagedChannel
import kotlinx.rpc.grpc.test.EchoRequest
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.withService
import java.lang.reflect.Field
import kotlin.test.assertTrue

actual fun GrpcTestBase.testUserAgent(
    userAgent: String,
) {
    runGrpcTest(
        clientConfiguration = {
            this.userAgent = userAgent
        }
    ) {
        it.withService<EchoService>().unaryEcho(EchoRequest { message = "Hello" })

        // grpc-java stores the composed user-agent ("<prefix> grpc-java-<transport>/<version>")
        // on ManagedChannelImpl, reachable through the forwarding wrapper's `delegate`.
        val composedUserAgent = it.getField<ManagedChannel>("channel")
            .platformApi
            .getField<String>("delegate", "userAgent")

        assertTrue(
            composedUserAgent.startsWith(userAgent),
            "Expected composed user-agent to start with '$userAgent', but was '$composedUserAgent'",
        )
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
