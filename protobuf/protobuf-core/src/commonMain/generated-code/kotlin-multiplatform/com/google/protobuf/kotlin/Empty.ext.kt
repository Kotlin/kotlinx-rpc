@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*

/**
* Constructs a new message.
* ```
* val message = Empty { }
* ```
*/
public operator fun com.google.protobuf.kotlin.Empty.Companion.invoke(body: com.google.protobuf.kotlin.EmptyInternal.() -> Unit): com.google.protobuf.kotlin.Empty { 
    val msg = com.google.protobuf.kotlin.EmptyInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy()
* ```
*/
public fun com.google.protobuf.kotlin.Empty.copy(body: com.google.protobuf.kotlin.EmptyInternal.() -> Unit = {}): com.google.protobuf.kotlin.Empty { 
    return this.asInternal().copyInternal(body)
}
