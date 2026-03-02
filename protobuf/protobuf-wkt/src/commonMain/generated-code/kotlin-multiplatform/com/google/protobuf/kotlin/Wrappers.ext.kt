@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
* Constructs a new message.
* ```
* val message = DoubleValue {
*    value = ...
* }
* ```
*/
public operator fun DoubleValue.Companion.invoke(body: DoubleValue.Builder.() -> Unit): DoubleValue {
    val msg = DoubleValueInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    value = ...
* }
* ```
*/
public fun DoubleValue.copy(body: DoubleValue.Builder.() -> Unit = {}): DoubleValue {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = FloatValue {
*    value = ...
* }
* ```
*/
public operator fun FloatValue.Companion.invoke(body: FloatValue.Builder.() -> Unit): FloatValue {
    val msg = FloatValueInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    value = ...
* }
* ```
*/
public fun FloatValue.copy(body: FloatValue.Builder.() -> Unit = {}): FloatValue {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = Int64Value {
*    value = ...
* }
* ```
*/
public operator fun Int64Value.Companion.invoke(body: Int64Value.Builder.() -> Unit): Int64Value {
    val msg = Int64ValueInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    value = ...
* }
* ```
*/
public fun Int64Value.copy(body: Int64Value.Builder.() -> Unit = {}): Int64Value {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = UInt64Value {
*    value = ...
* }
* ```
*/
public operator fun UInt64Value.Companion.invoke(body: UInt64Value.Builder.() -> Unit): UInt64Value {
    val msg = UInt64ValueInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    value = ...
* }
* ```
*/
public fun UInt64Value.copy(body: UInt64Value.Builder.() -> Unit = {}): UInt64Value {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = Int32Value {
*    value = ...
* }
* ```
*/
public operator fun Int32Value.Companion.invoke(body: Int32Value.Builder.() -> Unit): Int32Value {
    val msg = Int32ValueInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    value = ...
* }
* ```
*/
public fun Int32Value.copy(body: Int32Value.Builder.() -> Unit = {}): Int32Value {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = UInt32Value {
*    value = ...
* }
* ```
*/
public operator fun UInt32Value.Companion.invoke(body: UInt32Value.Builder.() -> Unit): UInt32Value {
    val msg = UInt32ValueInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    value = ...
* }
* ```
*/
public fun UInt32Value.copy(body: UInt32Value.Builder.() -> Unit = {}): UInt32Value {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = BoolValue {
*    value = ...
* }
* ```
*/
public operator fun BoolValue.Companion.invoke(body: BoolValue.Builder.() -> Unit): BoolValue {
    val msg = BoolValueInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    value = ...
* }
* ```
*/
public fun BoolValue.copy(body: BoolValue.Builder.() -> Unit = {}): BoolValue {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = StringValue {
*    value = ...
* }
* ```
*/
public operator fun StringValue.Companion.invoke(body: StringValue.Builder.() -> Unit): StringValue {
    val msg = StringValueInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    value = ...
* }
* ```
*/
public fun StringValue.copy(body: StringValue.Builder.() -> Unit = {}): StringValue {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = BytesValue {
*    value = ...
* }
* ```
*/
public operator fun BytesValue.Companion.invoke(body: BytesValue.Builder.() -> Unit): BytesValue {
    val msg = BytesValueInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    value = ...
* }
* ```
*/
public fun BytesValue.copy(body: BytesValue.Builder.() -> Unit = {}): BytesValue {
    return this.asInternal().copyInternal(body)
}
