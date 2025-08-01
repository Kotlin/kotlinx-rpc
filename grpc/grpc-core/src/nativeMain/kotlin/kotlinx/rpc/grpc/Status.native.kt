/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

public actual class Status {
    public actual fun getDescription(): String? {
        TODO("Not yet implemented")
    }

    public actual fun getCause(): Throwable? {
        TODO("Not yet implemented")
    }
}

public actual val Status.code: StatusCode
    get() = TODO("Not yet implemented")

public actual fun Status(
    code: StatusCode,
    description: String?,
    cause: Throwable?,
): Status {
    TODO("Not yet implemented")
}
