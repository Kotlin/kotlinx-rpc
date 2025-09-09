/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.core.model

enum class WireType {
    VARINT,
    FIXED64,
    LENGTH_DELIMITED,
    START_GROUP,
    END_GROUP,
    FIXED32
}

sealed interface FieldType {
    val defaultValue: String?
    val wireType: WireType

    val isPackable: Boolean get() = false

    data class List(val value: FieldType) : FieldType {
        override val defaultValue: String = "mutableListOf()"
        override val wireType: WireType = value.wireType
        override val isPackable: Boolean = value.isPackable
    }

    data class Map(val entry: Entry) : FieldType {
        override val defaultValue: String = "mutableMapOf()"
        override val wireType: WireType = WireType.LENGTH_DELIMITED

        data class Entry(val dec: MessageDeclaration, val key: FieldType, val value: FieldType)
    }

    data class Enum(val dec: EnumDeclaration) : FieldType {
        override val defaultValue = dec.defaultEntry().name.fullName()
        override val wireType: WireType = WireType.VARINT
        override val isPackable: Boolean = true
    }

    data class Message(val dec: Lazy<MessageDeclaration>) : FieldType {
        override val defaultValue: String? = null
        override val wireType: WireType by lazy { if (dec.value.isGroup) WireType.START_GROUP else WireType.LENGTH_DELIMITED }
    }

    data class OneOf(val dec: OneOfDeclaration) : FieldType {
        override val defaultValue: String = "null"
        override val wireType: WireType = WireType.LENGTH_DELIMITED
    }

    enum class IntegralType(
        simpleName: String,
        override val defaultValue: String,
        override val wireType: WireType,
        override val isPackable: Boolean = true,
    ) : FieldType {
        STRING("String", "\"\"", WireType.LENGTH_DELIMITED, false),
        BYTES("ByteArray", "byteArrayOf()", WireType.LENGTH_DELIMITED, false),
        BOOL("Boolean", "false", WireType.VARINT),
        FLOAT("Float", "0.0f", WireType.FIXED32),
        DOUBLE("Double", "0.0", WireType.FIXED64),
        INT32("Int", "0", WireType.VARINT),
        INT64("Long", "0L", WireType.VARINT),
        UINT32("UInt", "0u", WireType.VARINT),
        UINT64("ULong", "0uL", WireType.VARINT),
        FIXED32("UInt", "0u", WireType.FIXED32),
        FIXED64("ULong", "0uL", WireType.FIXED64),
        SINT32("Int", "0", WireType.VARINT),
        SINT64("Long", "0L", WireType.VARINT),
        SFIXED32("Int", "0", WireType.FIXED32),
        SFIXED64("Long", "0L", WireType.FIXED64);

        val fqName: FqName = FqName.Declaration(simpleName, FqName.Package.fromString("kotlin"))
    }
}

fun FieldType.scalarDefaultSuffix(): String = when (this) {
    FieldType.IntegralType.BOOL -> ""
    FieldType.IntegralType.FLOAT -> "f"
    FieldType.IntegralType.DOUBLE -> ""
    FieldType.IntegralType.INT32 -> ""
    FieldType.IntegralType.INT64 -> "L"
    FieldType.IntegralType.UINT32 -> "u"
    FieldType.IntegralType.UINT64 -> "uL"
    FieldType.IntegralType.FIXED32 -> "u"
    FieldType.IntegralType.FIXED64 -> "uL"
    FieldType.IntegralType.SINT32 -> ""
    FieldType.IntegralType.SINT64 -> "L"
    FieldType.IntegralType.SFIXED32 -> ""
    FieldType.IntegralType.SFIXED64 -> "L"
    else -> error("Unsupported scalar type: ${this::class}")
}
