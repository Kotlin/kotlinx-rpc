/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(InternalRpcApi::class, ExperimentalRpcApi::class)

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
import com.google.protobuf_test_messages.edition2023.TestAllTypesEdition2023Internal
import com.google.protobuf_test_messages.edition2023.TestMessagesEdition2023KtExtensions
import com.google.protobuf_test_messages.editions.proto2.TestMessagesProto2EditionsKtExtensions
import com.google.protobuf_test_messages.proto2.TestMessagesProto2KtExtensions
import kotlinx.io.Buffer
import kotlinx.io.bytestring.ByteString
import kotlinx.io.readByteArray
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtoConfig
import kotlinx.rpc.protobuf.ProtoExtensionRegistry
import kotlinx.rpc.protobuf.internal.InternalMessage
import kotlinx.rpc.protobuf.ProtobufException

// Adapted from
// https://github.com/protocolbuffers/protobuf/blob/main/conformance/ConformanceJava.java
// and
// https://github.com/bufbuild/protobuf-conformance/blob/main/impl/baseline/runner.ts
internal class ConformanceClient {
    // todo text and json formats
//    private var typeRegistry: TypeRegistry? = null

    private val extensionRegistry = ProtoExtensionRegistry {
        // Edition 2023 extensions
        +TestMessagesEdition2023KtExtensions.extensionInt32
        +TestMessagesEdition2023KtExtensions.extensionString
        +TestMessagesEdition2023KtExtensions.extensionBytes
        +TestMessagesEdition2023KtExtensions.groupliketype
        +TestMessagesEdition2023KtExtensions.delimitedExt
        // Proto2 extensions
        +TestMessagesProto2KtExtensions.extensionInt32
        +TestMessagesProto2KtExtensions.extensionString
        +TestMessagesProto2KtExtensions.extensionBytes
        +TestMessagesProto2KtExtensions.groupfield
        +TestMessagesProto2KtExtensions.TestAllTypesProto2.MessageSetCorrectExtension1.messageSetExtension
        +TestMessagesProto2KtExtensions.TestAllTypesProto2.MessageSetCorrectExtension2.messageSetExtension
        +TestMessagesProto2KtExtensions.TestAllTypesProto2.ExtensionWithOneof.extensionWithOneof
        +TestMessagesProto2KtExtensions.TestAllRequiredTypesProto2.MessageSetCorrectExtension1.messageSetExtension
        +TestMessagesProto2KtExtensions.TestAllRequiredTypesProto2.MessageSetCorrectExtension2.messageSetExtension
        // Editions Proto2 extensions
        +TestMessagesProto2EditionsKtExtensions.extensionInt32
        +TestMessagesProto2EditionsKtExtensions.extensionString
        +TestMessagesProto2EditionsKtExtensions.extensionBytes
        +TestMessagesProto2EditionsKtExtensions.groupfield
        +TestMessagesProto2EditionsKtExtensions.TestAllTypesProto2.MessageSetCorrectExtension1.messageSetExtension
        +TestMessagesProto2EditionsKtExtensions.TestAllTypesProto2.MessageSetCorrectExtension2.messageSetExtension
        +TestMessagesProto2EditionsKtExtensions.TestAllTypesProto2.ExtensionWithOneof.extensionWithOneof
        +TestMessagesProto2EditionsKtExtensions.TestAllRequiredTypesProto2.MessageSetCorrectExtension1.messageSetExtension
        +TestMessagesProto2EditionsKtExtensions.TestAllRequiredTypesProto2.MessageSetCorrectExtension2.messageSetExtension
    }

    private val config = ProtoConfig { extensionRegistry = this@ConformanceClient.extensionRegistry }

    private fun readFromStdin(buf: ByteArray, len: Int): Boolean {
        var remaining = len
        var ofs = 0
        while (remaining > 0) {
            val read = platformRead(buf, ofs, remaining)
            if (read == -1) {
                return false // EOF
            }
            ofs += read
            remaining -= read
        }

        return true
    }

