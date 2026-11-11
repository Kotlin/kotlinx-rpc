/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.rpc.grpc.marshaller.internal.ByteArrayMessageReader
import kotlin.test.Test

class NativeCodecTest {
    @Test
    fun encodesToNativeMemory() {
        for (case in codecCases()) {
            val writer = HeapMessageWriter(case.bytes.size)
            try {
                case.checkEncode(writer, "HeapMessageWriter") { writer.snapshot() }
            } finally {
                writer.release()
            }
        }
    }

    @Test
    fun decodesFromNativeMemory() {
        for (case in codecCases()) {
            withReader(case.bytes) { case.checkDecode(it, "HeapReader") }
        }
    }

    @Test
    fun decodeErrorsMatchByteArrayReader() {
        for (case in decodeErrorCases()) {
            val expected = case.decode(ByteArrayMessageReader(case.bytes))
            withReader(case.bytes) { assertSameOutcome(expected, case.decode(it), case.name) }
        }
    }

    private inline fun withReader(bytes: ByteArray, action: (HeapReader) -> Unit) {
        val reader = HeapReader(bytes)
        try {
            action(reader)
        } finally {
            reader.close()
            reader.release()
        }
    }
}
