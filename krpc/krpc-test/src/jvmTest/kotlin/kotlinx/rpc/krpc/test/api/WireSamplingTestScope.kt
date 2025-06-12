/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.internal.utils.hex.rpcInternalHexToReadableBinary
import kotlinx.rpc.internal.utils.map.RpcInternalConcurrentHashMap
import kotlinx.rpc.krpc.KrpcTransportMessage
import kotlinx.rpc.krpc.client.KrpcClient
import kotlinx.rpc.krpc.internal.logging.RpcInternalCommonLogger
import kotlinx.rpc.krpc.internal.logging.RpcInternalDumpLogger
import kotlinx.rpc.krpc.internal.logging.RpcInternalDumpLoggerContainer
import kotlinx.rpc.krpc.rpcClientConfig
import kotlinx.rpc.krpc.rpcServerConfig
import kotlinx.rpc.krpc.serialization.KrpcSerialFormatConfiguration
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.krpc.serialization.protobuf.protobuf
import kotlinx.rpc.krpc.test.KrpcTestClient
import kotlinx.rpc.krpc.test.KrpcTestServer
import kotlinx.rpc.krpc.test.LocalTransport
import kotlinx.rpc.krpc.test.api.ApiVersioningTest.Companion.latestVersionOrCurrent
import kotlinx.rpc.krpc.test.api.util.GoldComparable
import kotlinx.rpc.krpc.test.api.util.GoldComparisonResult
import kotlinx.rpc.krpc.test.api.util.GoldUtils
import kotlinx.rpc.krpc.test.api.util.checkGold
import kotlinx.rpc.krpc.test.debugCoroutines
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlinx.serialization.ExperimentalSerializationApi
import org.jetbrains.krpc.test.api.util.SamplingService
import org.jetbrains.krpc.test.api.util.SamplingServiceImpl
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.time.Duration.Companion.seconds

@Suppress("RedundantUnitReturnType")
fun wireSamplingTest(name: String, sampling: suspend WireSamplingTestScope.() -> Unit): TestResult {
    return runTest(timeout = 15.seconds) {
        debugCoroutines()

        WireSamplingTestScope(name, this).apply {
            sampling()

            runSimulatorTests()
        }
    }
}

class WireSamplingTestScope(private val sampleName: String, scope: TestScope) : CoroutineScope by scope {
    private val logger = RpcInternalCommonLogger.logger("[WireTest] [$sampleName]")
    private var clientSampling: (suspend SamplingService.() -> Unit)? = null

