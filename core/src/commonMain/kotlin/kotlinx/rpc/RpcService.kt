/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.rpc.descriptor.RpcServiceDescriptor
import kotlinx.rpc.descriptor.serviceDescriptorOf
import kotlin.reflect.KClass

public interface RpcService<Protocol : RpcProtocol, Self : RpcService<Protocol, Self>> {
    public val descriptor: RpcServiceDescriptor<Protocol, Self>
}

public val <Protocol: RpcProtocol, Service : RpcService<Protocol, Service>> KClass<Service>.descriptor: RpcServiceDescriptor<Protocol, Service>
    @Suppress("UNCHECKED_CAST")
    get() = serviceDescriptorOf(this) as RpcServiceDescriptor<Protocol, Service>
