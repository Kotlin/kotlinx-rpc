/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.testing.harness

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.rpc.grpc.server.testing.AWAIT_TIMEOUT
import kotlinx.rpc.test.EventLog
import kotlinx.rpc.test.runThreadIfPossible
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.time.Duration.Companion.milliseconds

class EventLogTest {
    @Test
    fun awaitReturnsAnEventAlreadyInTheLog() = runTest {
        withContext(Dispatchers.Default) {
            val log = EventLog<String>()
            log.add("ignored")
            log.add("ready")

            assertEquals("ready", log.await(AWAIT_TIMEOUT) { it == "ready" })
        }
    }

    @Test
    fun awaitReturnsAnEventAddedByAnotherThread() = runTest {
        withContext(Dispatchers.Default) {
            val log = EventLog<String>()
            val event = async(start = CoroutineStart.UNDISPATCHED) {
                log.await(AWAIT_TIMEOUT) { it == "ready" }
            }
            assertFalse(event.isCompleted)

            runThreadIfPossible { log.add("ready") }

            assertEquals("ready", event.await())
        }
    }

    @Test
    fun awaitWithCountTwoReturnsTheSecondMatch() = runTest {
        withContext(Dispatchers.Default) {
            val log = EventLog<String>()
            log.add("match one")
            log.add("ignored")
            log.add("match two")
            log.add("match three")

            val event = log.await(AWAIT_TIMEOUT, count = 2) { it.startsWith("match") }

            assertEquals("match two", event)
        }
    }

    @Test
    fun awaitTimesOutAndIncludesTheRecordedEvents() = runTest {
        withContext(Dispatchers.Default) {
            val log = EventLog<String>()
            log.add("recorded")

            val failure = assertFailsWith<AssertionError> {
                log.await(1.milliseconds) { it == "missing" }
            }

            assertContains(failure.message.orEmpty(), "recorded")
        }
    }
}
