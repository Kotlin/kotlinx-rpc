/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.fixture.proto

import com.google.protobuf.DescriptorProtos.DescriptorProto
import com.google.protobuf.DescriptorProtos.EnumDescriptorProto
import com.google.protobuf.DescriptorProtos.EnumValueDescriptorProto
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto
import com.google.protobuf.DescriptorProtos.FileDescriptorProto
import com.google.protobuf.DescriptorProtos.OneofDescriptorProto
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest
import kotlinx.rpc.protoc.gen.core.Config
import kotlinx.rpc.protoc.gen.core.model.Model
import kotlinx.rpc.protoc.gen.core.toModel

private const val PROTO_FILE_NAME = "testfixture.proto"

internal fun protobufProto(setup: ProtobufProtoFixture.() -> Unit): FileDescriptorProto {
    return ProtobufProtoFixture().apply(setup).build()
}

internal fun FileDescriptorProto.toGeneratorModel(config: Config): Model {
    return CodeGeneratorRequest.newBuilder()
        .addProtoFile(this)
        .addFileToGenerate(PROTO_FILE_NAME)
        .build()
        .toModel(config)
}

internal class ProtobufProtoFixture {
    private val builder = FileDescriptorProto.newBuilder()
        .setName(PROTO_FILE_NAME)
        .setSyntax("proto3")

    fun message(name: String, setup: MessageProtoFixture.() -> Unit) {
        builder.addMessageType(MessageProtoFixture(name).apply(setup).build())
    }

    fun enumType(name: String, vararg values: String) {
        val enumBuilder = EnumDescriptorProto.newBuilder().setName(name)
        values.forEachIndexed { number, value ->
            enumBuilder.addValue(
                EnumValueDescriptorProto.newBuilder()
                    .setName(value)
                    .setNumber(number)
            )
        }
        builder.addEnumType(enumBuilder)
    }

    fun build(): FileDescriptorProto = builder.build()
}

internal class MessageProtoFixture(name: String) {
    private val builder = DescriptorProto.newBuilder().setName(name)
    private var nextFieldNumber = 1

    fun field(name: String) {
        builder.addField(scalarField(name, oneOfIndex = null))
    }

    fun oneOf(name: String, setup: OneOfProtoFixture.() -> Unit) {
        val oneOfIndex = builder.oneofDeclCount
        builder.addOneofDecl(OneofDescriptorProto.newBuilder().setName(name))
        OneOfProtoFixture { fieldName ->
            builder.addField(scalarField(fieldName, oneOfIndex))
        }.apply(setup)
    }

    private fun scalarField(name: String, oneOfIndex: Int?): FieldDescriptorProto {
        return FieldDescriptorProto.newBuilder()
            .setName(name)
            .setNumber(nextFieldNumber++)
            .setLabel(FieldDescriptorProto.Label.LABEL_OPTIONAL)
            .setType(FieldDescriptorProto.Type.TYPE_STRING)
            .apply {
                if (oneOfIndex != null) {
                    setOneofIndex(oneOfIndex)
                }
            }
            .build()
    }

    fun build(): DescriptorProto = builder.build()
}

internal class OneOfProtoFixture(private val addField: (String) -> Unit) {
    fun field(name: String) {
        addField(name)
    }
}
