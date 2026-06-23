/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.grpc.fixture.proto

import com.google.protobuf.DescriptorProtos
import com.google.protobuf.DescriptorProtos.MethodDescriptorProto.SERVER_STREAMING_FIELD_NUMBER
import com.google.protobuf.DescriptorProtos.ServiceDescriptorProto.METHOD_FIELD_NUMBER
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest
import kotlinx.rpc.protoc.gen.core.Config
import kotlinx.rpc.protoc.gen.core.Platform
import kotlinx.rpc.protoc.gen.core.model.Model
import kotlinx.rpc.protoc.gen.core.toModel

private const val PROTO_FILE_NAME = "testfixture.proto"

internal fun grpcProto(body: GrpcProtoFixture.() -> Unit): DescriptorProtos.FileDescriptorProto =
    GrpcProtoFixture().apply(body).build()

internal fun DescriptorProtos.FileDescriptorProto.toGrpcModel(): Model {
    return CodeGeneratorRequest.newBuilder()
        .addProtoFile(this)
        .addFileToGenerate(PROTO_FILE_NAME)
        .build()
        .toModel(grpcGeneratorConfig(camelCaseGrpcMethods = false))
}

internal fun grpcGeneratorConfig(camelCaseGrpcMethods: Boolean): Config = Config(
    explicitApiModeEnabled = false,
    generateComments = true,
    generateFileLevelComments = true,
    indentSize = 4,
    platform = Platform.Jvm,
    protoNamesOutput = null,
    camelCaseGrpcMethods = camelCaseGrpcMethods,
)

internal class GrpcProtoFixture {
    private val builder = DescriptorProtos.FileDescriptorProto.newBuilder()
        .setName(PROTO_FILE_NAME)
        .setSyntax("proto3")

    fun message(name: String) {
        builder.addMessageType(DescriptorProtos.DescriptorProto.newBuilder().setName(name))
    }

    fun service(name: String, body: ServiceProtoFixture.() -> Unit) {
        val serviceIndex = builder.serviceCount
        val service = ServiceProtoFixture(name).apply(body)
        builder.addService(service.build())
        service.sourceLocations(serviceIndex).forEach {
            builder.sourceCodeInfoBuilder.addLocation(it)
        }
    }

    fun build(): DescriptorProtos.FileDescriptorProto = builder.build()


    internal class ServiceProtoFixture(name: String) {
        private val builder = DescriptorProtos.ServiceDescriptorProto.newBuilder().setName(name)

        fun rpc(
            name: String,
            input: String,
            output: String,
            idempotencyLevel: DescriptorProtos.MethodOptions.IdempotencyLevel? = null,
            clientStreaming: Boolean = false,
            serverStreaming: Boolean = false,
            deprecated: Boolean = false,
            comment: String? = null,
        ) {
            val methodIndex = builder.methodCount
            builder.addMethod(
                DescriptorProtos.MethodDescriptorProto.newBuilder()
                    .setName(name)
                    .setInputType(input)
                    .setOutputType(output)
                    .setClientStreaming(clientStreaming)
                    .setServerStreaming(serverStreaming)
                    .apply {
                        if (idempotencyLevel != null || deprecated) {
                            options = DescriptorProtos.MethodOptions.newBuilder()
                                .apply {
                                    idempotencyLevel?.let(::setIdempotencyLevel)
                                    if (deprecated) setDeprecated(true)
                                }
                                .build()
                        }
                    }
            )
            comment?.let {
                methodComments += methodIndex to it
            }
        }

        fun build(): DescriptorProtos.ServiceDescriptorProto = builder.build()

        fun sourceLocations(serviceIndex: Int): List<DescriptorProtos.SourceCodeInfo.Location> {
            return methodComments.map { (methodIndex, comment) ->
                DescriptorProtos.SourceCodeInfo.Location.newBuilder()
                    .addAllPath(listOf(SERVER_STREAMING_FIELD_NUMBER, serviceIndex, METHOD_FIELD_NUMBER, methodIndex))
                    .setLeadingComments(comment)
                    .build()
            }
        }

        private val methodComments = mutableMapOf<Int, String>()

    }
}