/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.coroutines.*
import org.jetbrains.krpc.client.awaitFieldInitialization
import org.jetbrains.krpc.client.withService
import org.jetbrains.krpc.serialization.json
import org.jetbrains.krpc.transport.ktor.client.rpc
import org.jetbrains.krpc.transport.ktor.client.rpcConfig
import org.junit.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ApplicationTest {
    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testRecognizer() = testApplication {
        application {
            install(io.ktor.server.websocket.WebSockets)
            appRouting()
        }

        val rpcClient = createClient {
            install(WebSockets)
        }.rpc("/image-recognizer") {
            rpcConfig {
                serialization {
                    json()
                }
            }
        }

        val recognizer = rpcClient.withService<ImageRecognizer>()

        val flowList = mutableListOf<String?>()

        assertEquals(null, recognizer.awaitFieldInitialization { currentlyProcessedImage }.value)

        val job = CoroutineScope(Dispatchers.IO).launch {
            recognizer.currentlyProcessedImage.collect { image ->
                flowList.add(image?.data?.toHexString())
            }
        }

        assertEquals(Category.DOG, recognizer.recognize(Image(byteArrayOf(1, 2, 3))))
        assertEquals(Category.CAT, recognizer.recognize(Image(byteArrayOf(0, 2, 3))))

        job.cancelAndJoin()

        println(flowList)
        assertContentEquals(listOf(null, "010203", null, "000203", null), flowList)
    }
}
