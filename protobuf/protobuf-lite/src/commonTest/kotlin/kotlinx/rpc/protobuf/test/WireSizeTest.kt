/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.rpc.protobuf.internal.WireSize
import kotlinx.rpc.protobuf.internal.WireTag
import kotlinx.rpc.protobuf.internal.WireType
import kotlin.test.Test
import kotlin.test.assertEquals

class WireSizeTest {
    @Test
    fun tagSizeAtVarintBoundaries() {
        val cases = listOf(
            1 to 1,
            15 to 1,
            16 to 2,
            2_047 to 2,
            2_048 to 3,
            262_143 to 3,
            262_144 to 4,
            33_554_431 to 4,
            33_554_432 to 5,
            268_435_456 to 5,
            536_870_911 to 5,
        )
        for ((fieldNumber, expectedSize) in cases) {
            for (wireType in WireType.VARINT..WireType.FIXED32) {
                val tag = WireTag((fieldNumber shl 3) or wireType)
                assertEquals(expectedSize, WireSize.tag(tag), "$fieldNumber, $wireType")
                assertEquals(expectedSize, WireSize.tag(fieldNumber, wireType), "$fieldNumber, $wireType")
            }
        }
    }
}
