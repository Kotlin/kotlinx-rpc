/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server.starter

import kotlinx.rpc.krpc.KrpcConfig
import kotlinx.rpc.krpc.rpcServerConfig
import kotlinx.rpc.krpc.serialization.cbor.cbor
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.krpc.serialization.protobuf.protobuf
import kotlinx.rpc.krpc.spring.server.InMemoryWebSocketSessionRegistry
import kotlinx.rpc.krpc.spring.server.KrpcProperties
import kotlinx.rpc.krpc.spring.server.KrpcServiceRegistration
import kotlinx.rpc.krpc.spring.server.KrpcWebSocketConfigurer
import kotlinx.rpc.krpc.spring.server.WebSocketSessionRegistry
import kotlinx.serialization.ExperimentalSerializationApi
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanNameGenerator
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigurationPackages
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.type.AnnotationMetadata

/** Configures the kRPC Spring server in a servlet application. */
@AutoConfiguration(
    afterName = [
        "org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration",
        "org.springframework.boot.websocket.autoconfigure.servlet.WebSocketMessagingAutoConfiguration",
    ],
)
@ConditionalOnWebApplication(type = SERVLET)
@ConditionalOnBooleanProperty(prefix = "kotlinx.rpc.krpc.server", name = ["enabled"], matchIfMissing = true)
@EnableConfigurationProperties(KrpcServerStarterProperties::class)
@Import(
    KrpcServiceAutoRegistrar::class,
    KrpcJsonServerConfiguration::class,
    KrpcCborServerConfiguration::class,
    KrpcProtobufServerConfiguration::class,
    KrpcWebSocketConfigurerConfiguration::class,
)
public open class KrpcServerAutoConfiguration {
    /** Returns the endpoint settings from [KrpcServerStarterProperties]. */
    @Bean("krpcProperties")
    @ConditionalOnMissingBean(KrpcProperties::class)
    public open fun krpcProperties(properties: KrpcServerStarterProperties): KrpcProperties =
        KrpcProperties(properties.path)

    /** Returns the default in-memory session registry. */
    @Bean("webSocketSessionRegistry")
    @ConditionalOnMissingBean(WebSocketSessionRegistry::class)
    public open fun webSocketSessionRegistry(): WebSocketSessionRegistry = InMemoryWebSocketSessionRegistry()
}

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(name = ["kotlinx.rpc.krpc.serialization.json.KrpcJsonSerialFormatKt"])
@ConditionalOnProperty(
    prefix = "kotlinx.rpc.krpc.server",
    name = ["serialization"],
    havingValue = "json",
    matchIfMissing = true,
)
internal open class KrpcJsonServerConfiguration {
    @Bean("krpcConfig")
    @ConditionalOnMissingBean(KrpcConfig.Server::class)
    open fun krpcConfig(): KrpcConfig.Server = rpcServerConfig {
        serialization { json() }
    }
}

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(name = ["kotlinx.rpc.krpc.serialization.cbor.KrpcCborSerialFormatKt"])
@ConditionalOnProperty(
    prefix = "kotlinx.rpc.krpc.server",
    name = ["serialization"],
    havingValue = "cbor",
    matchIfMissing = true,
)
@OptIn(ExperimentalSerializationApi::class)
internal open class KrpcCborServerConfiguration {
    @Bean("krpcConfig")
    @ConditionalOnMissingBean(KrpcConfig.Server::class)
    open fun krpcConfig(): KrpcConfig.Server = rpcServerConfig {
        serialization { cbor() }
    }
}

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(name = ["kotlinx.rpc.krpc.serialization.protobuf.KrpcProtobufSerialFormatKt"])
@ConditionalOnProperty(
    prefix = "kotlinx.rpc.krpc.server",
    name = ["serialization"],
    havingValue = "protobuf",
    matchIfMissing = true,
)
@OptIn(ExperimentalSerializationApi::class)
internal open class KrpcProtobufServerConfiguration {
    @Bean("krpcConfig")
    @ConditionalOnMissingBean(KrpcConfig.Server::class)
    open fun krpcConfig(): KrpcConfig.Server = rpcServerConfig {
        serialization { protobuf() }
    }
}

@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(KrpcWebSocketConfigurer::class)
@Import(KrpcWebSocketConfigurer::class)
internal open class KrpcWebSocketConfigurerConfiguration

/** Configuration properties for the kRPC Spring server starter. */
@ConfigurationProperties("kotlinx.rpc.krpc.server")
public open class KrpcServerStarterProperties {
    /** Enables the kRPC server auto-configuration. */
    public var enabled: Boolean = true

    /** Sets the WebSocket endpoint path. */
    public var path: String = "/krpc"

    /** Selects the serialization format. If unset, the starter selects the first available format. */
    public var serialization: KrpcServerSerialization? = null
}

/** A serialization format for the kRPC Spring server starter. */
public enum class KrpcServerSerialization {
    /** JSON serialization. */
    JSON,

    /** CBOR serialization. */
    CBOR,

    /** Protocol Buffers serialization. */
    PROTOBUF,
}

internal open class KrpcServiceAutoRegistrar : ImportBeanDefinitionRegistrar {
    override fun registerBeanDefinitions(
        importingClassMetadata: AnnotationMetadata,
        registry: BeanDefinitionRegistry,
        importBeanNameGenerator: BeanNameGenerator,
    ) {
        if (KrpcServiceRegistration.hasRegistrations(registry)) return

        val beanFactory = registry as? ConfigurableListableBeanFactory ?: registry as? BeanFactory ?: return
        if (AutoConfigurationPackages.has(beanFactory)) {
            KrpcServiceRegistration.register(
                registry,
                AutoConfigurationPackages.get(beanFactory),
                importBeanNameGenerator,
            )
        }
    }
}