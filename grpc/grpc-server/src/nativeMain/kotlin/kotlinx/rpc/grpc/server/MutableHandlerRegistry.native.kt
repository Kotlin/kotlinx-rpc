/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package kotlinx.rpc.grpc.server

import kotlinx.rpc.grpc.descriptor.GrpcMethodDescriptor
import kotlinx.rpc.grpc.server.internal.ServerMethodDefinition
import kotlinx.rpc.internal.utils.map.RpcInternalConcurrentHashMap

/**
 * Registry of services and their methods used by servers to dispatching incoming calls.
 */
public actual abstract class GrpcHandlerRegistry {
    /**
     * Returns the [GrpcServerServiceDefinition]s provided by the registry, or an empty list if not
     * supported by the implementation.
     */
    public abstract fun getServices(): List<GrpcServerServiceDefinition>

    /**
     * Lookup a [ServerMethodDefinition] by its fully-qualified name.
     *
     * @param methodName to lookup [ServerMethodDefinition] for.
     * @param authority the authority for the desired method (to do virtual hosting). If `null`
     * the first matching method will be returned.
     * @return the resolved method or `null` if no method for that name exists.
     */
    public abstract fun lookupMethod(
        methodName: String, authority: String?,
    ): ServerMethodDefinition<*, *>?

    /**
     * Lookup a [ServerMethodDefinition] by its fully-qualified name.
     *
     * @param methodName to lookup [ServerMethodDefinition] for.
     * @return the resolved method or `null` if no method for that name exists.
     */
    public fun lookupMethod(methodName: String): ServerMethodDefinition<*, *>? {
        return lookupMethod(methodName, null)
    }

}

internal actual class GrpcMutableHandlerRegistry : GrpcHandlerRegistry() {
    private val services = RpcInternalConcurrentHashMap<String, GrpcServerServiceDefinition>()

    actual fun addService(service: GrpcServerServiceDefinition): GrpcServerServiceDefinition? {
        return services.put(service.getServiceDescriptor().getName(), service)
    }

    actual fun removeService(service: GrpcServerServiceDefinition): Boolean {
        return services.remove(service.getServiceDescriptor().getName()) != null
    }

    override fun getServices(): List<GrpcServerServiceDefinition> {
        return services.values.toList()
    }

    override fun lookupMethod(
        methodName: String,
        authority: String?,
    ): ServerMethodDefinition<*, *>? {
        val serviceName = GrpcMethodDescriptor.extractFullServiceName(methodName) ?: return null
        val service = services[serviceName] ?: return null
        return service.getMethod(methodName)
    }
}
