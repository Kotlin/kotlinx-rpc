/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.e2e

import com.google.protobuf.kotlin.Empty
import io.grpc.testing.integration.TestService
import io.grpc.testing.integration.UnimplementedService
import kotlinx.coroutines.coroutineScope
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.client.internal.unaryRpc
import kotlinx.rpc.grpc.descriptor.GrpcMethodType
import kotlinx.rpc.grpc.internal.serviceDescriptor
import kotlinx.rpc.grpc.server.GrpcMutableHandlerRegistry
import kotlinx.rpc.grpc.server.internal.unaryServerMethodDefinition
import kotlinx.rpc.grpc.server.serverServiceDefinition
import kotlinx.rpc.grpc.server.testing.assertGrpcStatus
import kotlinx.rpc.grpc.server.testing.grpcServerTest
import kotlinx.rpc.grpc.server.testing.rawMethod
import kotlinx.rpc.grpc.statusCode
import kotlinx.rpc.registerService
import kotlin.reflect.typeOf
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ServerRegistryTest {
    @Test
    fun unknownServiceReturnsUnimplemented() = grpcServerTest {
        start()

        assertGrpcStatus(GrpcStatusCode.UNIMPLEMENTED) { unimplementedService.unimplementedCall(Empty {}) }

        assertEquals(emptyList(), observer.calls)
    }

    @Test
    fun unimplementedMethodReturnsUnimplemented() = grpcServerTest {
        start()

        assertGrpcStatus(GrpcStatusCode.UNIMPLEMENTED) { testService.unimplementedCall(Empty {}) }

        val close = observer.awaitCall("grpc.testing.TestService/UnimplementedCall").awaitClose()
        assertEquals(GrpcStatusCode.UNIMPLEMENTED, close.status.statusCode)
    }

    @Test
    fun fallbackRegistryHandlesAnUnknownService() = grpcServerTest {
        coroutineScope {
            val method = rawMethod("fallback.Service/Echo", GrpcMethodType.UNARY)
            val registry = GrpcMutableHandlerRegistry()
            registry.addService(serverServiceDefinition(
                serviceDescriptor("fallback.Service", listOf(method), null),
                listOf(unaryServerMethodDefinition(method, typeOf<ByteArray>(), emptyList()) { it }),
            ))
            start(serverConfig = { fallbackHandlerRegistry = registry })

            val response = client.unaryRpc(method, byteArrayOf(1, 2, 3))

            assertContentEquals(byteArrayOf(1, 2, 3), response)
            assertEquals(emptyList(), observer.calls)
        }
    }

    @Test
    @Ignore // https://youtrack.jetbrains.com/issue/KXRPC-720
    fun serviceRegisteredAfterStartAnswersCalls() = grpcServerTest {
        start()
        assertGrpcStatus(GrpcStatusCode.UNIMPLEMENTED) { unimplementedService.unimplementedCall(Empty {}) }

        server.registerService<UnimplementedService> {
            object : UnimplementedService {
                override suspend fun unimplementedCall(message: Empty): Empty = message
            }
        }

        assertEquals(Empty {}, unimplementedService.unimplementedCall(Empty {}))
        val close = observer.awaitCall("grpc.testing.UnimplementedService/UnimplementedCall").awaitClose()
        assertEquals(GrpcStatusCode.OK, close.status.statusCode)
    }

    @Test
    @Ignore // https://youtrack.jetbrains.com/issue/KXRPC-720
    fun deregisteredServiceReturnsUnimplemented() = grpcServerTest {
        start()
        server.registerService<UnimplementedService> {
            object : UnimplementedService {
                override suspend fun unimplementedCall(message: Empty): Empty = message
            }
        }
        unimplementedService.unimplementedCall(Empty {})

        server.deregisterService(UnimplementedService::class)

        assertGrpcStatus(GrpcStatusCode.UNIMPLEMENTED) { unimplementedService.unimplementedCall(Empty {}) }
    }

    @Test
    @Ignore // https://youtrack.jetbrains.com/issue/KXRPC-720
    fun serviceDeregisteredAfterInitialRegistrationReturnsUnimplemented() = grpcServerTest {
        start()
        testService.emptyCall(Empty {})

        server.deregisterService(TestService::class)

        assertGrpcStatus(GrpcStatusCode.UNIMPLEMENTED) { testService.emptyCall(Empty {}) }
    }
}
