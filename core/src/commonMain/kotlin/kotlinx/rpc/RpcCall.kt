/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.rpc.descriptor.RpcServiceDescriptor

public class RpcCall<Protocol : RpcProtocol, Service : RpcService<Protocol, Service>>(
    public val descriptor: RpcServiceDescriptor<Protocol, Service>,
    public val callableName: String,
    public val arguments: Array<Any?>,
    public val serviceId: Long,
)

