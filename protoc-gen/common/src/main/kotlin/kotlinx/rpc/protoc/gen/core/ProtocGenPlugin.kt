/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.core

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.FileAppender
import com.google.protobuf.DescriptorProtos
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse.Feature
import kotlinx.rpc.protoc.gen.core.model.Model
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.helpers.NOPLogger
import java.io.File

@Volatile
lateinit var globalLogger: Logger

class Config(
    val explicitApiModeEnabled: Boolean,
    val generateComments: Boolean,
    val generateFileLevelComments: Boolean,
    val indentSize: Int,
    val platform: Platform,
)

abstract class ProtocGenPlugin {
    companion object {
        private const val DEBUG_OUTPUT_OPTION = "debugOutput"
        private const val EXPLICIT_API_MODE_ENABLED_OPTION = "explicitApiModeEnabled"
        private const val GENERATE_COMMENTS_OPTION = "generateComments"
        private const val GENERATE_FILE_LEVEL_COMMENTS_OPTION = "generateFileLevelComments"
        private const val INDENT_SIZE_OPTION = "indentSize"
        private const val PLATFORM_OPTION = "platform"
    }

    private var debugOutput: String? = null
    private val logger: Logger by lazy {
        val debugOutput = debugOutput ?: return@lazy NOPLogger.NOP_LOGGER

        val factory = LoggerFactory.getILoggerFactory()
        (factory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger).apply {
            detachAndStopAllAppenders()

            val appender = FileAppender<ILoggingEvent>().apply {
                isAppend = true
                file = debugOutput
                encoder = PatternLayoutEncoder().apply {
                    pattern = "%d{YYYY-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
                    context = factory as LoggerContext
                    start()
                }
                context = factory as LoggerContext
                start()
            }

            addAppender(appender)

            level = Level.ALL
            isAdditive = true
        }
    }

    fun run(input: CodeGeneratorRequest): CodeGeneratorResponse {
        val parameters = input.parameter.split(",").associate {
            it.split("=").let { (key, value) -> key to value }
        }

        debugOutput = parameters[DEBUG_OUTPUT_OPTION]
        val explicitApiModeEnabled = parameters[EXPLICIT_API_MODE_ENABLED_OPTION]?.toBooleanStrictOrNull() ?: false

        val generateComments = parameters[GENERATE_COMMENTS_OPTION]?.toBooleanStrictOrNull() ?: true
        val generateFileLevelComments = parameters[GENERATE_FILE_LEVEL_COMMENTS_OPTION]?.toBooleanStrictOrNull() ?: true

        val indentSize = parameters[INDENT_SIZE_OPTION]?.toIntOrNull() ?: 4

        val platform = parameters[PLATFORM_OPTION]?.takeIf { it.isNotBlank() } ?: "jvm"

        val config = Config(
            explicitApiModeEnabled = explicitApiModeEnabled,
            generateComments = generateComments,
            generateFileLevelComments = generateFileLevelComments,
            indentSize = indentSize,
            platform = Platform.fromString(platform),
        )

        globalLogger = logger
        val files = input.runGeneration(config)

        return CodeGeneratorResponse.newBuilder()
            .apply {
                files.forEach(::addFile)

                val features =
                    Feature.FEATURE_PROTO3_OPTIONAL_VALUE or
                            Feature.FEATURE_SUPPORTS_EDITIONS_VALUE

                minimumEdition = DescriptorProtos.Edition.EDITION_PROTO2_VALUE
                maximumEdition = DescriptorProtos.Edition.EDITION_MAX_VALUE

                supportedFeatures = features.toLong()
            }
            .build()
    }

    private fun CodeGeneratorRequest.runGeneration(config: Config): List<CodeGeneratorResponse.File?> {
        return try {
            generateKotlinByModel(
                config = config,
                model = this.toModel(config),
            ).map { file ->
                CodeGeneratorResponse.File.newBuilder()
                    .apply {
                        val dir = file.packagePath
                            ?.takeIf { it.isNotEmpty() }
                            ?.replace('.', File.separatorChar)
                            ?.plus(File.separatorChar)
                            ?: ""

                        // some filename already contains package (true for Google's default .proto files)
                        val filename = file.filename?.removePrefix(dir) ?: error("File name can not be null")
                        name = "$dir$filename"
                        content = file.build()
                    }
                    .build()
            }
        } finally {
            (logger as ch.qos.logback.classic.Logger).detachAndStopAllAppenders()
        }
    }

    protected abstract fun generateKotlinByModel(
        config: Config,
        model: Model,
    ): List<FileGenerator>
}
