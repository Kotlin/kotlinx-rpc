@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*

/**
* Constructs a new message.
* ```
* val message = FieldMask {
*    paths = ...
* }
* ```
*/
public operator fun com.google.protobuf.kotlin.FieldMask.Companion.invoke(body: com.google.protobuf.kotlin.FieldMaskInternal.() -> Unit): com.google.protobuf.kotlin.FieldMask { 
    val msg = com.google.protobuf.kotlin.FieldMaskInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    paths = ...
* }
* ```
*/
public fun com.google.protobuf.kotlin.FieldMask.copy(body: com.google.protobuf.kotlin.FieldMaskInternal.() -> Unit = {}): com.google.protobuf.kotlin.FieldMask { 
    return this.asInternal().copyInternal(body)
}
