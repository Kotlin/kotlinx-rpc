@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*

/**
* Constructs a new message.
* ```
* val message = Api {
*    name = ...
* }
* ```
*/
public operator fun com.google.protobuf.kotlin.Api.Companion.invoke(body: com.google.protobuf.kotlin.ApiInternal.() -> Unit): com.google.protobuf.kotlin.Api { 
    val msg = com.google.protobuf.kotlin.ApiInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    name = ...
* }
* ```
*/
public fun com.google.protobuf.kotlin.Api.copy(body: com.google.protobuf.kotlin.ApiInternal.() -> Unit = {}): com.google.protobuf.kotlin.Api { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.Api] instance.
*/
public val com.google.protobuf.kotlin.Api.presence: com.google.protobuf.kotlin.ApiPresence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = Method {
*    name = ...
* }
* ```
*/
public operator fun com.google.protobuf.kotlin.Method.Companion.invoke(body: com.google.protobuf.kotlin.MethodInternal.() -> Unit): com.google.protobuf.kotlin.Method { 
    val msg = com.google.protobuf.kotlin.MethodInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    name = ...
* }
* ```
*/
public fun com.google.protobuf.kotlin.Method.copy(body: com.google.protobuf.kotlin.MethodInternal.() -> Unit = {}): com.google.protobuf.kotlin.Method { 
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = Mixin {
*    name = ...
* }
* ```
*/
public operator fun com.google.protobuf.kotlin.Mixin.Companion.invoke(body: com.google.protobuf.kotlin.MixinInternal.() -> Unit): com.google.protobuf.kotlin.Mixin { 
    val msg = com.google.protobuf.kotlin.MixinInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    name = ...
* }
* ```
*/
public fun com.google.protobuf.kotlin.Mixin.copy(body: com.google.protobuf.kotlin.MixinInternal.() -> Unit = {}): com.google.protobuf.kotlin.Mixin { 
    return this.asInternal().copyInternal(body)
}

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.Api] messages.
* Retrieve it via the [com.google.protobuf.kotlin.Api.presence] extension property.
*/
public interface ApiPresence { 
    public val hasSourceContext: kotlin.Boolean
}
