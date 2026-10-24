/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server

import org.springframework.context.annotation.Import

/**
 * Enables kRPC service registration in a Spring application context.
 *
 * A kRPC service is a class that has `@Component` (or `@Service`) and implements one or more interfaces with
 * [kotlinx.rpc.annotations.Rpc]. Each service must have exactly one bean.
 * If the class isn't a bean yet, the scan registers it.
 *
 * Also register [KrpcWebSocketConfigurer] to serve the services.
 *
 * @property basePackages Packages to scan for kRPC services. If empty, Spring scans the package of the annotated class.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Import(KrpcServiceRegistrar::class)
public annotation class EnableKrpc(
    public val basePackages: Array<String> = []
)
