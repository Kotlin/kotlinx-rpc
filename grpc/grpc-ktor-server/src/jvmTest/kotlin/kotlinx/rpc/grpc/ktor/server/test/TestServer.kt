/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.ktor.server.test

import io.ktor.server.testing.testApplication
import kotlinx.rpc.grpc.GrpcClient
import kotlin.test.Test
import kotlinx.rpc.grpc.ktor.server.grpc
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes

class KtorTestServiceImpl : KtorTestService {
    override suspend fun sayHello(message: Hello): Hello {
        return message
    }
}

const val PORT = 8085

class TestServer {
    @Test
    fun testPlainRequests() = testApplication {
        application {
            grpc(PORT) {
                registerService<KtorTestService> { KtorTestServiceImpl() }
            }
        }

        startApplication()

        val client = GrpcClient("localhost", PORT) {
            usePlaintext()
        }

        val response = client.withService<KtorTestService>().sayHello(Hello { message = "Hello" })
        assertEquals("Hello", response.message, "Wrong response message")

        client.shutdown()
        client.awaitTermination(1.minutes)
    }
}
