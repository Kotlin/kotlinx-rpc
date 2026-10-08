/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.testing

import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

/**
 * Stable description used to diagnose lost, duplicated, or reordered payloads.
 *
 * Failure messages list fingerprints instead of the payload bytes, which can be hundreds of kilobytes. Comparing
 * the expected and actual fingerprint lists shows directly whether a payload is missing, appears twice, is at the
 * wrong position, or has the wrong content.
 *
 * @property sequence One-based position of the payload in its message stream.
 * @property size Payload length in bytes.
 * @property checksum FNV-1a checksum of the payload bytes, see [testChecksum].
 */
internal data class PayloadFingerprint(
    val sequence: Int,
    val size: Int,
    val checksum: ULong,
) {
    override fun toString(): String {
        return "sequence=$sequence, size=$size, checksum=0x${checksum.toString(16).padStart(16, '0')}"
    }
}

/**
 * Produces identical non-uniform bytes on every Kotlin target.
 *
 * Each byte mixes its index with [seed], so the content changes along the payload and between seeds. Unlike
 * zero-filled arrays, this detects payloads which are truncated, shifted, have swapped chunks, or were exchanged
 * with another message of the same size, as long as each message uses a different [seed]. The formula uses only
 * integer arithmetic, so JVM and native tests produce the same bytes.
 */
internal fun deterministicBytes(size: Int, seed: Int = 0): ByteArray {
    require(size >= 0) { "payload size must not be negative" }
    return ByteArray(size) { index ->
        val mixed = index * 31 + seed * 17
        (mixed xor (index ushr 3) xor (seed shl 1)).toByte()
    }
}

/** Computes a stable 64-bit FNV-1a checksum over the bytes without allocating an encoded representation. */
internal fun ByteArray.testChecksum(): ULong {
    var checksum = FNV_OFFSET_BASIS
    for (byte in this) {
        checksum = (checksum xor byte.toUByte().toULong()) * FNV_PRIME
    }
    return checksum
}

/** Associates payload content with its one-based position in a message stream. */
internal fun ByteArray.fingerprint(sequence: Int): PayloadFingerprint {
    require(sequence > 0) { "payload sequence must be one-based" }
    return PayloadFingerprint(sequence, size, testChecksum())
}

/**
 * Compares payload count, order, size, checksum context, and exact bytes.
 *
 * First checks that both lists have the same number of payloads, then compares the payloads position by position.
 * Every failure message starts with [context] and contains the [PayloadFingerprint]s of the payloads involved,
 * so a mismatch can be diagnosed without dumping the bytes.
 *
 * @param context Prefix of the failure messages, for example the call id and server trace.
 */
internal fun assertPayloadSequence(
    expected: List<ByteArray>,
    actual: List<ByteArray>,
    context: String = "payload sequence",
) {
    val expectedFingerprints = expected.mapIndexed { index, bytes -> bytes.fingerprint(index + 1) }
    val actualFingerprints = actual.mapIndexed { index, bytes -> bytes.fingerprint(index + 1) }
    assertEquals(
        expected.size,
        actual.size,
        "$context count mismatch; expected=$expectedFingerprints, actual=$actualFingerprints",
    )
    expected.indices.forEach { index ->
        assertContentEquals(
            expected[index],
            actual[index],
            "$context mismatch at ${expectedFingerprints[index]}; actual=${actualFingerprints[index]}",
        )
    }
}

private const val FNV_OFFSET_BASIS: ULong = 14_695_981_039_346_656_037UL
private const val FNV_PRIME: ULong = 1_099_511_628_211UL
