/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server

import kotlinx.coroutines.SupervisorJob
import kotlinx.rpc.krpc.KrpcConfig
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

/**
 * Registers the kRPC WebSocket endpoint and services in a Spring application context.
 *
 * The application must define beans for [KrpcProperties], [KrpcConfig.Server], and [WebSocketSessionRegistry].
 * Annotate a configuration class with [EnableKrpc] to register service implementations.
 * The endpoint uses [KrpcProperties.krpcPath], and each WebSocket session has its own [SpringKrpcServer].
 *
 * All sessions use the same service beans.
 * Service implementations with the singleton scope must be thread-safe.
 *
 * When the application context closes, the configurer closes all open sessions.
 *
 * The WebSocket container limits the message size. Tomcat accepts 8 KB by default and closes the session
 * on a larger message. To change the limit, define a `ServletServerContainerFactoryBean` bean.
 *
 * @param krpcProperties Sets the WebSocket endpoint path.
 * @param krpcConfig Defines the server configuration.
 * @param webSocketSessionRegistry Stores a server for each WebSocket session.
 */
@Configuration
@EnableWebSocket
public open class KrpcWebSocketConfigurer(
    private val krpcProperties: KrpcProperties,
    private val krpcConfig: KrpcConfig.Server,
    private val webSocketSessionRegistry: WebSocketSessionRegistry,
    private val transportContextEnrichers: List<TransportContextEnricher>,
) : WebSocketConfigurer, BeanFactoryAware, DisposableBean {
    private val sessionsJob = SupervisorJob()

    private lateinit var beanFactory: ListableBeanFactory

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        val classLoader = (beanFactory as? ConfigurableBeanFactory)?.beanClassLoader

        val handler = KrpcWebSocketHandler(
            services = beanFactory.resolveKrpcServiceBeans(classLoader),
            config = krpcConfig,
            webSocketSessionRegistry = webSocketSessionRegistry,
            beanFactory = beanFactory,
            sessionsJob = sessionsJob,
            transportContextEnrichers = transportContextEnrichers,
        )
        registry.addHandler(handler, krpcProperties.krpcPath)
    }

    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory as? ListableBeanFactory
            ?: error("kRPC requires a ListableBeanFactory, but the bean factory is ${beanFactory::class.java.name}.")
    }

    override fun destroy() {
        sessionsJob.cancel()
    }
}
