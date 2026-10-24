/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server.starter.test

import kotlinx.rpc.annotations.Rpc
import kotlinx.serialization.Serializable
import org.springframework.stereotype.Service

@Serializable
data class Greeting(val text: String)

@Rpc
interface EchoService {
    suspend fun echo(value: String): String

    suspend fun greet(greeting: Greeting): Greeting
}

@Service
open class TestService : EchoService {
    override suspend fun echo(value: String): String = value

    override suspend fun greet(greeting: Greeting): Greeting = Greeting("Hello, ${greeting.text}!")
}
