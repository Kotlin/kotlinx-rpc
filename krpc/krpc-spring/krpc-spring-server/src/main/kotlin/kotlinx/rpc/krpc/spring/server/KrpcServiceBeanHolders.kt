/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server

import kotlinx.rpc.annotations.Rpc
import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.util.ClassUtils
import kotlin.reflect.KClass

/** The class names of the services that one [EnableKrpc] annotation found. */
internal class KrpcServiceBeanHolders(val serviceClassNames: Collection<String>)

/** A service bean and one of its [Rpc] interfaces. */
internal class KrpcServiceBean(
    val rpcInterface: KClass<*>,
    val beanName: String,
)

/**
 * Resolves the beans of the services in all [KrpcServiceBeanHolders] beans.
 *
 * A bean that implements more than one [Rpc] interface gives one [KrpcServiceBean] for each interface.
 */
internal fun ListableBeanFactory.resolveKrpcServiceBeans(classLoader: ClassLoader?): List<KrpcServiceBean> {
    val serviceClassNames = getBeansOfType(KrpcServiceBeanHolders::class.java).values
        .flatMapTo(linkedSetOf()) { it.serviceClassNames }

    return serviceClassNames.flatMap { className ->
        val serviceClass = ClassUtils.resolveClassName(className, classLoader)
        val beanNames = getBeanNamesForType(serviceClass)
        val beanName = beanNames.singleOrNull() ?: error(
            "kRPC service class '$className' must have exactly one bean, but it has ${beanNames.size}: " +
                beanNames.joinToString()
        )

        serviceClass.rpcInterfaces().map { KrpcServiceBean(it, beanName) }
    }
}

private fun Class<*>.rpcInterfaces(): List<KClass<*>> {
    val interfaces = ClassUtils.getAllInterfacesForClassAsSet(this)
        .filter { AnnotatedElementUtils.hasAnnotation(it, Rpc::class.java) }

    check(interfaces.isNotEmpty()) { "Class '$name' doesn't implement an interface annotated with @Rpc." }

    return interfaces.map { it.kotlin }
}
