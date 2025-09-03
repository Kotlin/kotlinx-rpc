/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test.lincheck

import ch.qos.logback.classic.Logger
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.krpc.test.BaseServiceTest
import kotlinx.rpc.krpc.test.TestLogAppender
import org.jetbrains.lincheck.Lincheck
import org.jetbrains.lincheck.datastructures.CTestConfiguration
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class LincheckTest : BaseLincheckTest() {
    @Test
    @Ignore("Has some lincheck issues, waiting for a fix from the team")
    fun simpleConcurrentRequests() = runTest { testLog ->
        launch {
            assertEquals(1, service.unary(1))
        }

        launch {
            assertEquals(2, service.unary(2))
        }

        assertTrue(testLog.warnings.isEmpty())
        assertTrue(testLog.errors.isEmpty())
    }
}

abstract class BaseLincheckTest() : BaseServiceTest() {
    fun createDispatcher(nThreads: Int): ExecutorCoroutineDispatcher = Executors
        .newFixedThreadPool(nThreads)
        .asCoroutineDispatcher()

    protected fun runTest(
        shouldFail: Boolean = false,
        invocations: Int = CTestConfiguration.DEFAULT_INVOCATIONS,
        nThreads: Int = Runtime.getRuntime().availableProcessors(),
        perCallBufferSize: Int = 100,
        block: suspend Env.(TestLogAppender) -> Unit,
    ) {
        val root = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as Logger
        val testAppender = root.getAppender("TEST") as TestLogAppender
        testAppender.events.clear()

        val result = runCatching {
            Lincheck.runConcurrentTest(invocations) {
                createDispatcher(nThreads).use { dispatcher ->
                    runBlocking(dispatcher) {
                        runServiceTest(coroutineContext, perCallBufferSize) {
                            block(testAppender)
                        }
                    }
                }
            }
        }

        testAppender.events.clear()

        if (result.isFailure != shouldFail) {
            val exceptionOrNull = result.exceptionOrNull()
            val message = if (shouldFail) {
                "Should've failed but succeeded"
            } else {
                "Should've succeeded but failed"
            }

            fail(message, exceptionOrNull)
        }
    }
}
