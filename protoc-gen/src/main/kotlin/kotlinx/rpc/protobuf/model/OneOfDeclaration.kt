/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.model

import com.google.protobuf.Descriptors

data class OneOfDeclaration(
    val name: FqName,
    val variants: List<FieldDeclaration>,
    val descriptor: Descriptors.OneofDescriptor?
)
