/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server.starter.test

import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.krpc.KrpcConfig
import kotlinx.rpc.krpc.spring.server.EnableKrpc
import kotlinx.rpc.krpc.spring.server.InMemoryWebSocketSessionRegistry
import kotlinx.rpc.krpc.spring.server.KrpcProperties
import kotlinx.rpc.krpc.spring.server.KrpcWebSocketConfigurer
import kotlinx.rpc.krpc.spring.server.SpringKrpcServer
import kotlinx.rpc.krpc.spring.server.WebSocketSessionRegistry
import kotlinx.rpc.krpc.spring.server.starter.KrpcServerAutoConfiguration
import kotlinx.rpc.krpc.spring.server.starter.KrpcCborServerConfiguration
import kotlinx.rpc.krpc.spring.server.starter.KrpcJsonServerConfiguration
import kotlinx.rpc.krpc.spring.server.starter.KrpcProtobufServerConfiguration
import kotlinx.rpc.krpc.spring.server.starter.test.explicit.ExplicitService
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import org.springframework.boot.autoconfigure.AutoConfigurationPackage
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.FilteredClassLoader
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.boot.test.context.runner.WebApplicationContextRunner
import org.springframework.context.annotation.Bean
import org.springframework.web.socket.WebSocketSession
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue

// The configurations aren't @Configuration classes, so the component scan of StarterTestApplication skips them.
@AutoConfigurationPackage
open class AutoPackageConfiguration

@AutoConfigurationPackage
open class CustomBeansConfiguration {
    @Bean
    open fun customKrpcProperties(): KrpcProperties = KrpcProperties("/custom")

    @Bean
    open fun customSessionRegistry(): WebSocketSessionRegistry = CustomSessionRegistry

    object CustomSessionRegistry : WebSocketSessionRegistry {
        override fun put(session: WebSocketSession, server: SpringKrpcServer) {}
        override fun get(session: WebSocketSession): SpringKrpcServer? = null
        override fun remove(session: WebSocketSession): SpringKrpcServer? = null
    }
}

@AutoConfigurationPackage
@EnableKrpc(basePackages = ["kotlinx.rpc.krpc.spring.server.starter.test.explicit"])
open class ExplicitPackagesConfiguration

@OptIn(ExperimentalSerializationApi::class)
class KrpcServerAutoConfigurationTest {
    private val runner = WebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(KrpcServerAutoConfiguration::class.java))

    @Test
    fun `provides default beans for a servlet application`() {
        runner.withUserConfiguration(AutoPackageConfiguration::class.java).run { context ->
            assertEquals("/krpc", context.getBean(KrpcProperties::class.java).krpcPath)
            assertIs<InMemoryWebSocketSessionRegistry>(context.getBean(WebSocketSessionRegistry::class.java))
            assertIs<Json>(context.getBean(KrpcConfig.Server::class.java).serialFormat())
            assertEquals(1, context.getBeansOfType(KrpcWebSocketConfigurer::class.java).size)
            assertEquals(1, context.getBeansOfType(TestService::class.java).size)
        }
    }

    @Test
    fun `uses CBOR when JSON is not available`() {
        runner.withClassLoader(
            FilteredClassLoader("kotlinx.rpc.krpc.serialization.json"),
        ).withUserConfiguration(AutoPackageConfiguration::class.java).run { context ->
            assertIs<Cbor>(context.getBean(KrpcConfig.Server::class.java).serialFormat())
        }
    }

    @Test
    fun `uses Protocol Buffers when JSON and CBOR are not available`() {
        runner.withClassLoader(
            FilteredClassLoader("kotlinx.rpc.krpc.serialization.json", "kotlinx.rpc.krpc.serialization.cbor"),
        ).withUserConfiguration(AutoPackageConfiguration::class.java).run { context ->
            assertIs<ProtoBuf>(context.getBean(KrpcConfig.Server::class.java).serialFormat())
        }
    }

    @Test
    fun `the serialization property selects CBOR when JSON is available`() {
        runner.withUserConfiguration(AutoPackageConfiguration::class.java)
            .withPropertyValues("kotlinx.rpc.krpc.server.serialization=cbor")
            .run { context ->
                assertIs<Cbor>(context.getBean(KrpcConfig.Server::class.java).serialFormat())
            }
    }

    @Test
    fun `the serialization property selects Protocol Buffers when JSON is available`() {
        runner.withUserConfiguration(AutoPackageConfiguration::class.java)
            .withPropertyValues("kotlinx.rpc.krpc.server.serialization=protobuf")
            .run { context ->
                assertIs<ProtoBuf>(context.getBean(KrpcConfig.Server::class.java).serialFormat())
            }
    }

    @Test
    fun `creates no server configuration when no serialization module is available`() {
        WebApplicationContextRunner()
            .withUserConfiguration(
                KrpcJsonServerConfiguration::class.java,
                KrpcCborServerConfiguration::class.java,
                KrpcProtobufServerConfiguration::class.java,
            )
            .withClassLoader(
                FilteredClassLoader(
                    "kotlinx.rpc.krpc.serialization.json",
                    "kotlinx.rpc.krpc.serialization.cbor",
                    "kotlinx.rpc.krpc.serialization.protobuf",
                ),
            )
            .run { context ->
                assertTrue(context.getBeansOfType(KrpcConfig.Server::class.java).isEmpty())
            }
    }

    @Test
    fun `binds the endpoint path property`() {
        runner.withUserConfiguration(AutoPackageConfiguration::class.java)
            .withPropertyValues("kotlinx.rpc.krpc.server.path=/rpc")
            .run { context ->
                assertEquals("/rpc", context.getBean(KrpcProperties::class.java).krpcPath)
            }
    }

    @Test
    fun `user beans replace the defaults`() {
        runner.withUserConfiguration(CustomBeansConfiguration::class.java).run { context ->
            assertEquals("/custom", context.getBean(KrpcProperties::class.java).krpcPath)
            assertSame(
                CustomBeansConfiguration.CustomSessionRegistry,
                context.getBean(WebSocketSessionRegistry::class.java),
            )
        }
    }

    @Test
    fun `the enabled property set to false disables the starter`() {
        runner.withUserConfiguration(AutoPackageConfiguration::class.java)
            .withPropertyValues("kotlinx.rpc.krpc.server.enabled=false")
            .run { context ->
                assertTrue(context.getBeansOfType(KrpcProperties::class.java).isEmpty())
                assertTrue(context.getBeansOfType(KrpcWebSocketConfigurer::class.java).isEmpty())
                assertTrue(context.getBeansOfType(TestService::class.java).isEmpty())
            }
    }

    @Test
    fun `a non-web application gets no kRPC beans`() {
        ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(KrpcServerAutoConfiguration::class.java))
            .withUserConfiguration(AutoPackageConfiguration::class.java)
            .run { context ->
                assertTrue(context.getBeansOfType(KrpcProperties::class.java).isEmpty())
                assertTrue(context.getBeansOfType(KrpcWebSocketConfigurer::class.java).isEmpty())
            }
    }

    @Test
    fun `EnableKrpc packages replace the auto-configuration package scan`() {
        runner.withUserConfiguration(ExplicitPackagesConfiguration::class.java).run { context ->
            assertEquals(1, context.getBeansOfType(ExplicitService::class.java).size)
            assertTrue(context.getBeansOfType(TestService::class.java).isEmpty())
        }
    }
}

@OptIn(InternalRpcApi::class)
private fun KrpcConfig.Server.serialFormat(): Any = serialFormatInitializer.build()
