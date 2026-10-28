/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.security.starter.test

import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.rpc.krpc.spring.server.KrpcContextEnricher
import kotlinx.rpc.krpc.spring.server.KrpcWebSocketConfigurer
import kotlinx.rpc.krpc.spring.security.SecurityContextEnricher
import kotlinx.rpc.krpc.spring.security.starter.KrpcSecurityAutoConfiguration
import kotlinx.rpc.krpc.spring.server.starter.KrpcServerAutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigurationPackage
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.FilteredClassLoader
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.boot.test.context.runner.WebApplicationContextRunner
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolderStrategy
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.web.socket.WebSocketSession
import java.lang.reflect.Proxy
import java.security.Principal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

// The configurations aren't @Configuration classes, so the component scan of SecurityStarterTestApplication skips them.
@AutoConfigurationPackage
open class SecurityAutoPackageConfiguration

@AutoConfigurationPackage
open class CustomEnricherConfiguration {
    @Bean
    open fun customEnricher(): SecurityContextEnricher = CustomEnricher

    object CustomEnricher : SecurityContextEnricher()
}

@AutoConfigurationPackage
open class CustomStrategyConfiguration {
    @Bean
    open fun customStrategy(): SecurityContextHolderStrategy = CustomStrategy

    object CustomStrategy : SecurityContextHolderStrategy {
        private val contextHolder = ThreadLocal<SecurityContext>()

        override fun clearContext() {
            contextHolder.remove()
        }

        override fun getContext(): SecurityContext =
            contextHolder.get() ?: createEmptyContext().also { contextHolder.set(it) }

        override fun setContext(context: SecurityContext) {
            contextHolder.set(context)
        }

        override fun createEmptyContext(): SecurityContext = SecurityContextImpl()
    }
}

class KrpcSecurityAutoConfigurationTest {
    private val runner = WebApplicationContextRunner()
        .withConfiguration(
            AutoConfigurations.of(KrpcServerAutoConfiguration::class.java, KrpcSecurityAutoConfiguration::class.java),
        )

    @Test
    fun `registers one enricher and the server configurer uses it`() {
        runner.withUserConfiguration(SecurityAutoPackageConfiguration::class.java).run { context ->
            assertNull(context.startupFailure)
            val enrichers = context.getBeansOfType(SecurityContextEnricher::class.java)
            assertEquals(setOf("securityContextEnricher"), enrichers.keys)

            val configurer = context.getBean(KrpcWebSocketConfigurer::class.java)
            assertEquals(listOf<KrpcContextEnricher>(enrichers.values.single()), configurer.contextEnrichers())
        }
    }

    @Test
    fun `the default enricher uses the global strategy`() = runTest {
        val enricher = runner.withUserConfiguration(SecurityAutoPackageConfiguration::class.java)
            .enricherOrNull()
        val authentication = TestingAuthenticationToken("user", "password", "ROLE_USER")
        val strategy = SecurityContextHolder.getContextHolderStrategy()

        withContext(enricher!!.contextFor(sessionWith(authentication))) {
            assertSame(authentication, strategy.context.authentication)
        }
    }

    @Test
    fun `a user enricher replaces the default`() {
        runner.withUserConfiguration(CustomEnricherConfiguration::class.java).run { context ->
            assertSame(CustomEnricherConfiguration.CustomEnricher, context.getBean(SecurityContextEnricher::class.java))
            assertEquals(setOf("customEnricher"), context.getBeansOfType(SecurityContextEnricher::class.java).keys)
            assertEquals(
                listOf<KrpcContextEnricher>(CustomEnricherConfiguration.CustomEnricher),
                context.getBean(KrpcWebSocketConfigurer::class.java).contextEnrichers(),
            )
        }
    }

    @Test
    fun `the enricher uses the application strategy bean`() = runTest {
        val enricher = runner.withUserConfiguration(CustomStrategyConfiguration::class.java).enricherOrNull()
        val authentication = TestingAuthenticationToken("user", "password", "ROLE_USER")
        val globalStrategy = SecurityContextHolder.getContextHolderStrategy()
        assertNotSame<SecurityContextHolderStrategy>(CustomStrategyConfiguration.CustomStrategy, globalStrategy)

        withContext(enricher!!.contextFor(sessionWith(authentication))) {
            assertSame(authentication, CustomStrategyConfiguration.CustomStrategy.context.authentication)
            assertNull(globalStrategy.context.authentication)
        }
    }

