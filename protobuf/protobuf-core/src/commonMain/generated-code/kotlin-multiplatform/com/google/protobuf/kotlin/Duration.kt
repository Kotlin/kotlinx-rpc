@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.DurationInternal.CODEC::class)
public interface Duration { 
    public val seconds: Long
    public val nanos: Int

    public companion object
}

