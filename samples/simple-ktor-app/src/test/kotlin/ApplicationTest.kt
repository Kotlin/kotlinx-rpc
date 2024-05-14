/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import io.ktor.server.testing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.rpc.client.awaitFieldInitialization
import kotlinx.rpc.client.withService
import kotlinx.rpc.serialization.json
import kotlinx.rpc.transport.ktor.client.installRPC
import kotlinx.rpc.transport.ktor.client.rpc
import kotlinx.rpc.transport.ktor.client.rpcConfig
import org.junit.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ApplicationTest {
    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testRecognizer() = testApplication {
        application {
            module()
        }

        val rpcClient = createClient {
            installRPC()
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
                val stringValue = image?.data?.toHexString()
                flowList.add(stringValue)

                if (stringValue == "000203") {
                    coroutineContext.cancel()
                }
            }
        }

        assertEquals(Category.DOG, recognizer.recognize(Image(byteArrayOf(1, 2, 3))))
        assertEquals(Category.CAT, recognizer.recognize(Image(byteArrayOf(0, 2, 3))))

        job.join()

        assertContentEquals(listOf(null, "010203", null, "000203"), flowList)
    }
}
