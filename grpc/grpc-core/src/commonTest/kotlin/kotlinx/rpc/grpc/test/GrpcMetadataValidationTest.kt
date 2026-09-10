/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test

import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.appendBinary
import kotlin.test.Test
import kotlin.test.assertFailsWith

class GrpcMetadataValidationTest {
    @Test
    fun emptyKeyIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            GrpcMetadata().append("", "value")
        }
    }

    @Test
    fun binarySuffixWithoutBaseNameIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            GrpcMetadata().appendBinary("-bin", byteArrayOf())
        }
    }
}
