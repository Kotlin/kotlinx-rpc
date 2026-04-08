/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.rpc.grpc.marshaller.grpcMarshallerOf
import kotlinx.rpc.protobuf.ProtoConfig
import kotlinx.rpc.protobuf.ProtoExtensionRegistry
import test.submsg.asInternal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProtoConfigDslTest {

    @Test
    fun `DSL builder creates config with default values`() {
        val config = ProtoConfig {}

        assertEquals(false, config.discardUnknownFields)
        assertEquals(ProtoConfig.DEFAULT_RECURSION_LIMIT, config.recursionLimit)
        assertNull(config.extensionRegistry)
    }

    @Test
    fun `DSL builder sets discardUnknownFields`() {
        val config = ProtoConfig {
            discardUnknownFields = true
        }

        assertEquals(true, config.discardUnknownFields)
    }

    @Test
    fun `DSL builder sets recursionLimit`() {
        val config = ProtoConfig {
            recursionLimit = 50
        }

        assertEquals(50, config.recursionLimit)
    }

    @Test
    fun `DSL builder sets extensionRegistry directly`() {
        val registry = ProtoExtensionRegistry {}
        val config = ProtoConfig {
            extensionRegistry = registry
        }

        assertEquals(registry, config.extensionRegistry)
    }

    @Test
    fun `DSL builder configures extensions inline`() {
        val config = ProtoConfig {
            extensions {}
        }

        assertTrue(config.extensionRegistry != null)
    }

    @Test
    fun `DSL builder sets multiple properties`() {
        val config = ProtoConfig {
            discardUnknownFields = true
            recursionLimit = 25
        }

        assertEquals(true, config.discardUnknownFields)
        assertEquals(25, config.recursionLimit)
    }

    @Test
    fun `DSL config works with grpcMarshallerOf for discardUnknownFields`() {
        val all = UnknownFieldsAll {
            field1 = 123
            intMissing = 456
        }

        val encoded = grpcMarshallerOf<UnknownFieldsAll>().encode(all)
        val discardMarshaller = grpcMarshallerOf<UnknownFieldsSubset>(ProtoConfig { discardUnknownFields = true })

        val subsetDiscarded = discardMarshaller.decode(encoded)
        assertEquals(0L, subsetDiscarded.asInternal()._unknownFields.size)
        assertEquals(all.field1, subsetDiscarded.field1)
    }
}
