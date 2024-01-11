/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.FileAppender
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse.Feature
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.helpers.NOPLogger

class RPCProtobufPlugin {
    companion object {
        private const val DEBUG_OUTPUT_OPTION = "debugOutput"
    }

    private var debugOutput: String? = null
    private val logger: Logger by lazy {
        val debugOutput = debugOutput ?: return@lazy NOPLogger.NOP_LOGGER

        (LoggerFactory.getILoggerFactory().getLogger("RPCProtobufPlugin") as ch.qos.logback.classic.Logger).apply {
            val appender = FileAppender<ILoggingEvent>().apply {
                isAppend = true
                file = debugOutput
                encoder = PatternLayoutEncoder().apply {
                    pattern = "%d{YYYY-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
                }
            }

            addAppender(appender)

            level = Level.ALL
        }
    }

    fun run(input: CodeGeneratorRequest): CodeGeneratorResponse {
        val parameters = input.parameter.split(",")
        parameters.singleOrNull { it.startsWith(DEBUG_OUTPUT_OPTION) }?.let {
            debugOutput = it.removePrefix("$DEBUG_OUTPUT_OPTION=")
        }

        val files = input.generateKotlinFiles()
            .map { file ->
                CodeGeneratorResponse.File.newBuilder()
                    .apply {
                        val dir = file.packageName?.replace('.', '/')?.plus("/") ?: ""

                        // some filename already contain package (true for Google's default .proto files)
                        val filename = file.filename?.removePrefix(dir) ?: error("File name can not be null")
                        name = "$dir$filename"
                        content = file.build()
                    }
                    .build()
            }

        return CodeGeneratorResponse.newBuilder()
            .apply {
                files.forEach(::addFile)

                supportedFeatures = Feature.FEATURE_PROTO3_OPTIONAL_VALUE.toLong()
            }
            .build()
    }

    private fun CodeGeneratorRequest.generateKotlinFiles(): List<FileGenerator> {
        val interpreter = ProtoToModelInterpreter(logger)
        val model = interpreter.interpretProtocRequest(this)
        val fileGenerator = ModelToKotlinGenerator(model, logger)
        return fileGenerator.generateKotlinFiles()
    }
}
