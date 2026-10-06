/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(kotlinx.rpc.internal.utils.InternalRpcApi::class)

package kotlinx.rpc.protobuf.test

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.rpc.grpc.marshaller.grpcMarshallerOf
import kotlinx.rpc.grpc.marshaller.internal.BufferMessageWriter
import kotlinx.rpc.grpc.marshaller.internal.OutputStreamMessageWriter
import java.io.ByteArrayOutputStream
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class OutputStreamMarshallerTest {
    @Test
    fun protobufWritesIdenticalBytesDirectlyToOutputStream() {
        val message = AllPrimitives {
            int32 = -123
            uint64 = 123456789uL
            string = "large payload ".repeat(1000)
        }
        val marshaller = grpcMarshallerOf<AllPrimitives>()
        val encoded = marshaller.prepare(message)
        val buffer = Buffer()
        marshaller.prepare(message).writeTo(BufferMessageWriter(buffer, encoded.size))
        val expected = buffer.readByteArray()

        val output = ByteArrayOutputStream()
        val writer = OutputStreamMessageWriter(output, encoded.size)
        encoded.writeTo(writer)

        assertEquals(0, writer.remaining)
        assertContentEquals(expected, output.toByteArray())
    }

    @Test
    fun emptyProtobufWritesNoBytes() {
        val marshaller = grpcMarshallerOf<AllPrimitives>()
        val encoded = marshaller.prepare(AllPrimitives {})
        val output = ByteArrayOutputStream()
        val writer = OutputStreamMessageWriter(output, encoded.size)
        encoded.writeTo(writer)

        assertEquals(0, writer.remaining)
        assertContentEquals(ByteArray(0), output.toByteArray())
    }
}
