/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.test

import com.google.protobuf.conformance.ConformanceRequest
import com.google.protobuf.conformance.ConformanceRequestInternal
import com.google.protobuf.conformance.ConformanceResponse
import com.google.protobuf.conformance.ConformanceResponseInternal
import com.google.protobuf.conformance.FailureSet
import com.google.protobuf.conformance.FailureSetInternal
import com.google.protobuf.conformance.TestCategory
import com.google.protobuf.conformance.WireFormat.*
import com.google.protobuf.conformance.invoke
import kotlinx.rpc.grpc.codec.MessageCodec
import kotlinx.rpc.grpc.codec.WithCodec
import kotlinx.rpc.protobuf.internal.InternalMessage
import kotlinx.rpc.protobuf.internal.ProtobufException
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.writeBytes
import kotlin.reflect.KClass

// Adapted from
// https://github.com/protocolbuffers/protobuf/blob/main/conformance/ConformanceJava.java
// and
// https://github.com/bufbuild/protobuf-conformance/blob/main/impl/baseline/runner.ts
internal class ConformanceClient {
    // todo text and json formats
//    private var typeRegistry: TypeRegistry? = null

    private fun readFromStdin(buf: ByteArray, len: Int): Boolean {
        var len = len
        var ofs = 0
        while (len > 0) {
            val read = System.`in`.read(buf, ofs, len)
            if (read == -1) {
                return false // EOF
            }
            ofs += read
            len -= read
        }

        return true
    }

    private fun writeToStdout(buf: ByteArray) {
        System.out.write(buf)
    }

    // Returns -1 on EOF (the actual values will always be positive).
    private fun readLittleEndianIntFromStdin(): Int {
        val buf = ByteArray(4)
        if (!readFromStdin(buf, 4)) {
            return -1
        }
        return ((buf[0].toInt() and 0xff)
                or ((buf[1].toInt() and 0xff) shl 8)
                or ((buf[2].toInt() and 0xff) shl 16)
                or ((buf[3].toInt() and 0xff) shl 24))
    }

    private fun writeLittleEndianIntToStdout(int: Int) {
        val buf = ByteArray(4)
        buf[0] = int.toByte()
        buf[1] = (int shr 8).toByte()
        buf[2] = (int shr 16).toByte()
        buf[3] = (int shr 24).toByte()
        writeToStdout(buf)
    }

    private fun testMessageKClassOf(messageType: String): KClass<*> {
        return when (messageType) {
            "protobuf_test_messages.proto3.TestAllTypesProto3" -> com.google.protobuf_test_messages.proto3.TestAllTypesProto3::class
            "protobuf_test_messages.proto2.TestAllTypesProto2" -> com.google.protobuf_test_messages.proto2.TestAllTypesProto2::class
            "protobuf_test_messages.editions.TestAllTypesEdition2023" -> com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023::class
            "protobuf_test_messages.editions.proto3.TestAllTypesProto3" -> com.google.protobuf_test_messages.editions.proto3.TestAllTypesProto3::class
            "protobuf_test_messages.editions.proto2.TestAllTypesProto2" -> com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2::class
            else -> error(
                "Protobuf request has unexpected payload type: $messageType"
            )
        }
    }

