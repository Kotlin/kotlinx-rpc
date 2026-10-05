/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class)

package kotlinx.rpc.protobuf.internal

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.plus
import kotlinx.cinterop.pointed
import kotlinx.cinterop.value

/**
 * A protobuf input over a borrowed native buffer. Valid only within its reader's buffer scope.
 */
internal class NativeBufferZeroCopyInput(private val base: CPointer<ByteVar>?, private val length: Int) : DecoderInput {
    init {
        require(length >= 0)
        require(length == 0 || base != null)
    }

    var position: Int = 0
        private set
    private var lastChunkSize: Int = 0

    override val availableSize: Long get() = length.toLong()

    override fun next(data: CPointer<CPointerVar<ByteVar>>, size: CPointer<IntVar>): Boolean {
        if (position == length) return false
        val count = length - position
        data.pointed.value = checkNotNull(base) + position
        size.pointed.value = count
        position = length
        lastChunkSize = count
        return true
    }

    override fun backUp(count: Int) {
        // C++ declares this callback void. Invalid requests cannot throw through that frame.
        if (count !in 0..lastChunkSize) return
        position -= count
        lastChunkSize = 0
    }

    override fun skip(count: Int): Boolean {
        lastChunkSize = 0
        if (count < 0 || count > length - position) return false
        position += count
        return true
    }

    override fun byteCount(): Long = position.toLong()

    override fun close() = Unit
}
