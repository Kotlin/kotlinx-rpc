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

    /** Port of the reference server's main listener, which serves the data-plane and control-plane services. */
    const val PORT: Int = 50051

    /**
     * ASCII request metadata key that a data-plane call sets to select the scenario registered under the value.
     * See [GrpcClientTestFixture.callId].
     */
    const val CALL_ID_METADATA_KEY: String = "kxrpc-test-call-id"
}
