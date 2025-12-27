/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.serialization

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.server.GrpcServer
import kotlinx.rpc.grpc.annotations.Grpc
import kotlinx.rpc.grpc.codec.MessageCodecResolver
import kotlinx.rpc.withService
import kotlin.reflect.KClass

abstract class  BaseGrpcServiceTest {
    protected inline fun <@Grpc reified Service : Any> runServiceTest(
        resolver: MessageCodecResolver,
        impl: Service,
        noinline block: suspend (Service) -> Unit,
    ) {
        runServiceTest(Service::class, resolver, impl, block)
    }

    protected fun <@Grpc Service : Any> runServiceTest(
        kClass: KClass<Service>,
        resolver: MessageCodecResolver,
        impl: Service,
        block: suspend (Service) -> Unit,
    ) = runTest {
        val server = GrpcServer(
            port = PORT,
            parentContext = coroutineContext,
        ) {
            messageCodecResolver = resolver
            services {
                registerService(kClass) { impl }
            }
        }

        server.start()

        val client = GrpcClient("localhost", PORT) {
            messageCodecResolver = resolver
            credentials = plaintext()
        }

        val service = client.withService(kClass)

        block(service)

        client.shutdown()
        client.awaitTermination()
        server.shutdown()
        server.awaitTermination()
    }

    companion object {
        const val PORT = 8082
    }
}

suspend fun doWork(): String {
    delay(1)
    return "qwerty"
}
