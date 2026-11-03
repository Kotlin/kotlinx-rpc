/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc

import kotlinx.rpc.RpcService
import kotlinx.rpc.krpc.descriptor.KrpcServiceDescriptor

public interface KrpcService<T: KrpcService<T>>: RpcService<KrpcProtocol, T> {
    public override val descriptor: KrpcServiceDescriptor<T>
}
