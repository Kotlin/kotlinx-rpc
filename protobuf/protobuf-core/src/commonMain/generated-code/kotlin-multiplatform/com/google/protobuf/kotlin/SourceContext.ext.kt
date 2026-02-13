@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
* Constructs a new message.
* ```
* val message = SourceContext {
*    fileName = ...
* }
* ```
*/
public operator fun SourceContext.Companion.invoke(body: SourceContextInternal.() -> Unit): SourceContext {
    val msg = SourceContextInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    fileName = ...
* }
* ```
*/
public fun SourceContext.copy(body: SourceContextInternal.() -> Unit = {}): SourceContext {
    return this.asInternal().copyInternal(body)
}
