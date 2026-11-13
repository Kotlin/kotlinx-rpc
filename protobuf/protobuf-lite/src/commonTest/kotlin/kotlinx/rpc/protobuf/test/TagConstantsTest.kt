/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.io.Buffer
import kotlinx.rpc.grpc.marshaller.decodeFromSource
import kotlinx.rpc.grpc.marshaller.encodeToByteArray
import kotlinx.rpc.grpc.marshaller.grpcMarshallerOf
import kotlinx.rpc.protobuf.internal.WireSize
import kotlinx.rpc.protobuf.internal.encodeWire
import tag_constants.CrossFile
import tag_constants.Left
import tag_constants.Right
import tag_constants.TagConstantNames
import tag_constants.TagConstantNamesInternalTags
import tag_constants.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

class TagConstantsTest {
    @Test
    fun tagNamesDoNotCollideAcrossFieldsAndNestedMessages() {
        val message = TagConstantNames {
            values = listOf(1, 2, 3)
            valuesPacked = 42
            left = Left.Inner { value = 123 }
            right = Right.Inner { value = "right" }
            shadow = TagConstantNamesInternalTags { value = "shadow" }
            crossFile = CrossFile.Inner { value = 456 }
        }
        val marshaller = grpcMarshallerOf<TagConstantNames>()
        for (kind in readerKinds) {
            val decoded = marshaller.decode(kind.create(marshaller.encodeToByteArray(message)))
            assertEquals(message, decoded, kind.name)
        }
    }

    @Test
    fun acceptsMixedPackedAndUnpackedOccurrencesWithCollidingTagNames() {
        val buffer = Buffer()
        encodeWire(buffer) { encoder ->
            encoder.writeInt32(1, 1)
            encoder.writePackedInt32(1, listOf(2, 3), WireSize.packedInt32(listOf(2, 3)))
            encoder.writeInt32(2, 42)
            encoder.writeInt32(1, 4)
        }
        val decoded = grpcMarshallerOf<TagConstantNames>().decodeFromSource(buffer)
        assertEquals(listOf(1, 2, 3, 4), decoded.values)
        assertEquals(42, decoded.valuesPacked)
    }
}
