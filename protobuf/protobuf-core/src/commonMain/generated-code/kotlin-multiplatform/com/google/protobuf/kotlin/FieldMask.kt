@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.FieldMaskInternal.CODEC::class)
public interface FieldMask { 
    public val paths: List<kotlin.String>

    public companion object
}

