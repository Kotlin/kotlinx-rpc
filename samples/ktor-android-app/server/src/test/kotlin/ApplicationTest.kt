/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import com.example.common.MyService
import com.example.common.UserData
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
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

    @Test
    fun testRoot2() = testApplication {
        application {
            routing {
                get("/") {
                    call.respondText("Hello World!")
                }
            }
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }
}
