/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class)

package kotlinx.rpc.protobuf.test

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.free
import kotlinx.cinterop.get
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.plus
import kotlinx.cinterop.set
import kotlinx.rpc.grpc.marshaller.internal.NativeBufferMessageReader
import kotlinx.rpc.grpc.marshaller.internal.NativeBufferMessageWriter
import kotlin.test.assertEquals

/** A [NativeBufferMessageReader] over a native copy of [bytes]. Call [release] after use. */
internal class HeapReader(bytes: ByteArray) : NativeBufferMessageReader(bytes.size) {
    private val storage = nativeHeap.allocArray<ByteVar>(maxOf(size, 1))

    init {
        for (index in bytes.indices) {
            storage[index] = bytes[index]
        }
    }

    override fun accessBuffer(block: (CPointer<ByteVar>?) -> Unit) {
        block(if (size == 0) { null } else { storage })
    }

    /** Sets all bytes of the native copy to 0. */
    fun overwrite() {
        for (index in 0 until size) {
            storage[index] = 0
        }
    }

    fun release() {
        nativeHeap.free(storage)
    }
}

/**
 * A [NativeBufferMessageWriter] over native memory of [size] bytes. Call [release] after use.
 *
 * The memory has a guard byte before and after it. [release] checks that the guard bytes didn't change.
 */
internal class HeapMessageWriter(size: Int) : NativeBufferMessageWriter(size) {
    private val allocation = nativeHeap.allocArray<ByteVar>(size + 2)
    private var released = false

    /** The number of [accessBuffer] calls. */
    var scopes = 0
        private set

    init {
        allocation[0] = GUARD
        allocation[size + 1] = GUARD
    }

    override fun accessBuffer(block: (CPointer<ByteVar>?) -> Unit) {
        scopes++
        block(allocation + 1)
    }

    fun release() {
        if (!released) {
            assertGuardsIntact()
            released = true
            nativeHeap.free(allocation)
        }
    }

    /** A copy of the written memory. */
    fun snapshot(): ByteArray = ByteArray(size) { allocation[it + 1] }

    fun assertGuardsIntact() {
        assertEquals(GUARD, allocation[0])
        assertEquals(GUARD, allocation[size + 1])
    }

    private companion object {
        const val GUARD: Byte = 0x5a
    }
}
