@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.DoubleValueInternal.CODEC::class)
public interface DoubleValue { 
    public val value: Double

    public companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.FloatValueInternal.CODEC::class)
public interface FloatValue { 
    public val value: Float

    public companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.Int64ValueInternal.CODEC::class)
public interface Int64Value { 
    public val value: Long

    public companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.UInt64ValueInternal.CODEC::class)
public interface UInt64Value { 
    public val value: ULong

    public companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.Int32ValueInternal.CODEC::class)
public interface Int32Value { 
    public val value: Int

    public companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.UInt32ValueInternal.CODEC::class)
public interface UInt32Value { 
    public val value: UInt

    public companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.BoolValueInternal.CODEC::class)
public interface BoolValue { 
    public val value: Boolean

    public companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.StringValueInternal.CODEC::class)
public interface StringValue { 
    public val value: String

    public companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.BytesValueInternal.CODEC::class)
public interface BytesValue { 
    public val value: ByteArray

    public companion object
}

