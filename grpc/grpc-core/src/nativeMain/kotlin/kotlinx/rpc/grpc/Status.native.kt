/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

public actual class Status internal constructor(
    private val description: String?,
    internal val code: StatusCode,
    private val cause: Throwable?,
) {
    public actual fun getDescription(): String? = description

    public actual fun getCause(): Throwable? = cause

    override fun toString(): String {
        return "Status(description=$description, statusCode=$code, cause=$cause)"
    }
}

public actual val Status.statusCode: StatusCode
    get() = this.code

public actual fun Status(
    code: StatusCode,
    description: String?,
    cause: Throwable?,
): Status = Status(description, code, cause)
