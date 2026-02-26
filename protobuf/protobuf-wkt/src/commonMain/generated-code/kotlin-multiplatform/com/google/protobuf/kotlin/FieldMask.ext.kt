@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
* Constructs a new message.
* ```
* val message = FieldMask {
*    paths = ...
* }
* ```
*/
public operator fun FieldMask.Companion.invoke(body: FieldMaskInternal.() -> Unit): FieldMask {
    val msg = FieldMaskInternal().apply(body)
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
public fun FieldMask.copy(body: FieldMaskInternal.() -> Unit = {}): FieldMask {
    return this.asInternal().copyInternal(body)
}
