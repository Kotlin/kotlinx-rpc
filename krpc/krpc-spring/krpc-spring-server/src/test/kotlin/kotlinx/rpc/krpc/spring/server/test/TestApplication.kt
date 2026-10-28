/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server.test

import kotlinx.rpc.krpc.KrpcConfig
import kotlinx.rpc.krpc.rpcServerConfig
import kotlinx.rpc.krpc.serialization.cbor.cbor
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.krpc.spring.server.*
import kotlinx.serialization.ExperimentalSerializationApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import

@SpringBootConfiguration(proxyBeanMethods = false)
@EnableAutoConfiguration
@EnableKrpc(basePackages = ["kotlinx.rpc.krpc.spring.server.test"])
@Import(KrpcWebSocketConfigurer::class)
@OptIn(ExperimentalSerializationApi::class)
open class TestApplication {
    @Bean
    fun krpcProperties(): KrpcProperties = KrpcProperties("/krpc")

    @Bean
    fun webSocketSessionRegistry(): WebSocketSessionRegistry = InMemoryWebSocketSessionRegistry()

    @Bean
    fun krpcConfig(@Value("\${test.serialization:json}") serializationFormat: String): KrpcConfig.Server =
        rpcServerConfig {
            serialization {
                when (serializationFormat) {
                    "json" -> json()
                    "cbor" -> cbor()
                    else -> error("Unsupported test serialization format: $serializationFormat")
                }
            }
        }
}
