/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.model

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
    val doc: String?,
    val dec: Descriptors.FileDescriptor,
)

data class MessageDeclaration(
    val name: FqName,
    val presenceMaskSize: Int,
    val actualFields: List<FieldDeclaration>, // excludes oneOf fields, but includes oneOf itself
    val oneOfDeclarations: List<OneOfDeclaration>,
    val enumDeclarations: List<EnumDeclaration>,
    val nestedDeclarations: List<MessageDeclaration>,
    val doc: String?,
    val dec: Descriptors.Descriptor,
)

data class EnumDeclaration(
    val name: FqName,
    val originalEntries: List<Entry>,
    val aliases: List<Alias>,
    val doc: String?,
    val dec: Descriptors.EnumDescriptor,
) {
    data class Entry(
        val name: FqName,
        val doc: String?,
        val dec: Descriptors.EnumValueDescriptor,
    )

    data class Alias(
        val name: FqName,
        val original: Entry,
        val doc: String?,
        val dec: Descriptors.EnumValueDescriptor,
    )
}

data class OneOfDeclaration(
    val name: FqName,
    val variants: List<FieldDeclaration>,
    val dec: Descriptors.OneofDescriptor
)

data class FieldDeclaration(
    val name: String,
    val type: FieldType,
    val doc: String?,
    val dec: Descriptors.FieldDescriptor,
    // defines the index in the presenceMask of the Message.
    // this cannot be the number, as only fields with hasPresence == true are part of the presenceMask
    val presenceIdx: Int? = null
) {
    val packedFixedSize = type.wireType == WireType.FIXED64 || type.wireType == WireType.FIXED32

    // aligns with edition settings and backward compatibility with proto2 and proto3
    val nullable: Boolean = dec.hasPresence() && !dec.isRequired && !dec.hasDefaultValue() && !dec.isRepeated
    val number: Int = dec.number
}

data class ServiceDeclaration(
    val name: FqName,
    val methods: List<MethodDeclaration>,
    val dec: Descriptors.ServiceDescriptor,
)

data class MethodDeclaration(
    val name: String,
    val inputType: MessageDeclaration,
    val outputType: MessageDeclaration,
    val dec: Descriptors.MethodDescriptor,
)

