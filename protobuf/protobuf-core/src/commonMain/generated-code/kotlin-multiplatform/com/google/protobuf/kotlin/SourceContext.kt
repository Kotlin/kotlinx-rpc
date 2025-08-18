@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.SourceContextInternal.CODEC::class)
public interface SourceContext { 
    public val fileName: String

    public companion object
}

