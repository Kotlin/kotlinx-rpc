/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc

import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.concurrent.Executors
import kotlin.random.Random
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

internal class KrpcSendHandlerStressTest : KrpcSendHandlerBaseTest() {
    @Test
    fun stressNoWindow() = runTest { channel, handler ->
        executorDispatcher().use { dispatcher ->
            repeat(100_000) {
                launch(dispatcher) {
                    handler.sendMessage("Hello".asMessage())
                }
            }

            launch {
                repeat(100_000) {
                    channel.receive()
                }
            }.join()
        }
    }

    @Test
    fun stressOpenWindow() = runTest { channel, handler ->
        executorDispatcher().use { dispatcher ->
            handler.updateWindowSize(100_000)

            repeat(100_000) {
                launch(dispatcher) {
                    handler.sendMessage("Hello".asMessage())
                }
            }

            launch {
                repeat(100_000) {
                    channel.receive()
                }
            }.join()
        }
    }

    @Test
    fun stressOpenWindowWithWait() = runTest { channel, handler ->
        executorDispatcher().use { dispatcher ->
            handler.updateWindowSize(0)

            repeat(100_000) {
                launch(dispatcher) {
                    handler.sendMessage("Hello".asMessage())
                }
            }

            val collector = launch {
                repeat(100_000) {
                    channel.receive()
                }
            }

            handler.awaitSenders(100_000)

            handler.updateWindowSize(100_000)

            collector.join()
        }
    }

    @Test
    fun stressRandomWindow() = runTest(timeout = 30.seconds) { channel, handler ->
        executorDispatcher().use { dispatcher ->
            handler.updateWindowSize(0)

            repeat(100_000) {
                launch(dispatcher) {
                    handler.sendMessage("Hello".asMessage())
                }
            }

            val collector = launch {
                repeat(100_000) {
                    channel.receive()
                }
            }

            handler.awaitSenders(100_000)

            var processed = 0
            val random = Random(Instant.now().toEpochMilli())
            while (processed < 100_000) {
                val nextUpdate = random.nextInt(500, 2000)
                handler.updateWindowSize(nextUpdate)
                handler.awaitSenders((100_000 - processed - nextUpdate).coerceAtLeast(0))
                println("Processed $processed")
                processed += nextUpdate
            }

            collector.join()
        }
    }

    private fun executorDispatcher(): ExecutorCoroutineDispatcher = Executors
        .newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2)
        .asCoroutineDispatcher()
}
