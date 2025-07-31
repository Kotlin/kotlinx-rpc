/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.FileAppender
import com.google.protobuf.Descriptors
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse.Feature
import kotlinx.rpc.protobuf.model.FieldDeclaration
import kotlinx.rpc.protobuf.model.FileDeclaration
import kotlinx.rpc.protobuf.model.MessageDeclaration
import kotlinx.rpc.protobuf.model.Model
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.helpers.NOPLogger
import java.io.File

class RpcProtobufPlugin {
    companion object {
        private const val DEBUG_OUTPUT_OPTION = "debugOutput"
        private const val MESSAGE_MODE_OPTION = "messageMode"

        // if set to "common" we generate kotlin common source code
        private const val TARGET_MODE_OPTION = "targetMode"
    }

    enum class MessageMode {
        Interface, Class;

        companion object {
            fun of(value: String?): MessageMode {
                return when (value) {
                    "interface" -> Interface
                    "class" -> Class
                    null -> error("Message mode is not specified, use --messageMode=interface or --messageMode=class")
                    else -> error("Unknown message mode: $value")
                }
            }
        }
    }

    private var debugOutput: String? = null
    private lateinit var messageGenerationMode: MessageMode
    private var targetCommon: Boolean = false
    private val logger: Logger by lazy {
        val debugOutput = debugOutput ?: return@lazy NOPLogger.NOP_LOGGER

        val factory = LoggerFactory.getILoggerFactory()
        (factory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger).apply {
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
        messageGenerationMode = MessageMode.of(parameters[MESSAGE_MODE_OPTION])
        targetCommon = parameters[TARGET_MODE_OPTION] == "common"

        // choose common generator if targetMode option was set.
        val generatorFn = if (targetCommon) {
            { input.generateKotlinCommonFiles() }
        } else {
            { input.generateKotlinJvmFiles() }
        }

        val files = generatorFn()
            .map { file ->
                CodeGeneratorResponse.File.newBuilder()
                    .apply {
                        val dir = file.packagePath
                            ?.takeIf { it.isNotEmpty() }
                            ?.replace('.', File.separatorChar)
                            ?.plus(File.separatorChar)
                            ?: ""

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

    private fun CodeGeneratorRequest.generateKotlinJvmFiles(): List<FileGenerator> {
        val interpreter = ProtoToModelInterpreter(logger)
        val model = interpreter.interpretProtocRequest(this)
        val fileGenerator =
            ModelToKotlinJvmGenerator(model, logger, CodeGenerationParameters(messageGenerationMode))
        return fileGenerator.generateKotlinFiles()
    }

    private fun CodeGeneratorRequest.generateKotlinCommonFiles(): List<FileGenerator> {
        val model = this.toCommonModel()
        val fileGenerator =
            ModelToKotlinCommonGenerator(model, logger, CodeGenerationParameters(messageGenerationMode))
        return fileGenerator.generateKotlinFiles()
    }

    private fun showFqNames(model: Model, descriptors: List<Descriptors.FileDescriptor>) {

        System.err.println("Model:")
        for (file in model.files) {
            file.print("")
        }

        System.err.println(" ----------------------- ")

        System.err.println("Descriptors:")
        for (file in descriptors) {
            file.print("")
        }

        println("Make it crash with this print")
    }

    private fun FileDeclaration.print(intent: String) {
        System.err.println("[FILE]  $intent- ${packageName.simpleName}: ${packageName}")
        for (msg in messageDeclarations) {
            msg.print(intent + "\t")
        }
    }

    private fun MessageDeclaration.print(intent: String) {
        System.err.println("[MSG]   $intent- ${name.simpleName}: ${name}")
        for (field in actualFields) {
            field.print(intent + "\t")
        }
        for (msg in nestedDeclarations) {
            msg.print(intent + "\t")
        }
    }

    private fun FieldDeclaration.print(intent: String) {
        System.err.println("[FIELD] $intent- ${this.name}")
    }

    private fun Descriptors.FileDescriptor.print(intent: String) {
        System.err.println("[FILE]  $intent- ${fqName().simpleName}: ${fqName()}")
        for (msg in messageTypes) {
            msg.print(intent + "\t")
        }
    }

    private fun Descriptors.Descriptor.print(intent: String) {
        System.err.println("[MSG]   $intent- ${fqName().simpleName}: ${fqName()}")
        for (field in fields) {
            field.print(intent + "\t")
        }
        for (msg in nestedTypes) {
            msg.print(intent + "\t")
        }
    }

    private fun Descriptors.FieldDescriptor.print(intent: String) {
        System.err.println("[FIELD] $intent- ${fqName().simpleName}: ${ktTypeName()}")
    }

    private fun Descriptors.FieldDescriptor.ktTypeName(): String {
        return when (type) {
            Descriptors.FieldDescriptor.Type.DOUBLE -> "Double"
            Descriptors.FieldDescriptor.Type.FLOAT -> "Float"
            Descriptors.FieldDescriptor.Type.INT64 -> "Long"
            Descriptors.FieldDescriptor.Type.UINT64 -> "ULong"
            Descriptors.FieldDescriptor.Type.INT32 -> "Int"
            Descriptors.FieldDescriptor.Type.FIXED64 -> "ULong"
            Descriptors.FieldDescriptor.Type.FIXED32 -> "UInt"
            Descriptors.FieldDescriptor.Type.BOOL -> "Boolean"
            Descriptors.FieldDescriptor.Type.STRING -> "String"
            Descriptors.FieldDescriptor.Type.BYTES -> "ByteArray"
            Descriptors.FieldDescriptor.Type.UINT32 -> "UInt"
            Descriptors.FieldDescriptor.Type.SFIXED32 -> "Int"
            Descriptors.FieldDescriptor.Type.SFIXED64 -> "Long"
            Descriptors.FieldDescriptor.Type.SINT32 -> "Int"
            Descriptors.FieldDescriptor.Type.SINT64 -> "Long"
            Descriptors.FieldDescriptor.Type.ENUM -> "<missing>"
            Descriptors.FieldDescriptor.Type.MESSAGE -> messageType!!.fqName().toString()
            Descriptors.FieldDescriptor.Type.GROUP -> error("GROUP is unsupported")
        }
    }
}

