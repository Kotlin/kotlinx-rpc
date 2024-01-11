/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.model

interface FqName {
    val packageName: String
    val simpleName: String
    val parentName: FqName?
}

data class SimpleFqName(
    override val packageName: String,
    override val simpleName: String,
    override val parentName: FqName? = null,
): FqName
