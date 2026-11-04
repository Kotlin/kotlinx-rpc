/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server

import kotlinx.rpc.internal.utils.InternalRpcApi
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanNameGenerator

/** Registers kRPC service beans from package scans. */
@InternalRpcApi
public object KrpcServiceRegistration {
    /** Returns whether the registry contains kRPC service registrations. */
    public fun hasRegistrations(registry: BeanDefinitionRegistry): Boolean =
        registry.beanDefinitionNames.any { beanName ->
            registry.getBeanDefinition(beanName).beanClassName == KrpcServiceBeanHolders::class.java.name
        }

    /** Scans [basePackages] and records the service classes in the registry. */
    public fun register(
        registry: BeanDefinitionRegistry,
        basePackages: Collection<String>,
        nameGenerator: BeanNameGenerator,
    ): Unit {
        val scanner = KrpcBeanScanner(registry)
        scanner.scan(*basePackages.toTypedArray())

        val holdersDefinition = BeanDefinitionBuilder
            .genericBeanDefinition(KrpcServiceBeanHolders::class.java)
            .addConstructorArgValue(scanner.serviceClassNames.toList())
            .beanDefinition

        registry.registerBeanDefinition(
            nameGenerator.generateBeanName(holdersDefinition, registry),
            holdersDefinition,
        )
    }
}