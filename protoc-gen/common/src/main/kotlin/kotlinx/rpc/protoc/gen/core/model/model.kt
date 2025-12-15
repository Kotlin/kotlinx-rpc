/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.core.model

import com.google.protobuf.Descriptors
import kotlinx.rpc.protoc.gen.core.Comment

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
    val doc: List<Comment>,
    val dec: Descriptors.FileDescriptor,
    val deprecated: Boolean,
)

data class MessageDeclaration(
    val name: FqName,
    val presenceMaskSize: Int,
    val actualFields: List<FieldDeclaration>, // excludes oneOf fields, but includes oneOf itself
    val oneOfDeclarations: List<OneOfDeclaration>,
    val enumDeclarations: List<EnumDeclaration>,
    val nestedDeclarations: List<MessageDeclaration>,
    val doc: Comment?,
    val dec: Descriptors.Descriptor,
    val deprecated: Boolean,
    val parent: Lazy<MessageDeclaration?>,
) {
    val isMapEntry = dec.options.mapEntry
    val isUserFacing = !isMapEntry

    val isGroup by lazy {
        val parent = dec.containingType ?: return@lazy false // top-level types can’t be groups
        parent.fields.any { f ->
            f.type == Descriptors.FieldDescriptor.Type.GROUP && f.messageType == dec
        }
    }

    val hasPresenceFields by lazy { actualFields.any { it.presenceIdx != null } }
    val presenceInterfaceFullName: String by lazy {
        parent.value?.let { "${it.presenceInterfaceFullName}.${name.simpleName}" }
            ?: name.fullName("Presence")
    }
}

data class EnumDeclaration(
    val name: FqName,
    val originalEntries: List<Entry>,
    val aliases: List<Alias>,
    val doc: Comment?,
    val dec: Descriptors.EnumDescriptor,
    val deprecated: Boolean,
) {

    fun defaultEntry(): Entry {
        // In proto3 and editions:
        // The first value must be a zero value, so that we can use 0 as a numeric default value
        // In proto2:
        // We use the first value as the default value
        return originalEntries.first()
    }

    data class Entry(
        val name: FqName,
        val doc: Comment?,
        val dec: Descriptors.EnumValueDescriptor,
        val deprecated: Boolean,
    )

    data class Alias(
        val name: FqName,
        val original: Entry,
        val doc: Comment?,
        val dec: Descriptors.EnumValueDescriptor,
        val deprecated: Boolean,
    )
}

data class OneOfDeclaration(
    val name: FqName,
    val variants: List<FieldDeclaration>,
    val dec: Descriptors.OneofDescriptor,
    val doc: Comment?,
)

data class FieldDeclaration(
    val name: String,
    val type: FieldType,
    val doc: Comment?,
    val dec: Descriptors.FieldDescriptor,
    val deprecated: Boolean,
    // defines the index in the presenceMask of the Message.
    // this cannot be the number, as only fields with hasPresence == true are part of the presenceMask
    val presenceIdx: Int? = null,
) {
    val packedFixedSize by lazy { type.wireType == WireType.FIXED64 || type.wireType == WireType.FIXED32 }

    val isPartOfOneof: Boolean = dec.realContainingOneof != null

    val isPartOfMapEntry = dec.containingType.options.mapEntry

    // aligns with edition settings and backward compatibility with proto2 and proto3
    val nullable: Boolean = (dec.hasPresence() && !dec.isRequired && !dec.hasDefaultValue()
            && !dec.isRepeated // repeated fields cannot be nullable (just empty)
            && !isPartOfOneof // upper conditions would match oneof inner fields
            && type !is FieldType.Message // messages must not be null (to conform protobuf standards)
            && !isPartOfMapEntry // map entry fields cannot be null
            )
            || type is FieldType.OneOf // all OneOf fields are nullable
    val number: Int = dec.number
}

data class ServiceDeclaration(
    val name: FqName,
    val methods: List<MethodDeclaration>,
    val dec: Descriptors.ServiceDescriptor,
    val doc: Comment?,
    val deprecated: Boolean,
)

data class MethodDeclaration(
    val name: String,
    val inputType: Lazy<MessageDeclaration>,
    val outputType: Lazy<MessageDeclaration>,
    val dec: Descriptors.MethodDescriptor,
    val doc: Comment?,
    val deprecated: Boolean,
)

