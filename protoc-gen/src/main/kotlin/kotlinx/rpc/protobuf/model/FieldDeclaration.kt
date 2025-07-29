/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.model

import com.google.protobuf.DescriptorProtos

data class FieldDeclaration(
    val name: String,
    val number: Int,
    val type: FieldType,
    val nullable: Boolean,
    val deprecated: Boolean,
    val doc: String?,
    val proto: DescriptorProtos.FieldDescriptorProto
)

enum class WireType {
    VARINT,
    FIXED64,
    LENGTH_DELIMITED,
    START_GROUP,
    END_GROUP,
    FIXED32
}

sealed interface FieldType {
    val defaultValue: String
    val wireType: WireType

    data class List(val value: FieldType) : FieldType {
        override val defaultValue: String = "emptyList()"
        override val wireType: WireType = value.wireType
    }

    data class Map(val entry: Lazy<Entry>) : FieldType {
        override val defaultValue: String = "emptyMap()"
        override val wireType: WireType = WireType.LENGTH_DELIMITED

        data class Entry(val key: FieldType, val value: FieldType)
    }

    data class Reference(val value: Lazy<FqName>) : FieldType {
        override val defaultValue: String = "null"
        override val wireType: WireType = WireType.LENGTH_DELIMITED
    }

    data class OneOf(val value: Lazy<FqName>, val index: Int) : FieldType {
        override val defaultValue: String = "null"
        override val wireType: WireType = WireType.LENGTH_DELIMITED
    }

    enum class IntegralType(simpleName: String, override val defaultValue: String, override val wireType: WireType) :
        FieldType {
        STRING("String", "\"\"", WireType.LENGTH_DELIMITED),
        BYTES("ByteArray", "byteArrayOf()", WireType.LENGTH_DELIMITED),
        BOOL("Boolean", "false", WireType.VARINT),
        FLOAT("Float", "0.0f", WireType.FIXED32),
        DOUBLE("Double", "0.0", WireType.FIXED64),
        INT32("Int", "0", WireType.VARINT),
        INT64("Long", "0", WireType.VARINT),
        UINT32("UInt", "0u", WireType.VARINT),
        UINT64("ULong", "0u", WireType.VARINT),
        FIXED32("UInt", "0u", WireType.FIXED32),
        FIXED64("ULong", "0u", WireType.FIXED64),
        SINT32("Int", "0", WireType.VARINT),
        SINT64("Long", "0", WireType.VARINT),
        SFIXED32("Int", "0", WireType.FIXED32),
        SFIXED64("Long", "0", WireType.FIXED64);

        val fqName: FqName = FqName.Declaration(simpleName, FqName.Package.fromString("kotlin"))
    }
}
