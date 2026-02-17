@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.grpc.codec.WithCodec
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
* Wrapper message for `double`.
* 
* The JSON representation for `DoubleValue` is JSON number.
* 
* Not recommended for use in new APIs, but still useful for legacy APIs and
* has no plan to be removed.
*/
@WithCodec(DoubleValueInternal.CODEC::class)
public interface DoubleValue {
    /**
    * The double value.
    */
    public val value: Double

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
@WithCodec(FloatValueInternal.CODEC::class)
public interface FloatValue {
    /**
    * The float value.
    */
    public val value: Float

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
@WithCodec(Int64ValueInternal.CODEC::class)
public interface Int64Value {
    /**
    * The int64 value.
    */
    public val value: Long

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
@WithCodec(UInt64ValueInternal.CODEC::class)
public interface UInt64Value {
    /**
    * The uint64 value.
    */
    public val value: ULong

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
@WithCodec(Int32ValueInternal.CODEC::class)
public interface Int32Value {
    /**
    * The int32 value.
    */
    public val value: Int

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
@WithCodec(UInt32ValueInternal.CODEC::class)
public interface UInt32Value {
    /**
    * The uint32 value.
    */
    public val value: UInt

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
@WithCodec(BoolValueInternal.CODEC::class)
public interface BoolValue {
    /**
    * The bool value.
    */
    public val value: Boolean

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
@WithCodec(StringValueInternal.CODEC::class)
public interface StringValue {
    /**
    * The string value.
    */
    public val value: String

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
@WithCodec(BytesValueInternal.CODEC::class)
public interface BytesValue {
    /**
    * The bytes value.
    */
    public val value: ByteArray

    public companion object
}
