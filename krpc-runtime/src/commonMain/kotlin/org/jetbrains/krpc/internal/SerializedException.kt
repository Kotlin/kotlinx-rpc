/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.internal

import kotlinx.serialization.Serializable

@InternalKRPCApi
@Serializable
public data class StackElement(
    val clazz: String,
    val method: String,
    val fileName: String?,
    val lineNumber: Int
)

@InternalKRPCApi
@Serializable
public class SerializedException(
    public val toStringMessage: String,
    public val message: String,
    public val stacktrace: List<StackElement>,
    public val cause: SerializedException?,
    public val className: String
)
