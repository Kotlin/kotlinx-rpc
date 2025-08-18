@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.AnyInternal.CODEC::class)
public interface Any { 
    public val typeUrl: String
    public val value: ByteArray

    public companion object
}

