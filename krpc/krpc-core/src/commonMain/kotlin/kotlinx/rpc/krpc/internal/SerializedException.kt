/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal

import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@InternalRpcApi
@Serializable
@SerialName("org.jetbrains.krpc.StackElement")
public data class StackElement(
    val clazz: String,
    val method: String,
    val fileName: String?,
    val lineNumber: Int
)

@InternalRpcApi
@Serializable
@SerialName("org.jetbrains.krpc.SerializedException")
public data class SerializedException(
    public val toStringMessage: String,
    public val message: String,
    public val stacktrace: List<StackElement>,
    public val cause: SerializedException?,
    public val className: String
)
