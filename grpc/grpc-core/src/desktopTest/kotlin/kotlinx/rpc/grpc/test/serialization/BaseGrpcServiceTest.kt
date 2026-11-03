/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.serialization

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.grpc.GrpcService
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.server.GrpcServer
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerResolver
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.seconds

abstract class  BaseGrpcServiceTest {
    protected inline fun <reified Service : GrpcService<Service>> runServiceTest(
        resolver: GrpcMarshallerResolver,
        impl: Service,
        noinline block: suspend (Service) -> Unit,
    ) {
        runServiceTest(Service::class, resolver, impl, block)
    }

    protected fun <Service : GrpcService<Service>> runServiceTest(
        kClass: KClass<Service>,
        resolver: GrpcMarshallerResolver,
        impl: Service,
        block: suspend (Service) -> Unit,
    ) = runTest {
        val server = GrpcServer(
            port = 0,
            parentContext = coroutineContext,
        ) {
            messageMarshallerResolver = resolver
            services {
                registerService(kClass) { impl }
            }
        }

        server.start()

        val client = GrpcClient("localhost", server.port) {
            messageMarshallerResolver = resolver
            credentials = plaintext()
        }

        val service = client.service(kClass)

        try {
            block(service)
        } finally {
            client.shutdownNow()
            server.shutdownNow()
            server.awaitTermination(30.seconds)
            client.awaitTermination(30.seconds)
        }
    }
}

suspend fun doWork(): String {
    delay(1)
    return "qwerty"
}