    suspend fun sample(
        vararg formats: SamplingFormat = SamplingFormat.ALL,
        block: suspend SamplingService.() -> Unit,
    ) {
        if (clientSampling != null) {
            error("Please, add only one sampling per test")
        }
        clientSampling = block

        val fails = mutableListOf<String>()
        formats.forEach { format ->
            val finishedToolkit = WireToolkit(this, format).apply {
                server // init server

                service.block()

                @Suppress("UNCHECKED_CAST")
                val requests = KrpcClient::class.memberProperties
                    .single { it.name == "requestChannels" }
                    .apply {
                        isAccessible = true
                    }
                    .get(client) as RpcInternalConcurrentHashMap<String, Channel<Any?>>

                while (requests.values.isNotEmpty()) {
                    delay(100)
                }

                stop()
            }

            val log = checkGold(
                latestDir = LATEST_WIRE_DUMPS_DIR,
                currentDir = CURRENT_WIRE_DUMPS_DIR,
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

    var skipOldServerTests: Boolean = true

    suspend fun runSimulatorTests() {
        ApiVersioningTest.WIRE_DUMPS_DIR.listFiles().forEach { dir ->
            runTestForVersionDirectory(dir)
        }
    }

    private suspend fun runTestForVersionDirectory(dir: Path) {
        dir.listFiles().filter {
            it.name.run {
                startsWith(sampleName) && endsWith(GoldUtils.GOLD_EXTENSION)
            }
        }.forEach { path ->
            val file = path.toFile()
            val formatName = file.name
                .removePrefix("${sampleName}_")
                .removeSuffix(".${GoldUtils.GOLD_EXTENSION}")

            val format = SamplingFormat.valueOf(formatName.replaceFirstChar { it.uppercaseChar() })
            runTestOnSample(dir.name, format, DumpLog.fromText(file.readLines(Charsets.UTF_8)))
        }
    }

    private suspend fun runTestOnSample(version: String, format: SamplingFormat, dump: List<DumpLog>) {
        runOldClientTest(version, format, dump)

        if (!skipOldServerTests || version == ApiVersioningTest.LIBRARY_VERSION_DIR) {
            runOldServerTest(version, format, dump)
        }
    }

    private suspend fun runOldClientTest(version: String, format: SamplingFormat, dump: List<DumpLog>) {
        val oldClientToolkit = WireToolkit(this, format, logger)
        logger.info { "Running wire test: old client (version: $version) with current server on $format format" }

        oldClientToolkit.server // init server
        for ((role, _, message) in dump.filter { it.phase == Phase.Send }) {
            when (role) {
                Role.Client -> {
                    oldClientToolkit.transport.client.send(message.toTransportMessage(format))
                }

                Role.Server -> {
                    // wait and discard message as we can not make safe assessments on it's contents
                    oldClientToolkit.transport.client.receive()
                }
            }
        }

        // if logs are over, and we've reached this line, test is considered successful
        oldClientToolkit.stop()
    }

    private suspend fun runOldServerTest(version: String, format: SamplingFormat, dump: List<DumpLog>) {
        val oldServerToolkit = WireToolkit(this, format, logger)
        logger.info { "Running wire test: old server (version: $version) with current client on $format format" }

        val clientJob = oldServerToolkit.transport.launch {
            val runClient = clientSampling
                ?: error("Client sampling is absent for $sampleName test")

            oldServerToolkit.service.runClient()
        }

        for ((role, _, message) in dump.filter { it.phase == Phase.Send }) {
            when (role) {
                Role.Client -> {
                    // wait and discard message as we can not make safe assessments on it's contents
                    oldServerToolkit.transport.server.receive()
                }

                Role.Server -> {
                    oldServerToolkit.transport.server.send(message.toTransportMessage(format))
                }
            }
        }
        clientJob.join()
        // if logs are over, and we've reached this line, test is considered successful
        oldServerToolkit.stop()
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun String.toTransportMessage(format: SamplingFormat): KrpcTransportMessage {
        return when (format) {
            SamplingFormat.Json -> KrpcTransportMessage.StringMessage(this)
            SamplingFormat.Protobuf -> KrpcTransportMessage.BinaryMessage(hexToByteArray())
        }
    }

    private fun Path.listFiles(): List<Path> {
        return Files.newDirectoryStream(this).use { it.toList() }
    }

    companion object {
        private val CURRENT_WIRE_DUMPS_DIR = ApiVersioningTest.WIRE_DUMPS_DIR
            .resolve(ApiVersioningTest.LIBRARY_VERSION_DIR)

        private val LATEST_WIRE_DUMPS_DIR = ApiVersioningTest.WIRE_DUMPS_DIR
            .latestVersionOrCurrent()
    }
}

private class WireToolkit(scope: CoroutineScope, format: SamplingFormat, val logger: RpcInternalCommonLogger? = null) {
    val transport = LocalTransport(scope)

    val client by lazy {
        KrpcTestClient(rpcClientConfig {
            serialization {
                format.init(this)
            }
        }, transport.client)
    }

    val service: SamplingService by lazy {
        client.withService<SamplingService>().withConsistentServiceId()
    }

    val server by lazy {
        KrpcTestServer(rpcServerConfig { serialization { format.init(this) } }, transport.server).apply {
            registerService<SamplingService> { SamplingServiceImpl() }
        }
    }

    val logs = mutableListOf<DumpLog>()

    val dumpLogger = object : RpcInternalDumpLogger {
        override val isEnabled: Boolean = true

        override fun dump(vararg tags: String, message: () -> String) {
            if (logger != null) {
                val log = when (format) {
                    SamplingFormat.Json -> message()
                    SamplingFormat.Protobuf -> message().rpcInternalHexToReadableBinary()
                }

                logger.info { "DumpLog: ${tags.joinToString(" ") { "[$it]" }} $log" }
            } else {
                logs.add(DumpLog(Role.fromText(tags[0]), Phase.fromText(tags[1]), message()))
            }
        }
    }

    suspend fun stop() {
        transport.coroutineContext.job.cancelAndJoin()
        RpcInternalDumpLoggerContainer.set(null)
    }

    init {
        RpcInternalDumpLoggerContainer.set(dumpLogger)
    }

    private inline fun <@Rpc reified Service : Any> Service.withConsistentServiceId(): Service = apply {
        val clazz = this::class.java
        val prop = clazz
            .declaredFields
            .single { it.name == "__rpc_stub_id" }
            .apply { isAccessible = true }

        prop.set(this, 1L)
    }
}

@OptIn(ExperimentalSerializationApi::class)
enum class SamplingFormat(val commentBinaryOutput: Boolean, val init: KrpcSerialFormatConfiguration.() -> Unit) {
    Json(false, {
        json()
    }),

    Protobuf(true, {
        protobuf()
    }),
    ;

    companion object {
        val ALL: Array<SamplingFormat> = SamplingFormat.entries.toTypedArray()
    }
}

data class DumpLog(
    val role: Role,
    val phase: Phase,
    val log: String,
) {
    companion object {
        fun fromText(lines: List<String>): List<DumpLog> {
            return lines
                .map { it.trim() }
                .filter { !it.startsWith("//") && it.isNotBlank() }
                .map { line ->
                    val (prefix, log) = line.split("\$", limit = 2).map { it.trim() }
                    val (role, phase) = prefix.split(" ")

                    DumpLog(Role.fromText(role), Phase.fromText(phase), log)
                }
        }
    }
}

enum class Role {
    Server, Client;

    companion object {
        fun fromText(text: String): Role {
            return text.trimStart('[').trimEnd(']').let {
                if (it == "Server") Server else Client
            }
        }
    }
}

enum class Phase {
    Send, Receive;

    companion object {
        fun fromText(text: String): Phase {
            return text.trimStart('[').trimEnd(']').let {
                if (it == "Send") Send else Receive
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
        return replace(stackTraceRegExp, "\"stacktrace\":[]")
    }

    private val logs = rawLogs.map {
        it.copy(log = it.log.removeExceptionStackTraceData())
    }

    private val transformed = logs.filter {
        it.phase == Phase.Send // receive phases are not needed to replay a connection
    }

    override fun compare(other: WireContent): GoldComparisonResult {
        return if (transformed == other.transformed) {
            GoldComparisonResult.Ok
        } else {
            GoldComparisonResult.Failure(
                "Wire comparison failed:\n" +
                        "Gold:\n ${other.dump()}\n\n" +
                        "Actual:\n ${dump()}"
            )
        }
    }

    override fun dump(): String {
        return logs.joinToString(GoldUtils.NewLine) { dump ->
            val base = "[${dump.role}] [${dump.phase}] $ ${dump.log}"

            if (commentBinaryOutput) {
                val decodedBytes = dump.log.rpcInternalHexToReadableBinary()

                "// decoded: $decodedBytes" + GoldUtils.NewLine + base
            } else {
                base
            }
        }
    }

    companion object {
        fun fromText(text: String): WireContent {
            return WireContent(
                rawLogs = DumpLog.fromText(text.split('\r', '\n')),
                commentBinaryOutput = false,
            )
        }
    }
}
