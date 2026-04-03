@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
* Constructs a new message.
* ```
* val message = Api {
*    name = ...
* }
* ```
*/
public operator fun Api.Companion.invoke(body: Api.Builder.() -> Unit): Api {
    val msg = ApiInternal().apply(body)
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
public fun Api.copy(body: Api.Builder.() -> Unit = {}): Api {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.Api] instance.
*/
public val Api.presence: ApiPresence get() = this.asInternal()._presence

/**
* Returns the value of the `sourceContext` field if present, otherwise null.
*/
public val Api.sourceContextOrNull: SourceContext? get() = if (this.presence.hasSourceContext) this.sourceContext else null

/**
* Constructs a new message.
* ```
* val message = Method {
*    name = ...
* }
* ```
*/
public operator fun Method.Companion.invoke(body: Method.Builder.() -> Unit): Method {
    val msg = MethodInternal().apply(body)
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
public fun Method.copy(body: Method.Builder.() -> Unit = {}): Method {
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
public operator fun Mixin.Companion.invoke(body: Mixin.Builder.() -> Unit): Mixin {
    val msg = MixinInternal().apply(body)
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
public fun Mixin.copy(body: Mixin.Builder.() -> Unit = {}): Mixin {
    return this.asInternal().copyInternal(body)
}

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.Api] messages.
* Retrieve it via the [com.google.protobuf.kotlin.Api.presence] extension property.
*/
public interface ApiPresence {
    public val hasSourceContext: Boolean
}
