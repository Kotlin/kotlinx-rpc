/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import OneOfNumeric32
import OneOfNumeric32Internal
import OneOfNumeric64
import OneOfNumeric64Internal
import PlainNumeric
import PlainNumericInternal
import decodeWith
import invoke
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.rpc.grpc.marshaller.grpcMarshallerOf
import kotlinx.rpc.protobuf.internal.WireDecoder
import java.lang.management.ManagementFactory
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Setting, reading and decoding a numeric oneof member must not allocate anything beyond the message itself:
 * the value lives in a primitive slot of the internal class, no wrapper object and no boxing.
 */
class OneOfAllocationTest {
    private val threadBean = ManagementFactory.getThreadMXBean() as? com.sun.management.ThreadMXBean

    private inline fun allocatedBytes(iterations: Int, block: () -> Unit): Long {
        val bean = threadBean ?: return 0
        val id = Thread.currentThread().id
        // warm up so the JIT does not allocate during measurement
        repeat(iterations) { block() }
        val before = bean.getThreadAllocatedBytes(id)
        repeat(iterations) { block() }
        return bean.getThreadAllocatedBytes(id) - before
    }

    @Test
    fun settingAndReadingNumericMembersDoesNotAllocate() {
        if (threadBean == null) return

        val msg32 = OneOfNumeric32 { } as OneOfNumeric32Internal
        val msg64 = OneOfNumeric64 { } as OneOfNumeric64Internal
        var sink = 0L
        val iterations = 1_000_000

        val bytes = allocatedBytes(iterations) {
            msg32.i32 = 123_456
            sink += msg32.i32
            msg32.f = 1.5f
            sink += msg32.f.toRawBits()
            msg32.enum = MyEnum.THREE
            sink += msg32.enum.number
            msg64.u64 = 9_000_000_000uL
            sink += msg64.u64.toLong()
            msg64.d = 2.5
            sink += msg64.d.toRawBits()
        }

        assertTrue(sink != 0L)
        assertTrue(
            actual = bytes < 64 * 1024,
            message = "set/read of numeric oneof members allocated $bytes bytes over $iterations iterations",
        )
    }

    @Test
    fun decodingANumericOneOfAllocatesOnlyTheMessage() {
        if (threadBean == null) return

        // both messages have the same wire shape: field 1, varint 123456
        val oneOfBytes = grpcMarshallerOf<OneOfNumeric32>().encode(OneOfNumeric32 { i32 = 123_456 }).readByteArray()
        val plainBytes = grpcMarshallerOf<PlainNumeric>().encode(PlainNumeric { i32 = 123_456 }).readByteArray()
        assertTrue(oneOfBytes.contentEquals(plainBytes))

        val iterations = 200_000
        var sink = 0L

        val oneOfAllocated = allocatedBytes(iterations) {
            val buffer = Buffer()
            buffer.write(oneOfBytes)
            WireDecoder(buffer).use { decoder ->
                val msg = OneOfNumeric32Internal()
                OneOfNumeric32Internal.decodeWith(msg, decoder, null)
                sink += msg.i32
            }
        }

        val plainAllocated = allocatedBytes(iterations) {
            val buffer = Buffer()
            buffer.write(plainBytes)
            WireDecoder(buffer).use { decoder ->
                val msg = PlainNumericInternal()
                PlainNumericInternal.decodeWith(msg, decoder, null)
                sink += msg.i32
            }
        }

        assertTrue(sink != 0L)
        val oneOfPerDecode = oneOfAllocated / iterations
        val plainPerDecode = plainAllocated / iterations
        // the plain message additionally allocates a MsgFieldDelegate per field, the oneof message must not
        // allocate anything for its value: it cannot allocate more than the plain baseline.
        assertTrue(
            oneOfPerDecode <= plainPerDecode,
            "decoding a numeric oneof allocated $oneOfPerDecode bytes per message, plain field: $plainPerDecode",
        )
    }
}
