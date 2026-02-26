@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
* Constructs a new message.
* ```
* val message = Any {
*    typeUrl = ...
* }
* ```
*/
public operator fun Any.Companion.invoke(body: AnyInternal.() -> Unit): Any {
    val msg = AnyInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    typeUrl = ...
* }
* ```
*/
public fun Any.copy(body: AnyInternal.() -> Unit = {}): Any {
    return this.asInternal().copyInternal(body)
}
