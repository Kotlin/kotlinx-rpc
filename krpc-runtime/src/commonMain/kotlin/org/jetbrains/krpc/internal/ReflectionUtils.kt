package org.jetbrains.krpc.internal

import kotlin.reflect.KClass
import kotlin.reflect.KType

@Suppress("UNCHECKED_CAST")
fun <T : Any> KType.kClass(): KClass<T> {
    val classifier = classifier ?: error("Expected denotable type, found $this")
    val classifierClass = classifier as? KClass<*> ?: error("Expected class type, found $this")

    return classifierClass as KClass<T>
}


fun internalError(message: String): Nothing {
    error("Internal kRPC error: $message")
}
