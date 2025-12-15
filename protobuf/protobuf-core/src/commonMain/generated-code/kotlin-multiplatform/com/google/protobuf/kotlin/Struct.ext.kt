@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlin.jvm.JvmInline
import kotlinx.rpc.internal.utils.*

/**
* Constructs a new message.
* ```
* val message = Struct {
*    fields = ...
* }
* ```
*/
public operator fun com.google.protobuf.kotlin.Struct.Companion.invoke(body: com.google.protobuf.kotlin.StructInternal.() -> Unit): com.google.protobuf.kotlin.Struct { 
    val msg = com.google.protobuf.kotlin.StructInternal().apply(body)
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
public fun com.google.protobuf.kotlin.Struct.copy(body: com.google.protobuf.kotlin.StructInternal.() -> Unit = {}): com.google.protobuf.kotlin.Struct { 
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
public operator fun com.google.protobuf.kotlin.Value.Companion.invoke(body: com.google.protobuf.kotlin.ValueInternal.() -> Unit): com.google.protobuf.kotlin.Value { 
    val msg = com.google.protobuf.kotlin.ValueInternal().apply(body)
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
public fun com.google.protobuf.kotlin.Value.copy(body: com.google.protobuf.kotlin.ValueInternal.() -> Unit = {}): com.google.protobuf.kotlin.Value { 
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
public operator fun com.google.protobuf.kotlin.ListValue.Companion.invoke(body: com.google.protobuf.kotlin.ListValueInternal.() -> Unit): com.google.protobuf.kotlin.ListValue { 
    val msg = com.google.protobuf.kotlin.ListValueInternal().apply(body)
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
public fun com.google.protobuf.kotlin.ListValue.copy(body: com.google.protobuf.kotlin.ListValueInternal.() -> Unit = {}): com.google.protobuf.kotlin.ListValue { 
    return this.asInternal().copyInternal(body)
}
