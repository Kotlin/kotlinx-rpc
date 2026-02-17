@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
* Constructs a new message.
* ```
* val message = Empty { }
* ```
*/
public operator fun Empty.Companion.invoke(body: EmptyInternal.() -> Unit): Empty {
    val msg = EmptyInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy()
* ```
*/
public fun Empty.copy(body: EmptyInternal.() -> Unit = {}): Empty {
    return this.asInternal().copyInternal(body)
}
