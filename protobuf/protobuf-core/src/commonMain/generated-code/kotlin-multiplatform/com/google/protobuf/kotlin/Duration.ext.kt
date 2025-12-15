@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*

/**
* Constructs a new message.
* ```
* val message = Duration {
*    seconds = ...
* }
* ```
*/
public operator fun com.google.protobuf.kotlin.Duration.Companion.invoke(body: com.google.protobuf.kotlin.DurationInternal.() -> Unit): com.google.protobuf.kotlin.Duration { 
    val msg = com.google.protobuf.kotlin.DurationInternal().apply(body)
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
public fun com.google.protobuf.kotlin.Duration.copy(body: com.google.protobuf.kotlin.DurationInternal.() -> Unit = {}): com.google.protobuf.kotlin.Duration { 
    return this.asInternal().copyInternal(body)
}
