@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*

/**
* Wrapper message for `double`.
* 
* The JSON representation for `DoubleValue` is JSON number.
* 
* Not recommended for use in new APIs, but still useful for legacy APIs and
* has no plan to be removed.
*/
@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.DoubleValueInternal.CODEC::class)
public interface DoubleValue { 
    /**
    * The double value.
    */
    public val value: Double

    /**
    * Copies the original message, including unknown fields.
    */
    public fun copy(body: com.google.protobuf.kotlin.DoubleValueInternal.() -> Unit = {}): com.google.protobuf.kotlin.DoubleValue

    public companion object
}

/**
* Wrapper message for `float`.
* 
* The JSON representation for `FloatValue` is JSON number.
* 
* Not recommended for use in new APIs, but still useful for legacy APIs and
* has no plan to be removed.
*/
@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.FloatValueInternal.CODEC::class)
public interface FloatValue { 
    /**
    * The float value.
    */
    public val value: Float

    /**
    * Copies the original message, including unknown fields.
    */
    public fun copy(body: com.google.protobuf.kotlin.FloatValueInternal.() -> Unit = {}): com.google.protobuf.kotlin.FloatValue

    public companion object
}

/**
* Wrapper message for `int64`.
* 
* The JSON representation for `Int64Value` is JSON string.
* 
* Not recommended for use in new APIs, but still useful for legacy APIs and
* has no plan to be removed.
*/
@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.Int64ValueInternal.CODEC::class)
public interface Int64Value { 
    /**
    * The int64 value.
    */
    public val value: Long

    /**
    * Copies the original message, including unknown fields.
    */
    public fun copy(body: com.google.protobuf.kotlin.Int64ValueInternal.() -> Unit = {}): com.google.protobuf.kotlin.Int64Value

    public companion object
}

/**
* Wrapper message for `uint64`.
* 
* The JSON representation for `UInt64Value` is JSON string.
* 
* Not recommended for use in new APIs, but still useful for legacy APIs and
* has no plan to be removed.
*/
@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.UInt64ValueInternal.CODEC::class)
public interface UInt64Value { 
    /**
    * The uint64 value.
    */
    public val value: ULong

    /**
    * Copies the original message, including unknown fields.
    */
    public fun copy(body: com.google.protobuf.kotlin.UInt64ValueInternal.() -> Unit = {}): com.google.protobuf.kotlin.UInt64Value

    public companion object
}

/**
* Wrapper message for `int32`.
* 
* The JSON representation for `Int32Value` is JSON number.
* 
* Not recommended for use in new APIs, but still useful for legacy APIs and
* has no plan to be removed.
*/
@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.Int32ValueInternal.CODEC::class)
public interface Int32Value { 
    /**
    * The int32 value.
    */
    public val value: Int

    /**
    * Copies the original message, including unknown fields.
    */
    public fun copy(body: com.google.protobuf.kotlin.Int32ValueInternal.() -> Unit = {}): com.google.protobuf.kotlin.Int32Value

    public companion object
}

/**
* Wrapper message for `uint32`.
* 
* The JSON representation for `UInt32Value` is JSON number.
* 
* Not recommended for use in new APIs, but still useful for legacy APIs and
* has no plan to be removed.
*/
@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.UInt32ValueInternal.CODEC::class)
public interface UInt32Value { 
    /**
    * The uint32 value.
    */
    public val value: UInt

    /**
    * Copies the original message, including unknown fields.
    */
    public fun copy(body: com.google.protobuf.kotlin.UInt32ValueInternal.() -> Unit = {}): com.google.protobuf.kotlin.UInt32Value

    public companion object
}

/**
* Wrapper message for `bool`.
* 
* The JSON representation for `BoolValue` is JSON `true` and `false`.
* 
* Not recommended for use in new APIs, but still useful for legacy APIs and
* has no plan to be removed.
*/
@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.BoolValueInternal.CODEC::class)
public interface BoolValue { 
    /**
    * The bool value.
    */
    public val value: Boolean

    /**
    * Copies the original message, including unknown fields.
    */
    public fun copy(body: com.google.protobuf.kotlin.BoolValueInternal.() -> Unit = {}): com.google.protobuf.kotlin.BoolValue

    public companion object
}

/**
* Wrapper message for `string`.
* 
* The JSON representation for `StringValue` is JSON string.
* 
* Not recommended for use in new APIs, but still useful for legacy APIs and
* has no plan to be removed.
*/
@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.StringValueInternal.CODEC::class)
public interface StringValue { 
    /**
    * The string value.
    */
    public val value: String

    /**
    * Copies the original message, including unknown fields.
    */
    public fun copy(body: com.google.protobuf.kotlin.StringValueInternal.() -> Unit = {}): com.google.protobuf.kotlin.StringValue

    public companion object
}

/**
* Wrapper message for `bytes`.
* 
* The JSON representation for `BytesValue` is JSON string.
* 
* Not recommended for use in new APIs, but still useful for legacy APIs and
* has no plan to be removed.
*/
@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.BytesValueInternal.CODEC::class)
public interface BytesValue { 
    /**
    * The bytes value.
    */
    public val value: ByteArray

    /**
    * Copies the original message, including unknown fields.
    */
    public fun copy(body: com.google.protobuf.kotlin.BytesValueInternal.() -> Unit = {}): com.google.protobuf.kotlin.BytesValue

    public companion object
}
