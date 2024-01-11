/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
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
    data class List(val valueName: FqName) : FieldType

    data class Map(val keyName: FqName, val valueName: FqName) : FieldType

    data class Reference(val value: FqName) : FieldType
}
