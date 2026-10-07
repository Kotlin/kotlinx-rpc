/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.grpc.fixture.proto

import com.google.protobuf.DescriptorProtos.DescriptorProto
import com.google.protobuf.DescriptorProtos.FileDescriptorProto
import com.google.protobuf.DescriptorProtos.MethodDescriptorProto
import com.google.protobuf.DescriptorProtos.ServiceDescriptorProto
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest
import kotlinx.rpc.protoc.gen.core.Config
import kotlinx.rpc.protoc.gen.core.model.Model
import kotlinx.rpc.protoc.gen.core.toModel

private const val PROTO_FILE_NAME = "testfixture.proto"

internal fun grpcProto(setup: GrpcProtoFixture.() -> Unit): FileDescriptorProto {
    return GrpcProtoFixture().apply(setup).build()
}

internal fun FileDescriptorProto.toGeneratorModel(config: Config): Model {
    return CodeGeneratorRequest.newBuilder()
        .addProtoFile(this)
        .addFileToGenerate(PROTO_FILE_NAME)
        .build()
        .toModel(config)
}

internal class GrpcProtoFixture {
    private val builder = FileDescriptorProto.newBuilder()
        .setName(PROTO_FILE_NAME)
        .setSyntax("proto3")

    fun message(name: String) {
        builder.addMessageType(DescriptorProto.newBuilder().setName(name))
    }

    fun service(name: String, setup: ServiceProtoFixture.() -> Unit) {
        builder.addService(ServiceProtoFixture(name).apply(setup).build())
    }

    fun build(): FileDescriptorProto = builder.build()
}

internal class ServiceProtoFixture(name: String) {
    private val builder = ServiceDescriptorProto.newBuilder().setName(name)

    fun rpc(
        name: String,
        input: String,
        output: String,
        clientStreaming: Boolean = false,
        serverStreaming: Boolean = false,
    ) {
        builder.addMethod(
            MethodDescriptorProto.newBuilder()
                .setName(name)
                .setInputType(input)
                .setOutputType(output)
                .setClientStreaming(clientStreaming)
                .setServerStreaming(serverStreaming)
        )
    }

    fun build(): ServiceDescriptorProto = builder.build()
}