    private fun writeToStdout(buf: ByteArray) {
        platformWrite(buf)
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

    @Suppress("UNCHECKED_CAST")
    private fun marshallerOf(messageType: String): GrpcMarshaller<Any?>? {
        val marshaller = when (messageType) {
            "protobuf_test_messages.proto3.TestAllTypesProto3" ->
                com.google.protobuf_test_messages.proto3.TestAllTypesProto3Internal.MARSHALLER

            "protobuf_test_messages.proto2.TestAllTypesProto2" ->
                com.google.protobuf_test_messages.proto2.TestAllTypesProto2Internal.MARSHALLER

            "protobuf_test_messages.editions.TestAllTypesEdition2023" ->
                TestAllTypesEdition2023Internal.MARSHALLER

            "protobuf_test_messages.editions.proto3.TestAllTypesProto3" ->
                com.google.protobuf_test_messages.editions.proto3.TestAllTypesProto3Internal.MARSHALLER

            "protobuf_test_messages.editions.proto2.TestAllTypesProto2" ->
                com.google.protobuf_test_messages.editions.proto2.TestAllTypesProto2Internal.MARSHALLER

            else -> return null
        }
        return marshaller as GrpcMarshaller<Any?>
    }

    private fun doTest(request: ConformanceRequest): ConformanceResponse {
        val testMessage: InternalMessage
        val marshaller: GrpcMarshaller<Any?>
        val messageType: String = request.messageType

        marshaller = marshallerOf(messageType)
            ?: return ConformanceResponse {
                result = ConformanceResponse.Result.Skipped("Unsupported message type: $messageType")
            }

        when (request.payload) {
            is ConformanceRequest.Payload.ProtobufPayload -> {
                try {
                    val binary = (request.payload as ConformanceRequest.Payload.ProtobufPayload).value

                    testMessage = marshaller.decode(binary.toByteArray().buffered(), config) as InternalMessage
                } catch (e: ProtobufException) {
                    return ConformanceResponse {
                        result = ConformanceResponse.Result.ParseError(
                            e.message ?: "ProtobufException with unknown message of type ${e::class.simpleName}"
                        )
                    }
                }
            }

            is ConformanceRequest.Payload.JsonPayload -> {
                error("JSON payloads are not supported by the conformance client.")
            }

            is ConformanceRequest.Payload.TextPayload -> {
                error("TEXT_FORMAT payloads are not supported by the conformance client.")
            }

            else -> {
                error("Request didn't have payload.")
            }
        }

        return when (request.requestedOutputFormat) {
            UNSPECIFIED -> error("Unspecified output format.")

            PROTOBUF -> {
                val messageString = marshaller.encode(testMessage).readByteArray().asByteString()

                ConformanceResponse {
                    result = ConformanceResponse.Result.ProtobufPayload(messageString)
                }
            }

            JSON -> {
                error("JSON output format is not supported by the conformance client.")
            }

            TEXT_FORMAT -> ConformanceResponse {
                error("TEXT_FORMAT output format is not supported by the conformance client.")
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

        dumpConfig.dumpConformanceInputFile?.let { writeFile(it, serializedInput) }

        val request: ConformanceRequest = ConformanceRequestInternal.MARSHALLER
            .decode(serializedInput.buffered())

        // The conformance runner will request a list of failures as the first request.
        // This will be known by message_type == "conformance.FailureSet", a conformance
        // test should return a serialized FailureSet in protobuf_payload.
        val response = if (request.messageType == "conformance.FailureSet") {
            ConformanceResponse {
                result = ConformanceResponse.Result.ProtobufPayload(
                    FailureSetInternal.MARSHALLER.encode(
                        FailureSet {}
                    ).readByteArray().asByteString()
                )
            }
        } else {
            test(request)
        }

        val serializedOutput = ConformanceResponseInternal.MARSHALLER
            .encode(response).readByteArray()

        dumpConfig.dumpConformanceOutputFile?.let { writeFile(it, serializedOutput) }

        writeLittleEndianIntToStdout(serializedOutput.size)
        writeToStdout(serializedOutput)

        return true
    }

    fun runMockTest(): Boolean {
        return doTest {
            if (`is ProtobufInput_UnknownOrdering_ProtobufOutput`(it)) {
                return@doTest ConformanceResponse {
                    result = ConformanceResponse.Result.ProtobufPayload(ByteArray(0).asByteString())
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

        if (!payload.toByteArray().contentEquals(ProtobufInput_UnknownOrdering_ProtobufOutput_Payload)) {
            return false
        }

        return true
    }

    @Suppress("PrivatePropertyName")
    private val ProtobufInput_UnknownOrdering_ProtobufOutput_Payload = byteArrayOf(
        -46, 41, 3, 97, 98, 99, -48, 41, 123, -46, 41, 3, 100, 101, 102, -48, 41, -56, 3
    )

    fun runConformanceTest(dumpConfig: DumpConfig): Boolean {
        return doTest(dumpConfig) { request ->
            val responseResult = runCatching {
                doTest(request)
            }

            if (request.payload is ConformanceRequest.Payload.ProtobufPayload) {
                dumpConfig.dumpPayloadInputFile?.let {
                    writeFile(it, (request.payload as ConformanceRequest.Payload.ProtobufPayload).value.toByteArray())
                }
            }

            responseResult.fold(
                onSuccess = { it },
                onFailure = {
                    val message = it.message ?: "Unknown error"

                    if (`is ProtobufInput_UnknownOrdering_ProtobufOutput`(request)) {
                        ConformanceResponse {
                            result = ConformanceResponse.Result.ProtobufPayload(message.encodeToByteArray().asByteString())
                        }
                    } else {
                        ConformanceResponse {
                            result = ConformanceResponse.Result.RuntimeError(message)
                        }
                    }
                }
            ).apply {
                if (result is ConformanceResponse.Result.ProtobufPayload) {
                    dumpConfig.dumpPayloadOutputFile?.let {
                        writeFile(it, (result as ConformanceResponse.Result.ProtobufPayload).value.toByteArray())
                    }
                }
            }
        }
    }
}

internal class DumpConfig(
    val dumpConformanceInputFile: String?,
    val dumpConformanceOutputFile: String?,
    val dumpPayloadInputFile: String?,
    val dumpPayloadOutputFile: String?,
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
        getEnvVariable("DUMP_CONFORMANCE_INPUT_FILE"),
        getEnvVariable("DUMP_CONFORMANCE_OUTPUT_FILE"),
        getEnvVariable("DUMP_PAYLOAD_INPUT_FILE"),
        getEnvVariable("DUMP_PAYLOAD_OUTPUT_FILE"),
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

private fun ByteArray.buffered() = Buffer().apply { write(this@buffered) }

private fun ByteArray.asByteString(): ByteString = ByteString(*this)
