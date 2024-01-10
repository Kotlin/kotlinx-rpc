/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc

import kotlinx.serialization.Serializable

@Serializable
data class StackElement(
    val clazz: String,
    val method: String,
    val fileName: String?,
    val lineNumber: Int
)

@Serializable
class SerializedException(
    val toStringMessage: String,
    val message: String,
    val stacktrace: List<StackElement>,
    val cause: SerializedException?,
    val className: String
)
