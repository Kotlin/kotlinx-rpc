/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.test.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.ExperimentalSerializationApi
import org.jetbrains.krpc.client.withService
import org.jetbrains.krpc.internal.hex.hexToByteArrayInternal
import org.jetbrains.krpc.internal.logging.DumpLogger
import org.jetbrains.krpc.internal.transport.RPCConnector
import org.jetbrains.krpc.registerService
import org.jetbrains.krpc.rpcClientConfig
import org.jetbrains.krpc.rpcServerConfig
import org.jetbrains.krpc.serialization.RPCSerialFormatConfiguration
import org.jetbrains.krpc.serialization.json
import org.jetbrains.krpc.serialization.protobuf
import org.jetbrains.krpc.test.LocalTransport
import org.jetbrains.krpc.test.api.util.*

@Suppress("RedundantUnitReturnType")
fun wireSamplingTest(name: String, test: suspend WireSamplingTestScope.() -> Unit): TestResult {
    return runTest {
        WireSamplingTestScope(name, this).test()
    }
}

class WireSamplingTestScope(private val sampleName: String, scope: TestScope) : CoroutineScope by scope {
    private var samplingAdded: Boolean = false

    suspend fun sample(
        vararg formats: SamplingFormat = SamplingFormat.ALL,
        block: suspend SamplingService.() -> Unit,
    ) {
        if (samplingAdded) {
            error("Please, add only one sampling per test")
        }
        samplingAdded = true

        val fails = mutableListOf<String>()
        formats.forEach { format ->
            val finishedToolkit = SamplingToolkit(this, format).apply {
                server // init server

                service.block()

                transport.cancel()
            }

            val log = checkGold(
                fileDir = CURRENT_WIRE_DUMPS_DIR,
                filename = "${sampleName}_${format.name.lowercase()}",
                content = WireContent(finishedToolkit.logs, format.commentBinaryOutput),
                parseGoldFile = { WireContent.fromText(it) },
            )

            if (log != null) {
                fails.add("Connection sample updated for '$sampleName' sample with $format format. $log")
            }
        }

        fails.failIfAnyCauses()
    }

    companion object {
        private val CURRENT_WIRE_DUMPS_DIR = ApiVersioningTest.WIRE_DUMPS_DIR
            .resolve(ApiVersioningTest.LIBRARY_VERSION)
    }
}

private class SamplingToolkit(scope: CoroutineScope, format: SamplingFormat) {
    val transport = LocalTransport(scope)

    val client by lazy {
        SamplingClient(rpcClientConfig { serialization { format.init(this) } }, transport, dumpLogger)
    }

    val service: SamplingService by lazy {
        client.withService()
    }

    val server by lazy {
        SamplingServer(rpcServerConfig { serialization { format.init(this) } }, transport, dumpLogger).apply {
            registerService<SamplingService>(SamplingServiceImpl(transport.coroutineContext))
        }
    }

    val logs = mutableListOf<DumpLog>()

    private val dumpLogger = object : DumpLogger {
        override val isEnabled: Boolean = true

        override fun dump(vararg tags: String, message: () -> String) {
            logs.add(DumpLog(Role.fromText(tags[0]), Phase.fromText(tags[1]), message()))
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
enum class SamplingFormat(val commentBinaryOutput: Boolean, val init: RPCSerialFormatConfiguration.() -> Unit) {
    Json(false, {
        json()
    }),

    Protobuf(true, {
        protobuf()
    }),
    ;

    companion object {
        @Suppress("EnumValuesSoftDeprecate") // .entries dont work on Kotlin pre 1.8.20
        val ALL = SamplingFormat.values()
    }
}

data class DumpLog(
    val role: Role,
    val phase: Phase,
    val log: String,
)

enum class Role {
    Server, Client;

    companion object {
        fun fromText(text: String): Role {
            return text.trimStart('[').trimEnd(']').let {
                if (it == RPCConnector.SERVER_ROLE) Server else Client
            }
        }
    }
}

enum class Phase {
    Send, Receive;

    companion object {
        fun fromText(text: String): Phase {
            return text.trimStart('[').trimEnd(']').let {
                if (it == RPCConnector.SEND_PHASE) Send else Receive
            }
        }
    }
}

private class WireContent(
    rawLogs: List<DumpLog>,
    private val commentBinaryOutput: Boolean,
) : GoldComparable<WireContent> {
    // too vague to trace, sensitive to code changes that do not affect actual structure
    @Suppress("RegExpRedundantEscape")
    private val stackTraceRegExp = Regex("\"stacktrace\":\\[.*?\\]")

    private fun String.removeExceptionStackTraceData(): String {
        return replace(stackTraceRegExp, "\"stacktrace\":[<Removed>]")
    }

    private val logs = rawLogs.map {
        it.copy(log = it.log.removeExceptionStackTraceData())
    }

    private val transformed = logs.filter {
        it.phase == Phase.Send // receive phases are not needed to replay a connection
    }

    override fun compare(other: WireContent): GoldComparisonResult {
        return if (transformed == other.transformed) GoldComparisonResult.Ok else GoldComparisonResult.Failure
    }

    override fun dump(): String {
        return logs.joinToString(GoldUtils.NewLine) { dump ->
            val base = "[${dump.role}] [${dump.phase}] $ ${dump.log}"

            if (commentBinaryOutput) {
                val decodedBytes = dump.log.hexToByteArrayInternal().joinToString("") { byte ->
                    byte.toInt().toChar().display()
                }

                "// decoded: $decodedBytes" + GoldUtils.NewLine + base
            } else {
                base
            }
        }
    }

    companion object {
        fun fromText(text: String): WireContent {
            return WireContent(
                rawLogs = text.split("\n", "\r")
                    .map { it.trim() }
                    .filter { !it.startsWith("//") }
                    .map { line ->
                        val (prefix, log) = line.split("\$", limit = 2).map { it.trim() }
                        val (role, phase) = prefix.split(" ")

                        DumpLog(Role.fromText(role), Phase.fromText(phase), log)
                    },
                commentBinaryOutput = false
            )
        }
    }
}

private fun Char.display(): String {
    // visible symbols range
    // https://www.asciitable.com/
    return if (code !in 32..126) "?" else toString()
}
