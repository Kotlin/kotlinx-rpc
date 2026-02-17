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
public operator fun DoubleValue.Companion.invoke(body: DoubleValueInternal.() -> Unit): DoubleValue {
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
public fun DoubleValue.copy(body: DoubleValueInternal.() -> Unit = {}): DoubleValue {
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
public operator fun FloatValue.Companion.invoke(body: FloatValueInternal.() -> Unit): FloatValue {
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
public fun FloatValue.copy(body: FloatValueInternal.() -> Unit = {}): FloatValue {
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
public operator fun Int64Value.Companion.invoke(body: Int64ValueInternal.() -> Unit): Int64Value {
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
public fun Int64Value.copy(body: Int64ValueInternal.() -> Unit = {}): Int64Value {
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
public operator fun UInt64Value.Companion.invoke(body: UInt64ValueInternal.() -> Unit): UInt64Value {
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
public fun UInt64Value.copy(body: UInt64ValueInternal.() -> Unit = {}): UInt64Value {
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
public operator fun Int32Value.Companion.invoke(body: Int32ValueInternal.() -> Unit): Int32Value {
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
public fun Int32Value.copy(body: Int32ValueInternal.() -> Unit = {}): Int32Value {
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
public operator fun UInt32Value.Companion.invoke(body: UInt32ValueInternal.() -> Unit): UInt32Value {
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
public fun UInt32Value.copy(body: UInt32ValueInternal.() -> Unit = {}): UInt32Value {
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
public operator fun BoolValue.Companion.invoke(body: BoolValueInternal.() -> Unit): BoolValue {
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
public fun BoolValue.copy(body: BoolValueInternal.() -> Unit = {}): BoolValue {
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
public operator fun StringValue.Companion.invoke(body: StringValueInternal.() -> Unit): StringValue {
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
public fun StringValue.copy(body: StringValueInternal.() -> Unit = {}): StringValue {
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
public operator fun BytesValue.Companion.invoke(body: BytesValueInternal.() -> Unit): BytesValue {
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
public fun BytesValue.copy(body: BytesValueInternal.() -> Unit = {}): BytesValue {
    return this.asInternal().copyInternal(body)
}
