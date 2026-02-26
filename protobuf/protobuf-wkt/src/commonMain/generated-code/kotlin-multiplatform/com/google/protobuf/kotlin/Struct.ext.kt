@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
* Constructs a new message.
* ```
* val message = Struct {
*    fields = ...
* }
* ```
*/
public operator fun Struct.Companion.invoke(body: StructInternal.() -> Unit): Struct {
    val msg = StructInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    fields = ...
* }
* ```
*/
public fun Struct.copy(body: StructInternal.() -> Unit = {}): Struct {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = Value {
*    kind = ...
* }
* ```
*/
public operator fun Value.Companion.invoke(body: ValueInternal.() -> Unit): Value {
    val msg = ValueInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    kind = ...
* }
* ```
*/
public fun Value.copy(body: ValueInternal.() -> Unit = {}): Value {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = ListValue {
*    values = ...
* }
* ```
*/
public operator fun ListValue.Companion.invoke(body: ListValueInternal.() -> Unit): ListValue {
    val msg = ListValueInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    values = ...
* }
* ```
*/
public fun ListValue.copy(body: ListValueInternal.() -> Unit = {}): ListValue {
    return this.asInternal().copyInternal(body)
}
