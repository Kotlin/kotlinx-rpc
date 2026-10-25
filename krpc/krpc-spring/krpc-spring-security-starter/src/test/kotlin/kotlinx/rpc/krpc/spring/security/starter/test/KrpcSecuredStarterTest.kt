/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.security.starter.test

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.krpc.spring.security.SecurityContextEnricher
import kotlinx.rpc.krpc.spring.security.starter.KrpcSecurityAutoConfiguration
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KrpcSecuredStarterTest {
    @Test
    fun `Boot imports the security auto-configuration and registers one enricher`() {
        SecuredStarterServer().use { server ->
            assertEquals(1, server.context.getBeansOfType(KrpcSecurityAutoConfiguration::class.java).size)
            assertEquals(
                setOf("securityContextEnricher"),
                server.context.getBeansOfType(SecurityContextEnricher::class.java).keys,
            )
        }
    }

    @Test
    fun `propagates the user authentication through a dispatcher switch`() = runTest {
        SecuredStarterServer().use { server ->
            val client = server.rpcClient("user", "user-password")
            try {
                val service = client.withService<SecuredService>()

                assertEquals("user", withRpcTimeout { service.currentUser() })
                assertEquals("user", withRpcTimeout { service.currentUserAfterDispatcherSwitch() })
            } finally {
                client.close()
            }
        }
    }

    @Test
    fun `allows an admin call for an admin`() = runTest {
        SecuredStarterServer().use { server ->
            val client = server.rpcClient("admin", "admin-password")
            try {
                val service = client.withService<SecuredService>()

                assertEquals("admin", withRpcTimeout { service.adminOnly() })
            } finally {
                client.close()
            }
        }
    }

    @Test
    fun `denies a user an admin call without closing the connection`() = runTest {
        SecuredStarterServer().use { server ->
            val client = server.rpcClient("user", "user-password")
            try {
                val service = client.withService<SecuredService>()

                val failure = assertFailsWith<Throwable> { withRpcTimeout { service.adminOnly() } }
                assertFalse(failure.isCancellation())
                assertTrue(failure.isAccessDenied())
                assertEquals("user", withRpcTimeout { service.currentUser() })
            } finally {
                client.close()
            }
        }
    }

    @Test
    fun `rejects a WebSocket connection without credentials`() = runTest {
        SecuredStarterServer().use { server ->
            val client = server.rpcClient()
            try {
                val service = client.withService<SecuredService>()

                val failure = assertFailsWith<Throwable> { withRpcTimeout { service.currentUser() } }
                assertFalse(failure.isCancellation())
                assertTrue(failure.isUnauthorizedHandshakeFailure())
            } finally {
                client.close()
            }
        }
    }

    @Test
    fun `keeps the principal isolated across concurrent authenticated connections`() = runTest {
        SecuredStarterServer().use { server ->
            val userClient = server.rpcClient("user", "user-password")
            val adminClient = server.rpcClient("admin", "admin-password")
            try {
                val userService = userClient.withService<SecuredService>()
                val adminService = adminClient.withService<SecuredService>()

                val names = withRpcTimeout {
                    coroutineScope {
                        val calls = List(REPETITIONS) { index ->
                            if (index % 2 == 0) {
                                async { "user" to userService.currentUserAfterDispatcherSwitch() }
                            } else {
                                async { "admin" to adminService.currentUserAfterDispatcherSwitch() }
                            }
                        }
                        calls.map { it.await() }
                    }
                }

                names.forEach { (expected, actual) -> assertEquals(expected, actual) }
            } finally {
                try {
                    userClient.close()
                } finally {
                    adminClient.close()
                }
            }
        }
    }

    @Test
    fun `the security enabled property set to false leaves calls without authentication`() = runTest {
        SecuredStarterServer("--kotlinx.rpc.krpc.server.security.enabled=false").use { server ->
            assertTrue(server.context.getBeansOfType(SecurityContextEnricher::class.java).isEmpty())

            val client = server.rpcClient("user", "user-password")
            try {
                val service = client.withService<SecuredService>()

                assertEquals("anonymous", withRpcTimeout { service.currentUser() })
            } finally {
                client.close()
            }
        }
    }

    private companion object {
        const val REPETITIONS = 20
    }
}
