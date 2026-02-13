@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
* Constructs a new message.
* ```
* val message = Timestamp {
*    seconds = ...
* }
* ```
*/
public operator fun Timestamp.Companion.invoke(body: TimestampInternal.() -> Unit): Timestamp {
    val msg = TimestampInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    seconds = ...
* }
* ```
*/
public fun Timestamp.copy(body: TimestampInternal.() -> Unit = {}): Timestamp {
    return this.asInternal().copyInternal(body)
}
