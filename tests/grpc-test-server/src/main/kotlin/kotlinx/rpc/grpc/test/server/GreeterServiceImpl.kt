/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import kotlinx.coroutines.delay
import kotlinx.rpc.grpc.test.GreeterService
import kotlinx.rpc.grpc.test.HelloReply
import kotlinx.rpc.grpc.test.HelloRequest
import kotlinx.rpc.grpc.test.invoke

internal class GreeterServiceImpl : GreeterService {
    override suspend fun SayHello(message: HelloRequest): HelloReply {
        delay(message.timeout?.toLong() ?: 0)
        return HelloReply {
            this.message = "Hello ${message.name}"
        }
    }
}