/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.model

data class FieldDeclaration(
    val name: String,
    val type: FieldType,
    val nullable: Boolean,
    val deprecated: Boolean,
    val doc: String?,
)

sealed interface FieldType {
    val defaultValue: String

    data class List(val value: FieldType) : FieldType {
        override val defaultValue: String = "emptyList()"
    }

    data class Map(val entry: Lazy<Entry>) : FieldType {
        override val defaultValue: String = "emptyMap()"

        data class Entry(val key: FieldType, val value: FieldType)
    }

    data class Reference(val value: Lazy<FqName>) : FieldType {
        override val defaultValue: String = "null"
    }

    enum class IntegralType(simpleName: String, override val defaultValue: String) : FieldType {
        STRING("String", "\"\""),
        BYTES("ByteArray", "byteArrayOf()"),
        BOOL("Boolean", "false"),
        FLOAT("Float", "0.0f"),
        DOUBLE("Double", "0.0"),
        INT32("Int", "0"),
        INT64("Long", "0"),
        UINT32("UInt", "0u"),
        UINT64("ULong", "0u"),
        FIXED32("UInt", "0u"),
        FIXED64("ULong", "0u"),
        SINT32("Int", "0"),
        SINT64("Long", "0"),
        SFIXED32("Int", "0"),
        SFIXED64("Long", "0");

        val fqName: FqName = FqName.Declaration(simpleName, FqName.Package.fromString("kotlin"))
    }
}
