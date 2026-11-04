/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.security.test

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class KrpcSpringSecurityTest {
    @Test
    fun `rejects a WebSocket connection without credentials`() = runTest {
        SecuredKrpcSpringServer().use { server ->
            val service = server.rpcClient().withService<SecuredService>()

            assertFails { server.withRpcTimeout { service.currentUser() } }
        }
    }

    @Test
    fun `rejects a WebSocket connection with an invalid password`() = runTest {
        SecuredKrpcSpringServer().use { server ->
            val service = server.rpcClient("user", "wrong-password").withService<SecuredService>()

            assertFails { server.withRpcTimeout { service.currentUser() } }
        }
    }

    @Test
    fun `propagates the user authentication through a dispatcher switch`() = runTest {
        SecuredKrpcSpringServer().use { server ->
            val client = server.rpcClient("user", "user-password")
            val service = client.withService<SecuredService>()

            assertEquals("user", server.withRpcTimeout { service.currentUser() })
            assertEquals("user", server.withRpcTimeout { service.currentUserAfterDispatcherSwitch() })
            assertEquals("user", server.withRpcTimeout { service.userOnly() })

            client.close()
        }
    }

    @Test
    fun `denies a user admin calls without closing the connection`() = runTest {
        SecuredKrpcSpringServer().use { server ->
            val client = server.rpcClient("user", "user-password")
            val service = client.withService<SecuredService>()

            assertFails { server.withRpcTimeout { service.adminOnly() } }
            assertEquals("user", server.withRpcTimeout { service.currentUser() })

            client.close()
        }
    }

    @Test
    fun `allows admin unary and streaming calls`() = runTest {
        SecuredKrpcSpringServer().use { server ->
            val client = server.rpcClient("admin", "admin-password")
            val service = client.withService<SecuredService>()

            assertEquals("admin", server.withRpcTimeout { service.adminOnly() })
            assertEquals(listOf("admin"), server.withRpcTimeout { service.adminStream().toList() })

            client.close()
        }
    }

    @Test
    fun `denies a user streaming call`() = runTest {
        SecuredKrpcSpringServer().use { server ->
            val client = server.rpcClient("user", "user-password")
            val service = client.withService<SecuredService>()

            assertFails { server.withRpcTimeout { service.adminStream().toList() } }

            client.close()
        }
    }

    @Test
    fun `keeps the principal isolated across concurrent authenticated connections`() = runTest {
        SecuredKrpcSpringServer().use { server ->
            val userClient = server.rpcClient("user", "user-password")
            val adminClient = server.rpcClient("admin", "admin-password")
            val userService = userClient.withService<SecuredService>()
            val adminService = adminClient.withService<SecuredService>()

            val names = server.withRpcTimeout {
                coroutineScope {
                    val user = async { userService.currentUserAfterDispatcherSwitch() }
                    val admin = async { adminService.currentUserAfterDispatcherSwitch() }
                    listOf(user.await(), admin.await())
                }
            }

            assertEquals(listOf("user", "admin"), names)

            userClient.close()
            adminClient.close()
        }
    }
}
