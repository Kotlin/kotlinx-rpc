/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import com.google.protobuf.CodedInputStream
import com.google.protobuf.CodedOutputStream

@OptIn(ExperimentalUnsignedTypes::class)
internal class WireDecoder(val buffer: ByteArray) {
    private val cos = CodedInputStream.newInstance(buffer)

    fun readTag(): Int {
        return cos.readTag()
    }

    fun readInt32(): Int {
        return cos.readRawVarint32()
    }
}