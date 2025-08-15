@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.TimestampInternal.CODEC::class)
public interface Timestamp { 
    public val seconds: Long
    public val nanos: Int

    public companion object
}

