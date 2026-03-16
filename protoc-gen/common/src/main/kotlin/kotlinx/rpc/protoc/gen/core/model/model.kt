/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.core.model

import com.google.protobuf.ByteString
import com.google.protobuf.Descriptors
import kotlinx.rpc.protoc.gen.core.Comment
import kotlinx.rpc.protoc.gen.core.FqNameTable

data class Model(
    val files: List<FileDeclaration>,
    val nameTable: FqNameTable,
)

data class FileDeclaration(
    val name: String,
    val packageName: FqName.Package,
    val dependencies: List<FileDeclaration>,
    val messageDeclarations: List<MessageDeclaration>,
    val enumDeclarations: List<EnumDeclaration>,
    val serviceDeclarations: List<ServiceDeclaration>,
    val extensions: List<FieldDeclaration>,
    val doc: List<Comment>,
    val dec: Descriptors.FileDescriptor,
    val deprecated: Boolean,

    // the object that stores internal extension descriptors for
    // extensions registered in this proto file
    val internalExtensionDescriptorObject: FqName.Declaration,
)

data class MessageDeclaration(
    val name: FqName,
    val presenceMaskSize: Int,
    val actualFields: List<FieldDeclaration>, // excludes oneOf fields, but includes oneOf itself
    val oneOfDeclarations: List<OneOfDeclaration>,
    val enumDeclarations: List<EnumDeclaration>,
    val nestedDeclarations: List<MessageDeclaration>,
    val extensions: List<FieldDeclaration>,
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

    val presenceInterfaceName: FqName.Declaration by lazy {
        parent.value?.presenceInterfaceName?.nested(name.simpleName)
            ?: name.suffixed("Presence")
    }

    val companionName: FqName.Declaration by lazy { name.nested("Companion") }

    val internalClassName: FqName.Declaration by lazy {
        parent.value?.internalClassName?.nested(name.simpleName)?.suffixed("Internal")
            ?: name.suffixed("Internal")
    }

    val builderClassName: FqName.Declaration by lazy {
        name.nested("Builder")
    }

    val internalCompanionName: FqName.Declaration by lazy { internalClassName.nested("Companion") }
    val presenceIndicesName: FqName.Declaration by lazy { internalClassName.nested("PresenceIndices") }
    val defaultObjectRef: FqName.Declaration by lazy { internalClassName.nested("DEFAULT") }
    val marshallerObjectName: FqName.Declaration by lazy { internalClassName.nested("MARSHALLER") }
    val bytesDefaultsName: FqName.Declaration by lazy { internalClassName.nested("BytesDefaults") }

    fun hasPresenceFieldsRecursive(): Boolean {
        return hasPresenceFields || hasExtensionRange || nestedDeclarations.any { it.isUserFacing && it.hasPresenceFieldsRecursive() }
    }

    fun hasEnums(): Boolean {
        return enumDeclarations.isNotEmpty() || nestedDeclarations.any { it.hasEnums() }
    }

    fun nonDefaultByteFields(): List<FieldDeclaration> {
        return actualFields
            .filter {
                it.type == FieldType.IntegralType.BYTES &&
                        it.dec.hasDefaultValue() &&
                        !(it.dec.defaultValue as ByteString).isEmpty
            }
    }

    val extensionRanges: List<IntRange> by lazy {
        dec.toProto().extensionRangeList.map { it.start..it.end }
    }

    val hasExtensionRange: Boolean by lazy {
        extensionRanges.isNotEmpty()
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
    val companionName: FqName.Declaration by lazy { name.nested("Companion") }
    val unrecognisedName: FqName.Declaration by lazy { name.nested("UNRECOGNIZED") }

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
    /** The Kotlin-safe name (escaped with backticks if it's a Kotlin keyword). Used in generated code. */
    val name: String,
    /** The raw proto-derived name (unescaped). Used for derived names like `hasXxx` and display. */
    val rawName: String,
    val type: FieldType,
    val doc: Comment?,
    val dec: Descriptors.FieldDescriptor,
    val deprecated: Boolean,
    // defines the index in the presenceMask of the Message.
    // this cannot be the number, as only fields with hasPresence == true are part of the presenceMask
    val presenceIdx: Int? = null,
    // the message of the field (also for extension fields)
    val containingType: Lazy<MessageDeclaration>,
    // fully-qualified symbol of the generated internal extension descriptor property
    // (null for non-extension fields)
    val extensionDescriptorName: FqName.Declaration? = null,
) {
    val packedFixedSize by lazy { type.wireType == WireType.FIXED64 || type.wireType == WireType.FIXED32 }

    val isPartOfOneof: Boolean = dec.realContainingOneof != null

    val isPartOfMapEntry = dec.containingType.options.mapEntry

    // all normal fields are non-nullable (KRPC-262)
    // only oneof fields are nullable.
    val nullable: Boolean = type is FieldType.OneOf
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
