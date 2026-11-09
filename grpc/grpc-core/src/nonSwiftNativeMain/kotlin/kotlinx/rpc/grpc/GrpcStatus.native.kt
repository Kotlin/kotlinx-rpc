/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import org.jetbrains.kotlinx.library.api.watchdog.ExemptionReason
import org.jetbrains.kotlinx.library.api.watchdog.IntentionallyWithoutEquals
import org.jetbrains.kotlinx.library.api.watchdog.IntentionallyWithoutHashCode

@IntentionallyWithoutEquals(reason = ExemptionReason.TODO, description = "review before 1.0")
@IntentionallyWithoutHashCode(reason = ExemptionReason.TODO, description = "review before 1.0")
public actual class GrpcStatus internal constructor(
    private val description: String?,
    internal val statusCode: GrpcStatusCode,
    private val cause: Throwable?,
) {
    public actual fun getDescription(): String? = description

    public actual fun getCause(): Throwable? = cause

    override fun toString(): String {
        return "GrpcStatus(description=$description, statusCode=$statusCode, cause=$cause)"
    }
}

public actual val GrpcStatus.statusCode: GrpcStatusCode
    get() = this.statusCode

public actual fun GrpcStatus(
    code: GrpcStatusCode,
    description: String?,
    cause: Throwable?,
): GrpcStatus = GrpcStatus(description, code, cause)
