/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test

import kotlinx.io.Source
import kotlinx.rpc.descriptor.serviceDescriptorOf
import kotlinx.rpc.grpc.annotations.Grpc
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.grpc.marshaller.GrpcEmptyMarshallerResolver
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerResolver
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

            descriptor.delegate(GrpcEmptyMarshallerResolver, null)
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

private val unitMarshaller = GrpcMarshallerResolver {
    object : GrpcMarshaller<Unit> {
        override fun encode(value: Unit, config: GrpcMarshallerConfig?): Source {
            TODO("Not yet implemented")
        }

        override fun decode(source: Source, config: GrpcMarshallerConfig?) {
            TODO("Not yet implemented")
        }
    }
}

