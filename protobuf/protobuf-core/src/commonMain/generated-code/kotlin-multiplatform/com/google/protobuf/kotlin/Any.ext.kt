@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*

/**
* Constructs a new message.
* ```
* val message = Any {
*    typeUrl = ...
* }
* ```
*/
public operator fun com.google.protobuf.kotlin.Any.Companion.invoke(body: com.google.protobuf.kotlin.AnyInternal.() -> Unit): com.google.protobuf.kotlin.Any { 
    val msg = com.google.protobuf.kotlin.AnyInternal().apply(body)
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
public fun com.google.protobuf.kotlin.Any.copy(body: com.google.protobuf.kotlin.AnyInternal.() -> Unit = {}): com.google.protobuf.kotlin.Any { 
    return this.asInternal().copyInternal(body)
}
