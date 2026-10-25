/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.security.starter

import kotlinx.rpc.krpc.spring.security.SecurityContextEnricher
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolderStrategy

/**
 * Registers a [SecurityContextEnricher] for the kRPC Spring server starter.
 * kRPC service calls then run with the `Authentication` of their WebSocket session.
 *
 * Declare these dependencies explicitly, because this starter doesn't add them:
 * - `kotlinx-rpc-krpc-spring-server-starter`
 * - `spring-boot-starter-security`
 *
 * The configuration applies only when all of these conditions are true:
 * - The application is a servlet web application.
 * - Both dependencies above are on the classpath.
 * - `kotlinx.rpc.krpc.server.enabled` isn't `false`.
 * - `kotlinx.rpc.krpc.server.security.enabled` isn't `false`.
 *
 * If a condition is false, the starter registers no bean and doesn't fail.
 * The application still configures its own `SecurityFilterChain` and authentication.
 */
@AutoConfiguration(afterName = [KRPC_SERVER_AUTO_CONFIGURATION])
@ConditionalOnWebApplication(type = SERVLET)
@ConditionalOnClass(name = [KRPC_SERVER_AUTO_CONFIGURATION, BOOT_SECURITY_AUTO_CONFIGURATION])
@ConditionalOnBooleanProperty(prefix = "kotlinx.rpc.krpc.server", name = ["enabled"], matchIfMissing = true)
@ConditionalOnBooleanProperty(prefix = "kotlinx.rpc.krpc.server.security", name = ["enabled"], matchIfMissing = true)
@EnableConfigurationProperties(KrpcSecurityStarterProperties::class)
public open class KrpcSecurityAutoConfiguration {
    /**
     * Returns the default [SecurityContextEnricher].
     *
     * The enricher uses the application's [SecurityContextHolderStrategy] bean.
     * If the application has no such bean, the enricher uses [SecurityContextHolder.getContextHolderStrategy].
     * Declare your own [SecurityContextEnricher] bean to replace this one.
     */
    @Bean("securityContextEnricher")
    @ConditionalOnMissingBean(SecurityContextEnricher::class)
    public open fun securityContextEnricher(
        strategy: ObjectProvider<SecurityContextHolderStrategy>,
    ): SecurityContextEnricher =
        SecurityContextEnricher(strategy.getIfAvailable { SecurityContextHolder.getContextHolderStrategy() })
}

/** Configuration properties for the kRPC Spring Security starter. */
@ConfigurationProperties("kotlinx.rpc.krpc.server.security")
public open class KrpcSecurityStarterProperties {
    /** Enables the kRPC Spring Security auto-configuration. Default is `true`. */
    public var enabled: Boolean = true
}

// String names, because both classes come from compileOnly dependencies that can be missing at runtime.
private const val KRPC_SERVER_AUTO_CONFIGURATION =
    "kotlinx.rpc.krpc.spring.server.starter.KrpcServerAutoConfiguration"
private const val BOOT_SECURITY_AUTO_CONFIGURATION =
    "org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration"
