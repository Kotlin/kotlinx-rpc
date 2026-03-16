@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.internal.GeneratedProtoMessage

/**
* Wrapper message for `double`.
* 
* The JSON representation for `DoubleValue` is JSON number.
* 
* Not recommended for use in new APIs, but still useful for legacy APIs and
* has no plan to be removed.
*/
@GeneratedProtoMessage
public interface DoubleValue {
    /**
    * The double value.
    */
    public val value: Double
}

/**
* Wrapper message for `float`.
* 
* The JSON representation for `FloatValue` is JSON number.
* 
* Not recommended for use in new APIs, but still useful for legacy APIs and
* has no plan to be removed.
*/
@GeneratedProtoMessage
public interface FloatValue {
    /**
    * The float value.
    */
    public val value: Float
}

/**
* Wrapper message for `int64`.
* 
* The JSON representation for `Int64Value` is JSON string.
* 
* Not recommended for use in new APIs, but still useful for legacy APIs and
* has no plan to be removed.
*/
@GeneratedProtoMessage
public interface Int64Value {
    /**
    * The int64 value.
    */
    public val value: Long
}

/**
* Wrapper message for `uint64`.
* 
* The JSON representation for `UInt64Value` is JSON string.
* 
* Not recommended for use in new APIs, but still useful for legacy APIs and
* has no plan to be removed.
*/
@GeneratedProtoMessage
public interface UInt64Value {
    /**
    * The uint64 value.
    */
    public val value: ULong
}

/**
* Wrapper message for `int32`.
* 
* The JSON representation for `Int32Value` is JSON number.
* 
* Not recommended for use in new APIs, but still useful for legacy APIs and
* has no plan to be removed.
*/
@GeneratedProtoMessage
public interface Int32Value {
    /**
    * The int32 value.
    */
    public val value: Int
}

/**
* Wrapper message for `uint32`.
* 
* The JSON representation for `UInt32Value` is JSON number.
* 
* Not recommended for use in new APIs, but still useful for legacy APIs and
* has no plan to be removed.
*/
@GeneratedProtoMessage
public interface UInt32Value {
    /**
    * The uint32 value.
    */
    public val value: UInt
}

/**
* Wrapper message for `bool`.
* 
* The JSON representation for `BoolValue` is JSON `true` and `false`.
* 
* Not recommended for use in new APIs, but still useful for legacy APIs and
* has no plan to be removed.
*/
@GeneratedProtoMessage
public interface BoolValue {
    /**
    * The bool value.
    */
    public val value: Boolean
}

/**
* Wrapper message for `string`.
* 
* The JSON representation for `StringValue` is JSON string.
* 
* Not recommended for use in new APIs, but still useful for legacy APIs and
* has no plan to be removed.
*/
@GeneratedProtoMessage
public interface StringValue {
    /**
    * The string value.
    */
    public val value: String
}

/**
* Wrapper message for `bytes`.
* 
* The JSON representation for `BytesValue` is JSON string.
* 
* Not recommended for use in new APIs, but still useful for legacy APIs and
* has no plan to be removed.
*/
@GeneratedProtoMessage
public interface BytesValue {
    /**
    * The bytes value.
    */
    public val value: ByteString
}
