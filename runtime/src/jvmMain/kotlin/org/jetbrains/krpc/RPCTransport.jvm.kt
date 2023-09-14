package org.jetbrains.krpc

actual class DeserializedException actual constructor(
    private val toStringMessage: String,
    override val message: String,
    stacktrace: List<StackElement>,
    cause: SerializedException?
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
        lineNumber = it.lineNumber
    )
}