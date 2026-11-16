/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import com.google.protobuf.kotlin.Empty
import com.google.protobuf.kotlin.invoke
import io.grpc.testing.integration.TestService
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

class GrpcClientTlsTest {
    @Test
    fun defaultCredentialsTrustPublicCertificate() = runTest {
        // TODO: Use a test server controlled by us (KRPC-215).
        val client = GrpcClient("grpc-test.sandbox.googleapis.com", 443)
        try {
            val service = client.withService<TestService>()
            assertEquals(Empty {}, service.emptyCall(Empty {}))
        } finally {
            client.shutdownNow()
            client.awaitTermination(30.seconds)
        }
    }
}
