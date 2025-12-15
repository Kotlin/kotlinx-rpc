@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*

/**
* Constructs a new message.
* ```
* val message = SourceContext {
*    fileName = ...
* }
* ```
*/
public operator fun com.google.protobuf.kotlin.SourceContext.Companion.invoke(body: com.google.protobuf.kotlin.SourceContextInternal.() -> Unit): com.google.protobuf.kotlin.SourceContext { 
    val msg = com.google.protobuf.kotlin.SourceContextInternal().apply(body)
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
public fun com.google.protobuf.kotlin.SourceContext.copy(body: com.google.protobuf.kotlin.SourceContextInternal.() -> Unit = {}): com.google.protobuf.kotlin.SourceContext { 
    return this.asInternal().copyInternal(body)
}
