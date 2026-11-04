/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.security.starter.test

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.krpc.ktor.client.KtorRpcClient
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.serialization.json.json
import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import java.util.Base64
import kotlin.time.Duration.Companion.seconds

// The application has no kRPC or enricher beans, so the starters provide them.
@SpringBootApplication(proxyBeanMethods = false)
@EnableMethodSecurity
open class SecurityStarterTestApplication {
    @Bean
    open fun users(): InMemoryUserDetailsManager = InMemoryUserDetailsManager(
        User.withUsername("user").password("{noop}user-password").roles("USER").build(),
        User.withUsername("admin").password("{noop}admin-password").roles("USER", "ADMIN").build(),
    )

    @Bean
    open fun securityFilterChain(http: HttpSecurity): SecurityFilterChain = http
        .authorizeHttpRequests { requests ->
            requests.requestMatchers("/krpc").hasAnyRole("USER", "ADMIN")
            requests.anyRequest().denyAll()
        }
        .httpBasic(Customizer.withDefaults())
        .csrf { it.disable() }
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .build()
}

internal class SecuredStarterServer(vararg properties: String) : AutoCloseable {
    val context: ConfigurableApplicationContext = run {
        disableTomcatUrlStreamHandlerFactory()
        SpringApplicationBuilder(SecurityStarterTestApplication::class.java).run(
            "--server.port=0",
            "--spring.main.banner-mode=off",
            "--logging.level.root=ERROR",
            *properties,
        )
    }

    private val port = context.environment.getRequiredProperty("local.server.port").toInt()
    private val httpClient = HttpClient(CIO) { install(WebSockets) }

    fun rpcClient(username: String? = null, password: String? = null): KtorRpcClient =
        httpClient.securedKrpcClient(port, username, password)

    override fun close() {
        httpClient.close()
        context.close()
    }
}

// runTest uses virtual time, so the timeout runs on a real dispatcher.
internal suspend fun <T> withRpcTimeout(block: suspend () -> T): T = withContext(Dispatchers.Default) {
    withTimeout(10.seconds) { block() }
}

internal fun HttpClient.securedKrpcClient(
    port: Int,
    username: String? = null,
    password: String? = null,
): KtorRpcClient = rpc("ws://127.0.0.1:$port/krpc") {
    if (username != null && password != null) {
        val credentials = Base64.getEncoder().encodeToString("$username:$password".toByteArray())
        header(HttpHeaders.Authorization, "Basic $credentials")
    }
    rpcConfig {
        serialization { json() }
    }
}

// The AOT test loads Tomcat in a second class loader. The JVM accepts only one URL stream handler factory,
// so each Tomcat copy must skip the registration.
internal fun disableTomcatUrlStreamHandlerFactory() {
    TomcatURLStreamHandlerFactory.disable()
}
