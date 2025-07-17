/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.rpc.grpc.internal.protowire.WireDecoder
import kotlinx.rpc.grpc.internal.protowire.WireEncoder
import kotlinx.rpc.grpc.internal.protowire.WireType
import kotlin.test.Test

@OptIn(ExperimentalForeignApi::class)
class ProtoWireTest {

    @Test
    fun testEncodeDecodeBool() {

        val fieldNr = 3;

        val encoder = WireEncoder()
        encoder.write(fieldNr, true)
        encoder.write(4, "Hello Test")

        val encodedBuffer = encoder.buffer.get()
        val decoder = WireDecoder(encodedBuffer)

        val t1 = decoder.readTag()!!
        check(t1.wireType == WireType.VARINT)
        check(t1.fieldNr == fieldNr)

        val bool = decoder.readBool()!!
        check(bool)

        val t2 = decoder.readTag()!!
        check(t2.wireType == WireType.LENGTH_DELIMITED)
        check(t2.fieldNr == 4)

        val str = decoder.readString()!!
        check(str == "Hello Test")


    }
}
