/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.rpc.descriptor.RpcServiceDescriptor

/**
 * Represents a method or field call of an RPC service.
 *
 * @property descriptor [RpcServiceDescriptor] of a service that made the call.
 * @property callableName The name of the callable. Can be the name of the method or field.
 * @property data The data for the call.
 * @property serviceId id of the service, that made the call.
 */
public data class RpcCall(
    val descriptor: RpcServiceDescriptor<*>,
    val callableName: String,
    val data: Any,
    val serviceId: Long,
)