    private fun doTest(request: ConformanceRequest): ConformanceResponse {
        val testMessage: InternalMessage
        val codec: MessageCodec<Any?>
        val messageType: String = request.messageType

        // todo support extensions
//        val extensions: ExtensionRegistry? = ExtensionRegistry.newInstance()
//        createTestFile(messageType)
//            .getMethod("registerAllExtensions", ExtensionRegistry::class.java)
//            .invoke(null, extensions)

        when (request.payload) {
            is ConformanceRequest.Payload.ProtobufPayload -> {
                try {
                    @Suppress("UNCHECKED_CAST")
                    codec = testMessageKClassOf(messageType)
                        .annotations
                        .filterIsInstance<WithCodec>()
                        .singleOrNull()
                        ?.codec
                        ?.objectInstance as? MessageCodec<Any?>
                        ?: error("Codec must be an object for $messageType")

                    val binary = (request.payload as ConformanceRequest.Payload.ProtobufPayload).value

                    testMessage = codec.decode(binary.inputStream() /*extensions*/) as InternalMessage
                } catch (e: ProtobufException) {
                    return ConformanceResponse {
                        result = ConformanceResponse.Result.ParseError(
                            e.message ?: "ProtobufException with unknown message of type ${e.javaClass.name}"
                        )
                    }
                }
            }

            is ConformanceRequest.Payload.JsonPayload -> {
                error("JSON payloads are not supported by the conformance client.")
//                try {
//                    var parser: JsonFormat.Parser = JsonFormat.parser().usingTypeRegistry(typeRegistry)
//                    if (request.getTestCategory()
//                        === Conformance.TestCategory.JSON_IGNORE_UNKNOWN_PARSING_TEST
//                    ) {
//                        parser = parser.ignoringUnknownFields()
//                    }
//                    val builder: AbstractMessage.Builder<*> =
//                        createTestMessage(messageType).getMethod("newBuilder")
//                            .invoke(null) as AbstractMessage.Builder<*>
//                    parser.merge(request.getJsonPayload(), builder)
//                    testMessage = builder.build() as AbstractMessage
//                } catch (e: ProtobufException) {
//                    return ConformanceResponse {
//                        result = ConformanceResponse.Result.ParseError(
//                            e.message ?: "ProtobufException with unknown message of type ${e.javaClass.name}"
//                        )
//                    }
//                } catch (e: Exception) {
//                    throw RuntimeException(e)
//                }
            }

            is ConformanceRequest.Payload.TextPayload -> {
                error("TEXT_FORMAT payloads are not supported by the conformance client.")
//                try {
//                    val builder: AbstractMessage.Builder<*> =
//                        createTestMessage(messageType).getMethod("newBuilder")
//                            .invoke(null) as AbstractMessage.Builder<*>
//                    TextFormat.merge(request.getTextPayload(), extensions, builder)
//                    testMessage = builder.build() as AbstractMessage
//                } catch (e: Exception) { // todo specific exceptions
//                    return ConformanceResponse {
//                        result = ConformanceResponse.Result.ParseError(
//                            e.message ?: "Exception with unknown message of type ${e.javaClass.name}"
//                        )
//                    }
//                } catch (e: Exception) {
//                    throw RuntimeException(e)
//                }
            }

            else -> {
                error("Request didn't have payload.")
            }
        }

        return when (request.requestedOutputFormat) {
            UNSPECIFIED -> error("Unspecified output format.")

            PROTOBUF -> {
                val messageString: ByteArray = codec.encode(testMessage).readBytes()

                ConformanceResponse {
                    result = ConformanceResponse.Result.ProtobufPayload(messageString)
                }
            }

            JSON -> {
                error("JSON output format is not supported by the conformance client.")
//                try {
//                    ConformanceResponse {
//                        result = ConformanceResponse.Result.JsonPayload(
//                            JsonFormat.printer().usingTypeRegistry(typeRegistry).print(testMessage)
//                        )
//                    }
//                } catch (e: ProtobufException) {
//                    ConformanceResponse {
//                        result = ConformanceResponse.Result.SerializeError(
//                            e.message ?: "ProtobufException with unknown message of type ${e.javaClass.name}"
//                        )
//                    }
//                } catch (e: IllegalArgumentException) {
//                    ConformanceResponse {
//                        result = ConformanceResponse.Result.SerializeError(
//                            e.message ?: "IllegalArgumentException with unknown message of type ${e.javaClass.name}"
//                        )
//                    }
//                }
            }

            TEXT_FORMAT -> ConformanceResponse {
                error("TEXT_FORMAT output format is not supported by the conformance client.")
//                result = ConformanceResponse.Result.TextPayload(
//                    TextFormat.printer().printToString(testMessage)
//                )
            }

            else -> {
                error("Unexpected request output format: ${request.requestedOutputFormat}.")
            }
        }
    }

    private fun doTest(
        dumpConfig: DumpConfig = DumpConfig.EMPTY,
        test: (ConformanceRequest) -> ConformanceResponse,
    ): Boolean {
        val bytes = readLittleEndianIntFromStdin()

        if (bytes == -1) {
            return false // EOF
        }

        val serializedInput = ByteArray(bytes)

        if (!readFromStdin(serializedInput, bytes)) {
            throw RuntimeException("Unexpected EOF from test program.")
        }

        dumpConfig.dumpConformanceInputFile?.writeBytes(serializedInput)

        val request: ConformanceRequest = ConformanceRequestInternal.CODEC
            .decode(serializedInput.inputStream())

        // The conformance runner will request a list of failures as the first request.
        // This will be known by message_type == "conformance.FailureSet", a conformance
        // test should return a serialized FailureSet in protobuf_payload.
        val response = if (request.messageType == "conformance.FailureSet") {
            ConformanceResponse {
                result = ConformanceResponse.Result.ProtobufPayload(
                    FailureSetInternal.CODEC.encode(
                        FailureSet {}
                    ).readBytes()
                )
            }
        } else {
            test(request)
        }

        val serializedOutput = ConformanceResponseInternal.CODEC
            .encode(response).readBytes()

        dumpConfig.dumpConformanceOutputFile?.writeBytes(serializedOutput)

        writeLittleEndianIntToStdout(serializedOutput.size)
        writeToStdout(serializedOutput)

        return true
    }

