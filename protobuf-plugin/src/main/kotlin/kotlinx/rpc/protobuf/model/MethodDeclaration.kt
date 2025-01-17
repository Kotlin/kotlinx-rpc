/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.model

data class MethodDeclaration(
    val name: FqName,
    val clientStreaming: Boolean,
    val serverStreaming: Boolean,
    val inputType: Lazy<MessageDeclaration>,
    val outputType: Lazy<MessageDeclaration>,
)
