/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server

import kotlinx.rpc.annotations.Rpc
import org.springframework.beans.factory.config.BeanDefinitionHolder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.core.type.filter.TypeFilter
import org.springframework.stereotype.Component

/**
 * Finds the classes that have [Component] and implement an [Rpc] interface, and registers them as beans.
 *
 * The scanner requires [Component] because the generated client stub classes also implement the [Rpc] interface.
 */
internal class KrpcBeanScanner(
    registry: BeanDefinitionRegistry,
) : ClassPathBeanDefinitionScanner(registry, false) {
    private val componentFilter = AnnotationTypeFilter(Component::class.java)
    private val rpcInterfaceFilter = AnnotationTypeFilter(Rpc::class.java, true, true)

    init {
        addIncludeFilter(TypeFilter { reader, factory ->
            componentFilter.match(reader, factory) && rpcInterfaceFilter.match(reader, factory)
        })
    }

    /** The class names of all found services, also of the services that are already registered as beans. */
    val serviceClassNames: MutableSet<String> = linkedSetOf()

    override fun doScan(vararg basePackages: String): Set<BeanDefinitionHolder> {
        basePackages.forEach { basePackage ->
            findCandidateComponents(basePackage).mapNotNullTo(serviceClassNames) { it.beanClassName }
        }
        return super.doScan(*basePackages)
    }
}
