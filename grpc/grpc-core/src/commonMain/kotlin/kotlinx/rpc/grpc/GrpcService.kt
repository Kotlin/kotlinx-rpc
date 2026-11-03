/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.rpc.RpcService
import kotlinx.rpc.grpc.descriptor.GrpcServiceDescriptor

public interface GrpcService<T: GrpcService<T>>: RpcService<GrpcProtocol, T> {
    public override val descriptor: GrpcServiceDescriptor<T>
}
