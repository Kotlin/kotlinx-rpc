/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.model

data class MessageDeclaration(
    val outerClassName: FqName,
    val name: FqName,
    val actualFields: Sequence<FieldDeclaration>, // excludes oneOf fields, but includes oneOf itself
    val oneOfDeclarations: Sequence<OneOfDeclaration>,
    val enumDeclarations: Sequence<EnumDeclaration>,
    val nestedDeclarations: Sequence<MessageDeclaration>,
    val deprecated: Boolean,
    val doc: String?,
)
