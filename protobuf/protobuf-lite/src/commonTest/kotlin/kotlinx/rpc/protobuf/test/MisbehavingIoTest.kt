/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.rpc.grpc.marshaller.GrpcMessageReader
import kotlinx.rpc.grpc.marshaller.GrpcMessageWriter
import kotlinx.rpc.grpc.marshaller.grpcMarshallerOf
import kotlinx.rpc.protobuf.ProtobufDecodingException
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

/**
 * Readers and writers that break the contract of [GrpcMessageReader] or [GrpcMessageWriter].
 *
 * The codec must throw an exception for each of them. It must never write or read outside its own memory.
 */
class MisbehavingIoTest {
    private val marshaller = grpcMarshallerOf<AllPrimitives>()
    private val cases = codecCases()
    private val small = cases[0].message as AllPrimitives
    private val smallBytes = cases[0].bytes
    private val large = cases[1].message as AllPrimitives
    private val largeBytes = cases[1].bytes

    @Test
    fun readerWithNegativeRemaining() {
        assertContractFailure { decode(FakeReader(smallBytes, report = { -1 })) }
    }

    @Test
    fun readerThatDoesNotReduceRemaining() {
        assertContractFailure { decode(FakeReader(smallBytes, consume = false)) }
    }

    @Test
    fun readerThatReducesRemainingByTooMuch() {
        assertContractFailure { decode(FakeReader(largeBytes, report = { afterFirstRead(it, 0) })) }
    }

    @Test
    fun readerWhoseRemainingTurnsNegative() {
        assertContractFailure { decode(FakeReader(largeBytes, report = { afterFirstRead(it, -3) })) }
    }

    @Test
    fun readerWithLargerRemainingThanItsBytes() {
        // The reader gives zeros after its bytes, and a zero tag is invalid.
        val extra = Int.MAX_VALUE - largeBytes.size
        assertFailsWith<ProtobufDecodingException> { decode(FakeReader(smallBytes, report = { it + 10 })) }
        assertFailsWith<ProtobufDecodingException> { decode(FakeReader(largeBytes, report = { it + extra })) }
    }

    @Test
    fun readerWithSmallerRemainingThanItsBytes() {
        assertFailsWith<ProtobufDecodingException> { decode(FakeReader(smallBytes, report = { maxOf(0, it - 5) })) }
    }

    @Test
    fun readerFailureGoesToTheCaller() {
        val failure = ImplementationFailure()
        val reader = FakeReader(largeBytes, failure = failure)
        assertSame(failure, assertFailsWith<ImplementationFailure> { decode(reader) })
    }

    @Test
    fun writerWithOtherRemainingThanTheMessageSize() {
        val size = smallBytes.size
        assertFailsWith<IllegalArgumentException> { encode(small, FakeWriter(size, report = { size - it - 1 })) }
        assertFailsWith<IllegalArgumentException> { encode(small, FakeWriter(size, report = { -1 })) }
    }

    @Test
    fun writerThatDoesNotReduceRemaining() {
        assertContractFailure { encode(small, FakeWriter(smallBytes.size, advance = false)) }
    }

    @Test
    fun writerWhoseRemainingChangesAfterTheFirstWrite() {
        val size = largeBytes.size
        for (after in listOf(-7, 0, Int.MAX_VALUE)) {
            val writer = FakeWriter(size, report = { written -> afterFirstWrite(written, size, after) })
            assertContractFailure { encode(large, writer) }
        }
    }

    @Test
    fun writerFailureGoesToTheCaller() {
        val failure = ImplementationFailure()
        val writer = FakeWriter(largeBytes.size, failure = failure)
        assertSame(failure, assertFailsWith<ImplementationFailure> { encode(large, writer) })
    }

    private fun decode(reader: GrpcMessageReader): AllPrimitives = marshaller.decode(reader)

    private fun encode(message: AllPrimitives, writer: GrpcMessageWriter) {
        marshaller.prepare(message).writeTo(writer)
    }

    /** The `remaining` of a reader over [largeBytes] that reports [after] when it has read some bytes. */
    private fun afterFirstRead(unread: Int, after: Int): Int = if (unread < largeBytes.size) {
        after
    } else {
        unread
    }

    /** The `remaining` of a writer of [size] bytes that reports [after] when it has written some bytes. */
    private fun afterFirstWrite(written: Int, size: Int, after: Int): Int = if (written > 0) {
        after
    } else {
        size - written
    }

    private fun assertContractFailure(block: () -> Unit) {
        val failure = assertFailsWith<IllegalStateException>(block = block)
        assertContains(failure.message.orEmpty(), "remaining bytes")
    }
}

private class ImplementationFailure : RuntimeException("implementation failure")

/**
 * A [GrpcMessageReader] over [bytes] that can break the reader contract:
 *
 * - [report]: gives `remaining` from the real number of unread bytes.
 * - [consume]: if `false`, a read doesn't move the read position.
 * - [failure]: if not `null`, each read throws it.
 *
 * A read past the end of [bytes] gives zeros, and the number of unread bytes becomes negative.
 */
private class FakeReader(
    private val bytes: ByteArray,
    private val consume: Boolean = true,
    private val failure: Throwable? = null,
    private val report: (unread: Int) -> Int = { it },
) : GrpcMessageReader {
    private var position = 0

    override val size: Int get() = bytes.size

    override val remaining: Int get() = report(bytes.size - position)

    override fun readByte(): Byte {
        val result = ByteArray(1)
        readTo(result)
        return result[0]
    }

    override fun readTo(bytes: ByteArray, startIndex: Int, endIndex: Int) {
        failure?.let { throw it }
        for (index in startIndex until endIndex) {
            val source = position + index - startIndex
            bytes[index] = if (source < this.bytes.size) {
                this.bytes[source]
            } else {
                0
            }
        }
        if (consume) {
            position += endIndex - startIndex
        }
    }

    override fun readByteArray(byteCount: Int): ByteArray = ByteArray(byteCount).also { readTo(it) }

    override fun asSource(): Source = Buffer()
}

/**
 * A [GrpcMessageWriter] of [size] bytes that doesn't keep the bytes and can break the writer contract:
 *
 * - [report]: gives `remaining` from the real number of written bytes.
 * - [advance]: if `false`, a write doesn't count its bytes.
 * - [failure]: if not `null`, each write throws it.
 */
private class FakeWriter(
    override val size: Int,
    private val advance: Boolean = true,
    private val failure: Throwable? = null,
    private val report: (written: Int) -> Int = { size - it },
) : GrpcMessageWriter {
    override var written: Int = 0
        private set

    override val remaining: Int get() = report(written)

    override fun writeByte(byte: Byte) {
        add(1)
    }

    override fun write(bytes: ByteArray, startIndex: Int, endIndex: Int) {
        add(endIndex - startIndex)
    }

    override fun write(source: Buffer, byteCount: Long) {
        add(byteCount.toInt())
    }

    private fun add(count: Int) {
        failure?.let { throw it }
        if (advance) {
            written += count
        }
    }
}
