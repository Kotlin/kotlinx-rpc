@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*

/**
* Constructs a new message.
* ```
* val message = DoubleValue {
*    value = ...
* }
* ```
*/
public operator fun com.google.protobuf.kotlin.DoubleValue.Companion.invoke(body: com.google.protobuf.kotlin.DoubleValueInternal.() -> Unit): com.google.protobuf.kotlin.DoubleValue { 
    val msg = com.google.protobuf.kotlin.DoubleValueInternal().apply(body)
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
public fun com.google.protobuf.kotlin.DoubleValue.copy(body: com.google.protobuf.kotlin.DoubleValueInternal.() -> Unit = {}): com.google.protobuf.kotlin.DoubleValue { 
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
public operator fun com.google.protobuf.kotlin.FloatValue.Companion.invoke(body: com.google.protobuf.kotlin.FloatValueInternal.() -> Unit): com.google.protobuf.kotlin.FloatValue { 
    val msg = com.google.protobuf.kotlin.FloatValueInternal().apply(body)
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
public fun com.google.protobuf.kotlin.FloatValue.copy(body: com.google.protobuf.kotlin.FloatValueInternal.() -> Unit = {}): com.google.protobuf.kotlin.FloatValue { 
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
public operator fun com.google.protobuf.kotlin.Int64Value.Companion.invoke(body: com.google.protobuf.kotlin.Int64ValueInternal.() -> Unit): com.google.protobuf.kotlin.Int64Value { 
    val msg = com.google.protobuf.kotlin.Int64ValueInternal().apply(body)
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
public fun com.google.protobuf.kotlin.Int64Value.copy(body: com.google.protobuf.kotlin.Int64ValueInternal.() -> Unit = {}): com.google.protobuf.kotlin.Int64Value { 
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
public operator fun com.google.protobuf.kotlin.UInt64Value.Companion.invoke(body: com.google.protobuf.kotlin.UInt64ValueInternal.() -> Unit): com.google.protobuf.kotlin.UInt64Value { 
    val msg = com.google.protobuf.kotlin.UInt64ValueInternal().apply(body)
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
public fun com.google.protobuf.kotlin.UInt64Value.copy(body: com.google.protobuf.kotlin.UInt64ValueInternal.() -> Unit = {}): com.google.protobuf.kotlin.UInt64Value { 
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
public operator fun com.google.protobuf.kotlin.Int32Value.Companion.invoke(body: com.google.protobuf.kotlin.Int32ValueInternal.() -> Unit): com.google.protobuf.kotlin.Int32Value { 
    val msg = com.google.protobuf.kotlin.Int32ValueInternal().apply(body)
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
public fun com.google.protobuf.kotlin.Int32Value.copy(body: com.google.protobuf.kotlin.Int32ValueInternal.() -> Unit = {}): com.google.protobuf.kotlin.Int32Value { 
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
public operator fun com.google.protobuf.kotlin.UInt32Value.Companion.invoke(body: com.google.protobuf.kotlin.UInt32ValueInternal.() -> Unit): com.google.protobuf.kotlin.UInt32Value { 
    val msg = com.google.protobuf.kotlin.UInt32ValueInternal().apply(body)
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
public fun com.google.protobuf.kotlin.UInt32Value.copy(body: com.google.protobuf.kotlin.UInt32ValueInternal.() -> Unit = {}): com.google.protobuf.kotlin.UInt32Value { 
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
public operator fun com.google.protobuf.kotlin.BoolValue.Companion.invoke(body: com.google.protobuf.kotlin.BoolValueInternal.() -> Unit): com.google.protobuf.kotlin.BoolValue { 
    val msg = com.google.protobuf.kotlin.BoolValueInternal().apply(body)
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
public fun com.google.protobuf.kotlin.BoolValue.copy(body: com.google.protobuf.kotlin.BoolValueInternal.() -> Unit = {}): com.google.protobuf.kotlin.BoolValue { 
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
public operator fun com.google.protobuf.kotlin.StringValue.Companion.invoke(body: com.google.protobuf.kotlin.StringValueInternal.() -> Unit): com.google.protobuf.kotlin.StringValue { 
    val msg = com.google.protobuf.kotlin.StringValueInternal().apply(body)
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
public fun com.google.protobuf.kotlin.StringValue.copy(body: com.google.protobuf.kotlin.StringValueInternal.() -> Unit = {}): com.google.protobuf.kotlin.StringValue { 
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
public operator fun com.google.protobuf.kotlin.BytesValue.Companion.invoke(body: com.google.protobuf.kotlin.BytesValueInternal.() -> Unit): com.google.protobuf.kotlin.BytesValue { 
    val msg = com.google.protobuf.kotlin.BytesValueInternal().apply(body)
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
public fun com.google.protobuf.kotlin.BytesValue.copy(body: com.google.protobuf.kotlin.BytesValueInternal.() -> Unit = {}): com.google.protobuf.kotlin.BytesValue { 
    return this.asInternal().copyInternal(body)
}
