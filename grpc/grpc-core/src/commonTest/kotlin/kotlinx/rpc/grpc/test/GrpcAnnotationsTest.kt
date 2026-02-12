/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test

import kotlinx.io.Source
import kotlinx.rpc.descriptor.serviceDescriptorOf
import kotlinx.rpc.grpc.annotations.Grpc
import kotlinx.rpc.grpc.codec.CodecConfig
import kotlinx.rpc.grpc.codec.EmptyMessageCodecResolver
import kotlinx.rpc.grpc.codec.MessageCodec
import kotlinx.rpc.grpc.codec.MessageCodecResolver
import kotlinx.rpc.grpc.descriptor.GrpcServiceDescriptor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Grpc("grpc.test")
interface GrpcAnnotationsService {
    @Suppress("unused")
    @Grpc.Method(name = "Empty", safe = true, idempotent = true, sampledToLocalTracing = false)
    suspend fun empty()
}

class GrpcAnnotationsTest {
    @Test
    fun nullCodec() {
        assertFailsWith<IllegalArgumentException> {
            val descriptor = serviceDescriptorOf<GrpcAnnotationsService>()
                    as GrpcServiceDescriptor<GrpcAnnotationsService>

            descriptor.delegate(EmptyMessageCodecResolver, null)
        }
    }

    @Test
    fun methodAnnotations() {
        val descriptor = serviceDescriptorOf<GrpcAnnotationsService>()
                as GrpcServiceDescriptor<GrpcAnnotationsService>
        val methodDescriptor = descriptor
            .delegate(unitCodec, null)
            .getMethodDescriptor("Empty")

        assertNotNull(methodDescriptor)

        assertEquals("grpc.test.GrpcAnnotationsService/Empty", methodDescriptor.getFullMethodName())
        assertTrue(methodDescriptor.isSafe())
        assertTrue(methodDescriptor.isIdempotent())
        assertFalse(methodDescriptor.isSampledToLocalTracing())
    }
}

private val unitCodec = MessageCodecResolver {
    object : MessageCodec<Unit> {
        override fun encode(value: Unit, config: CodecConfig?): Source {
            TODO("Not yet implemented")
        }

        override fun decode(source: Source, config: CodecConfig?) {
            TODO("Not yet implemented")
        }
    }
}

