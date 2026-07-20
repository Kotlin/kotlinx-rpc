/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.grpc

import kotlinx.rpc.protoc.gen.core.Config
import kotlinx.rpc.protoc.gen.core.GeneratedMetadata
import kotlinx.rpc.protoc.gen.core.Platform
import kotlinx.rpc.protoc.gen.grpc.fixture.proto.grpcProto
import kotlinx.rpc.protoc.gen.grpc.fixture.proto.toGeneratorModel
import kotlin.test.Test
import kotlin.test.assertContains

class ModelToGrpcKotlinCommonGeneratorTest {
    @Test
    fun `camel case option converts gRPC declaration names`() {
        val config = Config(
            explicitApiModeEnabled = false,
            generateComments = false,
            generateFileLevelComments = false,
            generateOptionalFieldOrNullGetters = false,
            indentSize = 4,
            platform = Platform.Jvm,
            protoNamesOutput = null,
            camelCaseNames = true,
        )
        val model = grpcProto {
            message("request_message")
            message("response_message")
            service("user_service") {
                rpc(
                    name = "GetUser",
                    input = "request_message",
                    output = "response_message",
                )
            }
        }.toGeneratorModel(config)

        val generated = ModelToGrpcKotlinCommonGenerator(
            config = config,
            generatedMetadata = GeneratedMetadata(),
            model = model,
        ).generateKotlinFiles().single().build()

        assertContains(
            generated,
            """
            @Grpc
            interface UserService {
                @Grpc.Method(name = "GetUser")
                suspend fun getUser(message: RequestMessage): ResponseMessage
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `disabling camel case option preserves gRPC declaration names`() {
        val config = Config(
            explicitApiModeEnabled = false,
            generateComments = false,
            generateFileLevelComments = false,
            generateOptionalFieldOrNullGetters = false,
            indentSize = 4,
            platform = Platform.Jvm,
            protoNamesOutput = null,
            camelCaseNames = false,
        )
        val model = grpcProto {
            message("request_message")
            message("response_message")
            service("user_service") {
                rpc(
                    name = "GetUser",
                    input = "request_message",
                    output = "response_message",
                )
            }
        }.toGeneratorModel(config)

        val generated = ModelToGrpcKotlinCommonGenerator(
            config = config,
            generatedMetadata = GeneratedMetadata(),
            model = model,
        ).generateKotlinFiles().single().build()

        assertContains(
            generated,
            """
            @Grpc
            interface user_service {
                suspend fun GetUser(message: request_message): response_message
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `camel case option handles underscores and digits in gRPC declaration names`() {
        val config = Config(
            explicitApiModeEnabled = false,
            generateComments = false,
            generateFileLevelComments = false,
            generateOptionalFieldOrNullGetters = false,
            indentSize = 4,
            platform = Platform.Jvm,
            protoNamesOutput = null,
            camelCaseNames = true,
        )
        val model = grpcProto {
            message("request__message_2")
            message("response_1_message")
            service("user__service_2") {
                rpc(
                    name = "get__HTTP_2_response",
                    input = "request__message_2",
                    output = "response_1_message",
                )
            }
        }.toGeneratorModel(config)

        val generated = ModelToGrpcKotlinCommonGenerator(
            config = config,
            generatedMetadata = GeneratedMetadata(),
            model = model,
        ).generateKotlinFiles().single().build()

        assertContains(
            generated,
            """
            @Grpc
            interface User_Service_2 {
                @Grpc.Method(name = "get__HTTP_2_response")
                suspend fun get__HTTP_2Response(message: Request_Message_2): Response_1Message
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `generates unary server streaming client streaming and bidirectional streaming RPCs`() {
        val config = Config(
            explicitApiModeEnabled = false,
            generateComments = false,
            generateFileLevelComments = false,
            generateOptionalFieldOrNullGetters = false,
            indentSize = 4,
            platform = Platform.Jvm,
            protoNamesOutput = null,
            camelCaseNames = true,
        )
        val model = grpcProto {
            message("request_message")
            message("response_message")
            service("streaming_service") {
                rpc(
                    name = "UnaryCall",
                    input = "request_message",
                    output = "response_message",
                )
                rpc(
                    name = "ServerStreamingCall",
                    input = "request_message",
                    output = "response_message",
                    serverStreaming = true,
                )
                rpc(
                    name = "ClientStreamingCall",
                    input = "request_message",
                    output = "response_message",
                    clientStreaming = true,
                )
                rpc(
                    name = "BidirectionalStreamingCall",
                    input = "request_message",
                    output = "response_message",
                    clientStreaming = true,
                    serverStreaming = true,
                )
            }
        }.toGeneratorModel(config)

        val generated = ModelToGrpcKotlinCommonGenerator(
            config = config,
            generatedMetadata = GeneratedMetadata(),
            model = model,
        ).generateKotlinFiles().single().build()

        assertContains(
            generated,
            """
            @Grpc
            interface StreamingService {
                @Grpc.Method(name = "UnaryCall")
                suspend fun unaryCall(message: RequestMessage): ResponseMessage

                @Grpc.Method(name = "ServerStreamingCall")
                fun serverStreamingCall(message: RequestMessage): Flow<ResponseMessage>

                @Grpc.Method(name = "ClientStreamingCall")
                suspend fun clientStreamingCall(message: Flow<RequestMessage>): ResponseMessage

                @Grpc.Method(name = "BidirectionalStreamingCall")
                fun bidirectionalStreamingCall(message: Flow<RequestMessage>): Flow<ResponseMessage>
            }
            """.trimIndent(),
        )
    }
}
