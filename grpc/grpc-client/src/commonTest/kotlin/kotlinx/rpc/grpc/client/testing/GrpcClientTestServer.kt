/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.testing

/**
 * Connection and metadata constants shared with the Gradle-managed reference server.
 *
 * The reference server is `:tests:grpc-test-server`, a plain grpc-java server. Gradle starts it before the first
 * test task that calls `withGrpcClientTestServer()`. Gradle shares the server between all such tasks, including
 * the JVM and native test tasks. The services and the scenario protocol are in `tests/grpc-test-server/README.md`.
 */
internal object GrpcClientTestServer {
    /** Loopback address the reference server listens on. */
    const val HOST: String = "127.0.0.1"

    /**
     * Port of the reference server's main listener, which serves the data-plane and control-plane services.
     *
     * Gradle picks a free port for each build and passes it to the test task in [PORT_ENV].
     */
    val port: Int by lazy {
        checkNotNull(environmentVariable(PORT_ENV)) { "$PORT_ENV isn't set" }.toInt()
    }

    private const val PORT_ENV: String = "GRPC_TEST_SERVER_PORT"

    /**
     * ASCII request metadata key that a data-plane call sets to select the scenario registered under the value.
     * See [GrpcClientTestFixture.callId].
     */
    const val CALL_ID_METADATA_KEY: String = "kxrpc-test-call-id"
}

/** Returns the value of the environment variable [name] of the test process, or `null` if it isn't set. */
internal expect fun environmentVariable(name: String): String?
