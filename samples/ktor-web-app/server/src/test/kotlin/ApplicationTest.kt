/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.server.testing.*
import kotlinx.coroutines.flow.toList
import org.jetbrains.krpc.client.withService
import org.jetbrains.krpc.serialization.json
import org.jetbrains.krpc.transport.ktor.client.rpc
import org.jetbrains.krpc.transport.ktor.client.rpcConfig
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        val service = createClient {
            install(WebSockets)
        }.rpc("/api") {
            rpcConfig {
                serialization {
                    json()
                }
            }
        }.withService<MyService>()

        assertEquals(
            expected = "Nice to meet you Alex, how is it in address1?",
            actual = service.hello("Alex", UserData("address1", "last")),
        )

        assertEquals(
            expected = List(10) { "Article number $it" },
            actual = service.subscribeToNews().toList(),
        )
    }
}