    @Test
    fun `the security enabled property set to false disables the enricher`() {
        runner.withUserConfiguration(SecurityAutoPackageConfiguration::class.java)
            .withPropertyValues("kotlinx.rpc.krpc.server.security.enabled=false")
            .run { context ->
                assertNull(context.startupFailure)
                assertTrue(context.getBeansOfType(SecurityContextEnricher::class.java).isEmpty())
                assertTrue(context.getBean(KrpcWebSocketConfigurer::class.java).contextEnrichers().isEmpty())
            }
    }

    @Test
    fun `the server enabled property set to false disables the enricher`() {
        runner.withUserConfiguration(SecurityAutoPackageConfiguration::class.java)
            .withPropertyValues("kotlinx.rpc.krpc.server.enabled=false")
            .run { context ->
                assertNull(context.startupFailure)
                assertTrue(context.getBeansOfType(SecurityContextEnricher::class.java).isEmpty())
                assertTrue(context.getBeansOfType(KrpcWebSocketConfigurer::class.java).isEmpty())
            }
    }

    @Test
    fun `a non-web application gets no enricher`() {
        ApplicationContextRunner()
            .withConfiguration(
                AutoConfigurations.of(
                    KrpcServerAutoConfiguration::class.java,
                    KrpcSecurityAutoConfiguration::class.java,
                ),
            )
            .withUserConfiguration(SecurityAutoPackageConfiguration::class.java)
            .run { context ->
                assertNull(context.startupFailure)
                assertTrue(context.getBeansOfType(SecurityContextEnricher::class.java).isEmpty())
            }
    }

    @Test
    fun `a missing server starter disables the enricher`() {
        WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(KrpcSecurityAutoConfiguration::class.java))
            .withClassLoader(FilteredClassLoader(KRPC_SERVER_STARTER_PACKAGE))
            .withUserConfiguration(SecurityAutoPackageConfiguration::class.java)
            .run { context ->
                assertNull(context.startupFailure)
                assertTrue(context.getBeansOfType(SecurityContextEnricher::class.java).isEmpty())
                assertTrue(context.getBeansOfType(KrpcSecurityAutoConfiguration::class.java).isEmpty())
            }
    }

    @Test
    fun `a missing Boot security auto-configuration disables the enricher`() {
        runner.withClassLoader(FilteredClassLoader(BOOT_SECURITY_AUTOCONFIGURE_PACKAGE))
            .withUserConfiguration(SecurityAutoPackageConfiguration::class.java)
            .run { context ->
                assertNull(context.startupFailure)
                assertTrue(context.getBeansOfType(SecurityContextEnricher::class.java).isEmpty())
                assertTrue(context.getBeansOfType(KrpcSecurityAutoConfiguration::class.java).isEmpty())
                // The server starter still works without the security integration.
                assertTrue(context.getBean(KrpcWebSocketConfigurer::class.java).contextEnrichers().isEmpty())
            }
    }

    private fun WebApplicationContextRunner.enricherOrNull(): SecurityContextEnricher? {
        var enricher: SecurityContextEnricher? = null
        run { context ->
            assertNull(context.startupFailure)
            enricher = context.getBean(SecurityContextEnricher::class.java)
        }
        return enricher
    }

    private companion object {
        const val KRPC_SERVER_STARTER_PACKAGE = "kotlinx.rpc.krpc.spring.server.starter"
        const val BOOT_SECURITY_AUTOCONFIGURE_PACKAGE = "org.springframework.boot.security.autoconfigure"
    }
}

private fun KrpcWebSocketConfigurer.contextEnrichers(): List<*> {
    val field = KrpcWebSocketConfigurer::class.java.getDeclaredField("krpcContextEnrichers")
    field.isAccessible = true
    return assertIs<List<*>>(field.get(this))
}

private fun sessionWith(principal: Principal): WebSocketSession =
    Proxy.newProxyInstance(
        WebSocketSession::class.java.classLoader,
        arrayOf(WebSocketSession::class.java),
    ) { _, method, _ ->
        when (method.name) {
            "getPrincipal" -> principal
            else -> throw UnsupportedOperationException(method.name)
        }
    } as WebSocketSession
