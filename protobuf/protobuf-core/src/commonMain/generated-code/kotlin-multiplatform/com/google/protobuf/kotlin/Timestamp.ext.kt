@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*

/**
* Constructs a new message.
* ```
* val message = Timestamp {
*    seconds = ...
* }
* ```
*/
public operator fun com.google.protobuf.kotlin.Timestamp.Companion.invoke(body: com.google.protobuf.kotlin.TimestampInternal.() -> Unit): com.google.protobuf.kotlin.Timestamp { 
    val msg = com.google.protobuf.kotlin.TimestampInternal().apply(body)
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
public fun com.google.protobuf.kotlin.Timestamp.copy(body: com.google.protobuf.kotlin.TimestampInternal.() -> Unit = {}): com.google.protobuf.kotlin.Timestamp { 
    return this.asInternal().copyInternal(body)
}
