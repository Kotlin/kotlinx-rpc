/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.testing

/** Shared connection and metadata constants for the Gradle-managed reference server. */
internal object GrpcClientTestServer {
    const val HOST: String = "127.0.0.1"
    const val PORT: Int = 50051
    const val CALL_ID_METADATA_KEY: String = "kxrpc-test-call-id"
}
