/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.test.api

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.rpc.client.awaitFieldInitialization
import kotlinx.rpc.internal.transport.CancellationType
import kotlinx.rpc.internal.transport.RPCMessage
import kotlinx.rpc.internal.transport.RPCPlugin
import kotlinx.rpc.internal.transport.RPCPluginKey
import kotlinx.rpc.test.api.util.GoldUtils.NewLine
import kotlinx.rpc.test.plainFlow
import org.jetbrains.krpc.test.api.util.SamplingData
import org.junit.Test
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
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
    fun testCancellationType() {
        testEnum<CancellationType>()
    }

    @Test
    fun testEchoSampling() = wireSamplingTest("echo") {
        sample {
            val response = echo("Hello", SamplingData("data"))
            assertEquals(SamplingData("data"), response)
        }
    }

    @Test
    @Ignore("Flow sampling tests are too unstable. Ignored until better fix")
    fun testClientStreamSampling() = wireSamplingTest("clientStream") {
        sample {
            val response = clientStream(plainFlow { it }).joinToString()
            val expected = List(5) { it }.joinToString()

            assertEquals(expected, response)
        }
    }

    @Test
    @Ignore("Nested flows proved to be unstable")
    fun testClientNestedStreamSampling() = wireSamplingTest("clientNestedStream") {
        sample {
            val response = clientNestedStream(plainFlow { plainFlow { it } }).join()
            val expected = List(5) { List(5) { it } }.join()

            assertEquals(expected, response)
        }
    }

    @Test
    @Ignore("Flow sampling tests are too unstable. Ignored until better fix")
    fun testServerStreamSampling() = wireSamplingTest("serverStream") {
        sample {
            val response = serverFlow().toList().joinToString()
            val expected = List(5) { SamplingData("data") }.joinToString()

            assertEquals(expected, response)
        }
    }

    @Test
    @Ignore("Nested flows proved to be unstable")
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
    @Ignore("Flow sampling tests are too unstable. Ignored until better fix")
    fun testPlainFlowSampling() = wireSamplingTest("plainFlow") {
        sample {
            val response = plainFlow.toList().joinToString()
            val expected = List(5) { it }.joinToString()

            assertEquals(expected, response)
        }
    }

    @Test
    @Ignore("First value in stateFlow and CallSuccess for its initialization have a race")
    fun testSharedFlowSampling() = wireSamplingTest("sharedFlow") {
        sample {
            val response = sharedFlow.take(5).toList().joinToString()
            val expected = List(5) { it }.joinToString()

            assertEquals(expected, response)
        }
    }

    @Test
    @Ignore("First value in stateFlow and CallSuccess for its initialization have a race")
    fun testStateFlowSampling() = wireSamplingTest("stateFlow") {
        sample {
            val state = awaitFieldInitialization { stateFlow }
            assertEquals(-1, state.value)

            val newFlow = state.take(2)

            newFlow.collect {
                when (it) {
                    -1 -> emitNextInStateFlow(10)
                    10 -> assertEquals(10, state.value)
                    else -> error("Unexpected value in flow: $it")
                }
            }
        }
    }

    private fun List<List<*>>.join() = joinToString { "[${it.joinToString()}]" }

    companion object {
        val LIBRARY_VERSION_DIR = System.getenv("LIBRARY_VERSION")?.versionToDirName()
            ?: error("Expected LIBRARY_VERSION env variable")

        val CURRENT_CLASS_DUMPS_DIR: Path = Path("src/jvmTest/resources/class_dumps/")
            .resolve(LIBRARY_VERSION_DIR)

        val LATEST_CLASS_DUMPS_DIR: Path = Path("src/jvmTest/resources/class_dumps/")
            .latestVersionOrCurrent()

        val WIRE_DUMPS_DIR: Path = Path("src/jvmTest/resources/wire_dumps/")
        val INDEXED_ENUM_DUMPS_DIR: Path = Path("src/jvmTest/resources/indexed_enum_dumps/")

        private fun String.versionToDirName(): String {
            return replace('.', '_').replace('-', '_')
        }

        fun Path.latestVersionOrCurrent(): Path {
            return listDirectoryEntries()
                .filter { it.isDirectory() }
                .sortedWith { a, b ->
                    val aBeta = a.name.contains("beta")
                    val bBeta = b.name.contains("beta")
                    when {
                        aBeta && bBeta -> a.compareTo(b)
                        aBeta -> -1
                        bBeta -> 1
                        else -> a.compareTo(b)
                    }
                }.lastOrNull()
                ?: resolve(LIBRARY_VERSION_DIR)
        }
    }
}

fun List<String>.failIfAnyCauses() {
    if (isNotEmpty()) {
        fail(joinToString(NewLine.repeat(2), NewLine, NewLine))
    }
}
