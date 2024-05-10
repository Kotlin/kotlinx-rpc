/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal.service

import kotlinx.rpc.internal.InternalRPCApi
import kotlin.reflect.KClass

@InternalRPCApi
open class ServiceContainer {
    private val services = mutableSetOf<Any>()

    fun upload(service: Any) {
        services.add(service)
    }

    fun <Service : Any> load(serviceClass: KClass<Service>): Service {
        @Suppress("UNCHECKED_CAST")
        return services.singleOrNull { serviceClass.isInstance(it) } as? Service
            ?: error("Unable to find service $serviceClass")
    }

    inline fun <reified Service : Any> load(): Service {
        return load(Service::class)
    }
}

@InternalRPCApi
open class CompanionServiceContainer<DeclaringClass : Any>(
    private val declaringKClass: KClass<DeclaringClass>,
) : ServiceContainer() {
    fun loadService(): DeclaringClass {
        return load(declaringKClass)
    }
}
