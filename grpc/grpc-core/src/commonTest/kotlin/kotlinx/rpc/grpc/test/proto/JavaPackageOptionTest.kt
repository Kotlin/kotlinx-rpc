/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.proto

import com.google.protobuf.kotlin.Empty
import com.google.protobuf.kotlin.EmptyInternal
import com.google.protobuf.kotlin.invoke
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.GrpcClient
import kotlinx.rpc.grpc.internal.MethodType
import kotlinx.rpc.grpc.internal.methodDescriptor
import kotlinx.rpc.grpc.internal.unaryRpc
import kotlinx.rpc.grpc.test.withJavaPkg.TheService
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.test.Test

class TheServiceImpl : TheService {
    override suspend fun TheMethod(message: Empty): Empty {
        return Empty {}
    }
}

/**
 * Tests proto service with java_package that differs from the `package` name.
 * While the generated Kotlin sources should be generated in the java_package,
 * the service name must use the `package` name.
 */
class JavaPackageOptionTest : GrpcProtoTest() {

    /**
     * Tests that the generated service descriptor uses the `package` name.
     */
    @Test
    fun testJavaPackageOptionRaw() = runGrpcTest { client ->
        val descriptor = methodDescriptor(
            fullMethodName = "protopackage.TheService/TheMethod",
            requestCodec = EmptyInternal.CODEC,
            responseCodec = EmptyInternal.CODEC,
            type = MethodType.UNARY,
            schemaDescriptor = Unit,
            idempotent = true,
            safe = true,
            sampledToLocalTracing = true,
        )

        client.unaryRpc(descriptor, Empty {})

        // just reach this without an error
    }

    /**
     * Tests that the generated client uses the `package` name to call the service.
     */
    @Test
    fun testJavaPackageOptionStub() = runGrpcTest { client ->
        val service = client.withService<TheService>()
        service.TheMethod(Empty {})

        // just reach this without an error
    }

    override fun RpcServer.registerServices() {
        registerService<TheService> { TheServiceImpl() }
    }
}