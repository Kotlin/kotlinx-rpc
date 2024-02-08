/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.test.api

import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import org.jetbrains.krpc.client.awaitFieldInitialization
import org.jetbrains.krpc.internal.transport.RPCMessage
import org.jetbrains.krpc.internal.transport.RPCPlugin
import org.jetbrains.krpc.internal.transport.RPCPluginKey
import org.jetbrains.krpc.test.api.util.GoldUtils.NewLine
import org.jetbrains.krpc.test.api.util.SamplingData
import org.jetbrains.krpc.test.plainFlow
import org.junit.Test
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.test.Ignore
import kotlin.test.assertEquals
import kotlin.test.fail

class ApiVersioningTest {
    @Test
    fun testProtocolApiVersion() {
        val context = checkProtocolApi<RPCMessage>()

        context.fails.failIfAnyCauses()
    }

    @Test
    fun testRPCPluginEnum() {
        testEnum<RPCPlugin>()
    }

    @Test
    fun testRPCPluginKeyEnum() {
        testEnum<RPCPluginKey>()
    }

    @Test
    fun testEchoSampling() = wireSamplingTest("echo") {
        sample {
            val response = echo("Hello", SamplingData("data"))
            assertEquals(SamplingData("data"), response)
        }
    }

    @Test
    fun testClientStreamSampling() = wireSamplingTest("clientStream") {
        sample {
            val response = clientStream(plainFlow { it }).joinToString()
            val expected = List(5) { it }.joinToString()

            assertEquals(expected, response)
        }
    }

    @Test
    @Ignore
    fun testClientNestedStreamSampling() = wireSamplingTest("clientNestedStream") {
        sample {
            val response = clientNestedStream(plainFlow { plainFlow { it } }).join()
            val expected = List(5) { List(5) { it } }.join()

            assertEquals(expected, response)
        }
    }

    @Test
    fun testServerStreamSampling() = wireSamplingTest("serverStream") {
        sample {
            val response = serverFlow().toList().joinToString()
            val expected = List(5) { SamplingData("data") }.joinToString()

            assertEquals(expected, response)
        }
    }

    @Test
    fun testServerNestedStreamSampling() = wireSamplingTest("serverNestedStream") {
        sample {
            val response = serverNestedFlow().map { it.toList() }.toList().join()
            val expected = List(5) { List(5) { it } }.join()

            assertEquals(expected, response)
        }
    }

    @Test
    fun testCallExceptionSampling() = wireSamplingTest("callException") {
        // ignore protobuf here, as it's hard to properly sample stacktrace
        // in Json we can just cut it out
        sample(SamplingFormat.Json) {
            try {
                callException()
                fail("Expected exception to be thrown")
            } catch (e: IllegalStateException) {
                assertEquals("Server exception", e.message)
            }
        }
    }

    @Test
    fun testPlainFlowSampling() = wireSamplingTest("plainFlow") {
        sample {
            val response = plainFlow.toList().joinToString()
            val expected = List(5) { it }.joinToString()

            assertEquals(expected, response)
        }
    }

    @Test
    fun testSharedFlowSampling() = wireSamplingTest("sharedFlow") {
        sample {
            val response = sharedFlow.take(5).toList().joinToString()
            val expected = List(5) { it }.joinToString()

            assertEquals(expected, response)
        }
    }

    @Test
    fun testStateFlowSampling() = wireSamplingTest("stateFlow") {
        sample {
            val state = awaitFieldInitialization { stateFlow }
            assertEquals(-1, state.value)

            val newFlow = state.drop(1).take(1)
            emitNextInStateFlow(10)
            newFlow.collect {
                assertEquals(10, it)
                assertEquals(10, state.value)
            }
        }
    }

    private fun List<List<*>>.join() = joinToString { "[${it.joinToString()}]" }

    companion object {
        val LIBRARY_VERSION_DIR = System.getenv("LIBRARY_VERSION")?.versionToDirName()
            ?: error("Expected LIBRARY_VERSION env variable")

        val CLASS_DUMPS_DIR: Path = Path("src/jvmTest/resources/class_dumps/$LIBRARY_VERSION_DIR")
        val WIRE_DUMPS_DIR: Path = Path("src/jvmTest/resources/wire_dumps/")
        val INDEXED_ENUM_DUMPS_DIR: Path = Path("src/jvmTest/resources/indexed_enum_dumps/")

        private fun String.versionToDirName(): String {
            return replace('.', '_').replace('-', '_')
        }
    }
}

fun List<String>.failIfAnyCauses() {
    if (isNotEmpty()) {
        fail(joinToString(NewLine.repeat(2), NewLine, NewLine))
    }
}
