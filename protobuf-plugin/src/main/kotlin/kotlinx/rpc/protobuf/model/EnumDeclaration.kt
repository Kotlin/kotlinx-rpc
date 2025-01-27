/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.model

data class EnumDeclaration(
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
