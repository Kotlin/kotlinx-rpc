/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.model

import com.google.protobuf.DescriptorProtos
import com.google.protobuf.Descriptors

data class Model(
    val files: List<FileDeclaration>,
)

data class FileDeclaration(
    val name: String,
    val packageName: FqName.Package,
    val dependencies: List<FileDeclaration>,
    val messageDeclarations: List<MessageDeclaration>,
    val enumDeclarations: List<EnumDeclaration>,
    val serviceDeclarations: List<ServiceDeclaration>,
    val deprecated: Boolean,
    val doc: String?,
)

data class MessageDeclaration(
    val outerClassName: FqName,
    val name: FqName,
    val actualFields: List<FieldDeclaration>, // excludes oneOf fields, but includes oneOf itself
    val oneOfDeclarations: List<OneOfDeclaration>,
    val enumDeclarations: List<EnumDeclaration>,
    val nestedDeclarations: List<MessageDeclaration>,
    val deprecated: Boolean,
    val doc: String?,
)

data class EnumDeclaration(
    val outerClassName: FqName,
    val name: FqName,
    val originalEntries: List<Entry>,
    val aliases: List<Alias>,
    val deprecated: Boolean,
    val doc: String?,
) {
    data class Entry(
        val name: FqName,
        val deprecated: Boolean,
        val doc: String?,
    )

    data class Alias(
        val name: FqName,
        val original: Entry,
        val deprecated: Boolean,
        val doc: String?,
    )
}

data class OneOfDeclaration(
    val name: FqName,
    val variants: List<FieldDeclaration>,
    val descriptor: Descriptors.OneofDescriptor?
)

data class FieldDeclaration(
    val name: String,
    val number: Int,
    val type: FieldType,
    val nullable: Boolean,
    val deprecated: Boolean,
    val doc: String?,
    val proto: DescriptorProtos.FieldDescriptorProto
) {
    val isRepeated = proto.label == DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED
    val isExtension = proto.hasExtendee()
    val containsOneOf = proto.hasOneofIndex()

    val hasPresence = if (isRepeated) false else
        proto.proto3Optional || proto.type == DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE
                || proto.type == DescriptorProtos.FieldDescriptorProto.Type.TYPE_GROUP
                || isExtension || containsOneOf

    val isPackable = isRepeated && type.isPackable

    val packed = isPackable // TODO: must checked if this is also declared as [packed = true] (or proto3 auto packed)

    val packedFixedSize = type.wireType == WireType.FIXED64 || type.wireType == WireType.FIXED32
}

data class ServiceDeclaration(
    val name: FqName,
    val methods: List<MethodDeclaration>,
)

data class MethodDeclaration(
    val name: String,
    val clientStreaming: Boolean,
    val serverStreaming: Boolean,
    val inputType: Lazy<MessageDeclaration>,
    val outputType: Lazy<MessageDeclaration>,
)

