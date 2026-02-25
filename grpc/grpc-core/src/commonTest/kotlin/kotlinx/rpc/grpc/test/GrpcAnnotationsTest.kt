/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test

import kotlinx.io.Source
import kotlinx.rpc.descriptor.serviceDescriptorOf
import kotlinx.rpc.grpc.annotations.Grpc
import kotlinx.rpc.grpc.marshaller.MarshallerConfig
import kotlinx.rpc.grpc.marshaller.EmptyMessageMarshallerResolver
import kotlinx.rpc.grpc.marshaller.MessageMarshaller
import kotlinx.rpc.grpc.marshaller.MessageMarshallerResolver
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
    fun nullMarshaller() {
        assertFailsWith<IllegalArgumentException> {
            val descriptor = serviceDescriptorOf<GrpcAnnotationsService>()
                    as GrpcServiceDescriptor<GrpcAnnotationsService>

            descriptor.delegate(EmptyMessageMarshallerResolver, null)
        }
    }

    @Test
    fun methodAnnotations() {
        val descriptor = serviceDescriptorOf<GrpcAnnotationsService>()
                as GrpcServiceDescriptor<GrpcAnnotationsService>
        val methodDescriptor = descriptor
            .delegate(unitMarshaller, null)
            .getMethodDescriptor("Empty")

        assertNotNull(methodDescriptor)

        assertEquals("grpc.test.GrpcAnnotationsService/Empty", methodDescriptor.getFullMethodName())
        assertTrue(methodDescriptor.isSafe())
        assertTrue(methodDescriptor.isIdempotent())
        assertFalse(methodDescriptor.isSampledToLocalTracing())
    }
}

private val unitMarshaller = MessageMarshallerResolver {
    object : MessageMarshaller<Unit> {
        override fun encode(value: Unit, config: MarshallerConfig?): Source {
            TODO("Not yet implemented")
        }

        override fun decode(source: Source, config: MarshallerConfig?) {
            TODO("Not yet implemented")
        }
    }
}

