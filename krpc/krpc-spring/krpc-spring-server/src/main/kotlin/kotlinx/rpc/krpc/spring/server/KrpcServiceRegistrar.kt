/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server

import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanNameGenerator
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.annotation.AnnotationAttributes
import org.springframework.core.type.AnnotationMetadata
import org.springframework.util.ClassUtils

internal class KrpcServiceRegistrar : ImportBeanDefinitionRegistrar {

    override fun registerBeanDefinitions(
        importingClassMetadata: AnnotationMetadata,
        registry: BeanDefinitionRegistry,
        nameGenerator: BeanNameGenerator,
    ) {
        val attributes = AnnotationAttributes.fromMap(
            importingClassMetadata.getAnnotationAttributes(EnableKrpc::class.java.name)
        ) ?: return

        val basePackages = attributes.getStringArray(EnableKrpc::basePackages.name)
            .ifEmpty { arrayOf(ClassUtils.getPackageName(importingClassMetadata.className)) }

        KrpcServiceRegistration.register(registry, basePackages.toList(), nameGenerator)
    }
}
