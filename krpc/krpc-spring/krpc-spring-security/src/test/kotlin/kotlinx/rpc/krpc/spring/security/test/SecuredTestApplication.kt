/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.security.test

import kotlinx.rpc.krpc.KrpcConfig
import kotlinx.rpc.krpc.rpcServerConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.krpc.spring.security.SecurityContextEnricher
import kotlinx.rpc.krpc.spring.server.EnableKrpc
import kotlinx.rpc.krpc.spring.server.InMemoryWebSocketSessionRegistry
import kotlinx.rpc.krpc.spring.server.KrpcProperties
import kotlinx.rpc.krpc.spring.server.KrpcWebSocketConfigurer
import kotlinx.rpc.krpc.spring.server.WebSocketSessionRegistry
import kotlinx.serialization.ExperimentalSerializationApi
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@SpringBootConfiguration(proxyBeanMethods = false)
@EnableAutoConfiguration
@EnableKrpc(basePackages = ["kotlinx.rpc.krpc.spring.security.test"])
@EnableMethodSecurity
@Import(KrpcWebSocketConfigurer::class)
@OptIn(ExperimentalSerializationApi::class)
open class SecuredTestApplication {
    @Bean
    fun krpcProperties(): KrpcProperties = KrpcProperties("/krpc")

    @Bean
    fun webSocketSessionRegistry(): WebSocketSessionRegistry = InMemoryWebSocketSessionRegistry()

    @Bean
    fun krpcConfig(): KrpcConfig.Server = rpcServerConfig {
        serialization { json() }
    }

    @Bean
    fun securityContextEnricher(): SecurityContextEnricher = SecurityContextEnricher()

    @Bean
    fun users(): InMemoryUserDetailsManager = InMemoryUserDetailsManager(
        User.withUsername("user").password("{noop}user-password").roles("USER").build(),
        User.withUsername("admin").password("{noop}admin-password").roles("USER", "ADMIN").build(),
    )

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain = http
        .authorizeHttpRequests { requests ->
            requests.requestMatchers("/krpc").hasAnyRole("USER", "ADMIN")
            requests.anyRequest().denyAll()
        }
        .httpBasic(Customizer.withDefaults())
        .csrf { it.disable() }
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .build()
}