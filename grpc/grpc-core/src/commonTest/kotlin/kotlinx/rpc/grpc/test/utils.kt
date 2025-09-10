/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test

import kotlinx.rpc.grpc.StatusCode
import kotlinx.rpc.grpc.StatusException
import kotlinx.rpc.grpc.statusCode
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


fun assertGrpcFailure(statusCode: StatusCode, message: String? = null, block: () -> Unit) {
    val exc = assertFailsWith<StatusException>(message) { block() }
    assertEquals(statusCode, exc.getStatus().statusCode)
    if (message != null) {
        assertContains(message, exc.getStatus().getDescription() ?: "")
    }
}