    fun runMockTest(): Boolean {
        return doTest {
            if (`is ProtobufInput_UnknownOrdering_ProtobufOutput`(it)) {
                return@doTest ConformanceResponse {
                    result = ConformanceResponse.Result.ProtobufPayload(ByteArray(0))
                }
            }

            ConformanceResponse {
                result = ConformanceResponse.Result.RuntimeError("Mock test")
            }
        }
    }

    @Suppress("FunctionName")
    private fun `is ProtobufInput_UnknownOrdering_ProtobufOutput`(
        request: ConformanceRequest,
    ): Boolean {
        if (request.testCategory != TestCategory.BINARY_TEST) {
            return false
        }

        if (request.requestedOutputFormat != PROTOBUF) {
            return false
        }

        if (
            request.messageType != "protobuf_test_messages.proto3.TestAllTypesProto3" &&
            request.messageType != "protobuf_test_messages.proto2.TestAllTypesProto2" &&
            request.messageType != "protobuf_test_messages.editions.proto2.TestAllTypesProto2" &&
            request.messageType != "protobuf_test_messages.editions.proto3.TestAllTypesProto3"
        ) {
            return false
        }

        if (request.payload !is ConformanceRequest.Payload.ProtobufPayload) {
            return false
        }

        val payload = (request.payload as ConformanceRequest.Payload.ProtobufPayload).value

        if (payload.size != ProtobufInput_UnknownOrdering_ProtobufOutput_Payload.size) {
            return false
        }

        if (!payload.contentEquals(ProtobufInput_UnknownOrdering_ProtobufOutput_Payload)) {
            return false
        }

        return true
    }

    @Suppress("PrivatePropertyName")
    private val ProtobufInput_UnknownOrdering_ProtobufOutput_Payload = byteArrayOf(
        -46, 41, 3, 97, 98, 99, -48, 41, 123, -46, 41, 3, 100, 101, 102, -48, 41, -56, 3
    )

    fun runConformanceTest(dumpConfig: DumpConfig): Boolean {
        //        typeRegistry =
//            TypeRegistry.newBuilder()
//                .add(TestMessagesProto3.TestAllTypesProto3.getDescriptor())
//                .add(
//                    com.google.protobuf_test_messages.editions.proto3.TestMessagesProto3Editions
//                        .TestAllTypesProto3.getDescriptor()
//                )
//                .build()

        return doTest(dumpConfig) { request ->
            val responseResult = runCatching {
                doTest(request)
            }

            if (request.payload is ConformanceRequest.Payload.ProtobufPayload) {
                dumpConfig.dumpPayloadInputFile
                    ?.writeBytes((request.payload as ConformanceRequest.Payload.ProtobufPayload).value)
            }

            responseResult.fold(
                onSuccess = { it },
                onFailure = {
                    val message = it.message ?: "Unknown error"

                    if (`is ProtobufInput_UnknownOrdering_ProtobufOutput`(request)) {
                        ConformanceResponse {
                            result = ConformanceResponse.Result.ProtobufPayload(message.encodeToByteArray())
                        }
                    } else {
                        ConformanceResponse {
                            result = ConformanceResponse.Result.RuntimeError(message)
                        }
                    }
                }
            ).apply {
                if (result is ConformanceResponse.Result.ProtobufPayload) {
                    dumpConfig.dumpPayloadOutputFile
                        ?.writeBytes((result as ConformanceResponse.Result.ProtobufPayload).value)
                }
            }
        }
    }
}

class DumpConfig(
    val dumpConformanceInputFile: Path?,
    val dumpConformanceOutputFile: Path?,
    val dumpPayloadInputFile: Path?,
    val dumpPayloadOutputFile: Path?,
) {
    companion object {
        val EMPTY = DumpConfig(null, null, null, null)
    }
}

fun main(args: Array<String>) {
    val client = ConformanceClient()

    if (args.isEmpty()) {
        error("Expected at least one argument: run mode (mock|conformance).")
    }

    val dumpConfig = DumpConfig(
        getDumpFile("DUMP_CONFORMANCE_INPUT_FILE"),
        getDumpFile("DUMP_CONFORMANCE_OUTPUT_FILE"),
        getDumpFile("DUMP_PAYLOAD_INPUT_FILE"),
        getDumpFile("DUMP_PAYLOAD_OUTPUT_FILE"),
    )

    if (args[0] == "mock") {
        do {
            // test
        } while (client.runMockTest())
    } else if (args[0] == "conformance") {
        do {
            // test
        } while (client.runConformanceTest(dumpConfig))
    } else {
        error("Invalid run mode: ${args[0]}")
    }
}

private fun getDumpFile(propName: String): Path? {
    val dumpFileProp = System.getenv(propName)
    return if (dumpFileProp != null) {
        Path(dumpFileProp)
    } else {
        null
    }
}
