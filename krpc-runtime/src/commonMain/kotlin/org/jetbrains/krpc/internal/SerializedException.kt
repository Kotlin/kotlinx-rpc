package org.jetbrains.krpc.internal

import kotlinx.serialization.Serializable

@InternalKRPCApi
@Serializable
data class StackElement(
    val clazz: String,
    val method: String,
    val fileName: String?,
    val lineNumber: Int
)

@InternalKRPCApi
@Serializable
class SerializedException(
    val toStringMessage: String,
    val message: String,
    val stacktrace: List<StackElement>,
    val cause: SerializedException?,
    val className: String
)
