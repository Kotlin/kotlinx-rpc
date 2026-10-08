/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.testing

/**
 * Shared connection and metadata constants for the Gradle-managed reference server.
 *
 * The server is `:tests:grpc-test-server`, a plain grpc-java server which Gradle starts before the first test
 * task that opts in with `withGrpcClientTestServer()` and shares between all of them, including the JVM and
 * native test tasks. See `tests/grpc-test-server/README.md` for its services and the scenario protocol.
 */
internal object GrpcClientTestServer {
    /** Loopback address the reference server listens on. */
    const val HOST: String = "127.0.0.1"

    /** Port of the reference server's main listener, serving both data-plane and control-plane services. */
    const val PORT: Int = 50051

    /**
     * ASCII request metadata key through which a data-plane call opts into the scenario registered under its
     * value, see [GrpcClientTestFixture.callId].
     */
    const val CALL_ID_METADATA_KEY: String = "kxrpc-test-call-id"
}
