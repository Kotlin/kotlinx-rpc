/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal

import kotlinx.rpc.internal.transport.SerializedException
import kotlinx.rpc.internal.transport.StackElement
import java.lang.reflect.Constructor
import java.lang.reflect.Modifier

private val throwableFields = Throwable::class.java.fieldsCountOrDefault(-1)

internal actual class DeserializedException actual constructor(
    private val toStringMessage: String,
    actual override val message: String,
    stacktrace: List<StackElement>,
    cause: SerializedException?,
    private val className: String
) : Throwable() {
    init {
        stackTrace = stacktrace.map {
            StackTraceElement(it.clazz, it.method, it.fileName, it.lineNumber)
        }.toTypedArray()
    }

    override val cause: Throwable? = cause?.deserialize()

    override fun toString(): String = toStringMessage
}

internal actual fun Throwable.stackElements(): List<StackElement> = stackTrace.map {
    StackElement(
        clazz = it.className,
        method = it.methodName,
        fileName = it.fileName,
        lineNumber = it.lineNumber,
    )
}

@InternalRPCApi
public actual fun SerializedException.deserialize(): Throwable {
    try {
        val clazz = Class.forName(className)
        val fieldsCount = clazz.fieldsCountOrDefault(throwableFields)
        if (fieldsCount != throwableFields) {
            return DeserializedException(toStringMessage, message, stacktrace, cause, className)
        }

        val constructors = clazz.constructors.sortedByDescending { it.parameterTypes.size }
        for (constructor in constructors) {
            tryCreateException(constructor, this)?.let { return it }
        }
    } catch (_: Throwable) {
        return DeserializedException(toStringMessage, message, stacktrace, cause, className)
    }

    return DeserializedException(toStringMessage, message, stacktrace, cause, className)
}


private fun Class<*>.fieldsCountOrDefault(defaultValue: Int) =
    kotlin.runCatching { fieldsCount() }.getOrDefault(defaultValue)

private tailrec fun Class<*>.fieldsCount(accumulator: Set<String> = emptySet()): Int {
    val fields = declaredFields
        .filter { !Modifier.isStatic(it.modifiers) }
        .map { it.name }
    val totalFields = (accumulator + fields)
    val superClass = superclass ?: run {
        var messageField = false
        return totalFields.count {
            // Throwable has a private field 'detailMessage', but a public open `getMessage`,
            // which are the same in custom Kotlin exceptions
            if (it == "message" || it == "detailMessage") {
                if (messageField) {
                    return@count false
                }
                messageField = true
            }
            true
        }
    }
    return superClass.fieldsCount(totalFields)
}

private fun Class<*>.isExceptionClass(): Boolean = Throwable::class.java.isAssignableFrom(this)

private fun tryCreateException(constructor: Constructor<*>, serialized: SerializedException): Throwable? {
    val parameters = constructor.parameterTypes

    val result = when (parameters.size) {
        2 -> when {
            parameters[0] == String::class.java && parameters[1].isExceptionClass() ->
                constructor.newInstance(serialized.message, serialized.cause?.deserialize())

            else -> null
        }

        1 -> when {
            parameters[0].isExceptionClass() -> constructor.newInstance(serialized.cause?.deserialize())
            parameters[0] == String::class.java -> constructor.newInstance(serialized.message)
            else -> null
        }

        0 -> constructor.newInstance()
        else -> null
    }

    if (result != null) {
        val clazz = result::class.java
        // Call [Throwable.setStackTrace]
        val setStackTrace = clazz.getMethod("setStackTrace", Array<StackTraceElement>::class.java)
        setStackTrace.invoke(result, serialized.stacktrace.map {
            StackTraceElement(it.clazz, it.method, it.fileName, it.lineNumber)
        }.toTypedArray())
    }

    return result as? Throwable?
}
