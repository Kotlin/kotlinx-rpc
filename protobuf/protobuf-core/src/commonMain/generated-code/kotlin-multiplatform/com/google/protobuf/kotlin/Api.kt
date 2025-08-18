@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.ApiInternal.CODEC::class)
public interface Api { 
    public val name: String
    public val methods: List<com.google.protobuf.kotlin.Method>
    public val options: List<com.google.protobuf.kotlin.Option>
    public val version: String
    public val sourceContext: com.google.protobuf.kotlin.SourceContext
    public val mixins: List<com.google.protobuf.kotlin.Mixin>
    public val syntax: com.google.protobuf.kotlin.Syntax

    public companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.MethodInternal.CODEC::class)
public interface Method { 
    public val name: String
    public val requestTypeUrl: String
    public val requestStreaming: Boolean
    public val responseTypeUrl: String
    public val responseStreaming: Boolean
    public val options: List<com.google.protobuf.kotlin.Option>
    public val syntax: com.google.protobuf.kotlin.Syntax

    public companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.MixinInternal.CODEC::class)
public interface Mixin { 
    public val name: String
    public val root: String

    public companion object
}

