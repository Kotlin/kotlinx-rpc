/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test

/**
 * Platform handle for an external test service process.
 */
internal interface ExternalServiceHandle {
    fun close()
}

/**
 * Starts an external service process from a JAR and returns a handle to manage its lifecycle.
 * Implementations should be blocked until the service is ready to accept requests (or fail with an error).
 */
internal expect fun startExternalService(mainClassName: String, jarPath: String): ExternalServiceHandle

/**
 * Manages the lifecycle of an external test service.
 * The service is started when the [ExternalTestService] is created and stopped when the [ExternalTestService] is closed.
 * The main class of the service is specified by the [mainClassName] parameter.
 */
class ExternalTestService(
    mainClassName: String,
    jarPath: String = getEnv("TEST_SERVICES_JAR") ?: error("TEST_SERVICES_JAR is not set"),
) : AutoCloseable {
    private val handle: ExternalServiceHandle = startExternalService(mainClassName, jarPath)

    override fun close() {
        handle.close()
    }
}