@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
* Constructs a new message.
* ```
* val message = Duration {
*    seconds = ...
* }
* ```
*/
public operator fun Duration.Companion.invoke(body: DurationInternal.() -> Unit): Duration {
    val msg = DurationInternal().apply(body)
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
public fun Duration.copy(body: DurationInternal.() -> Unit = {}): Duration {
    return this.asInternal().copyInternal(body)
}
