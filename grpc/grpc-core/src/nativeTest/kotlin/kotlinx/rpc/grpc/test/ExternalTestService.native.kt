/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test

private class NativeExternalServiceHandle : ExternalServiceHandle {
    override fun close() {
        // Not implemented for Native yet
    }
}

internal actual fun startExternalService(mainClassName: String, jarPath: String): ExternalServiceHandle {
    // Starting a JVM process from Kotlin/Native tests is not supported in this minimal implementation.
    // If needed, provide a posix-based launcher in the future.
    error("ExternalTestService is not supported on Native in this environment.")
}
