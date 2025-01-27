/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.model

interface FqName {
    val packageName: String
    val simpleName: String
    val parentName: FqName?

    val parentNameAsPrefix: String get() = parentName?.let { "$it.".removePrefix(".") } ?: ""
}

data class SimpleFqName(
    override val packageName: String,
    override val simpleName: String,
    override val parentName: FqName? = null,
): FqName {
    override fun equals(other: Any?): Boolean {
        return other is FqName && simpleName == other.simpleName
    }

    override fun hashCode(): Int {
        return simpleName.hashCode()
    }

    override fun toString(): String {
        return simpleName
    }
}
