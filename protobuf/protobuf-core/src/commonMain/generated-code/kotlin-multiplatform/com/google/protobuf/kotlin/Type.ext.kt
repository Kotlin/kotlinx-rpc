@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
* Constructs a new message.
* ```
* val message = Type {
*    name = ...
* }
* ```
*/
public operator fun Type.Companion.invoke(body: TypeInternal.() -> Unit): Type {
    val msg = TypeInternal().apply(body)
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
public fun Type.copy(body: TypeInternal.() -> Unit = {}): Type {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.Type] instance.
*/
public val Type.presence: TypePresence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = Field {
*    kind = ...
* }
* ```
*/
public operator fun Field.Companion.invoke(body: FieldInternal.() -> Unit): Field {
    val msg = FieldInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    kind = ...
* }
* ```
*/
public fun Field.copy(body: FieldInternal.() -> Unit = {}): Field {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = Enum {
*    name = ...
* }
* ```
*/
public operator fun Enum.Companion.invoke(body: EnumInternal.() -> Unit): Enum {
    val msg = EnumInternal().apply(body)
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
public fun Enum.copy(body: EnumInternal.() -> Unit = {}): Enum {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.Enum] instance.
*/
public val Enum.presence: EnumPresence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = EnumValue {
*    name = ...
* }
* ```
*/
public operator fun EnumValue.Companion.invoke(body: EnumValueInternal.() -> Unit): EnumValue {
    val msg = EnumValueInternal().apply(body)
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
public fun EnumValue.copy(body: EnumValueInternal.() -> Unit = {}): EnumValue {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = Option {
*    name = ...
* }
* ```
*/
public operator fun Option.Companion.invoke(body: OptionInternal.() -> Unit): Option {
    val msg = OptionInternal().apply(body)
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
public fun Option.copy(body: OptionInternal.() -> Unit = {}): Option {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.Option] instance.
*/
public val Option.presence: OptionPresence get() = this.asInternal()._presence

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.Type] messages.
* Retrieve it via the [com.google.protobuf.kotlin.Type.presence] extension property.
*/
public interface TypePresence {
    public val hasSourceContext: Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.Enum] messages.
* Retrieve it via the [com.google.protobuf.kotlin.Enum.presence] extension property.
*/
public interface EnumPresence {
    public val hasSourceContext: Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.Option] messages.
* Retrieve it via the [com.google.protobuf.kotlin.Option.presence] extension property.
*/
public interface OptionPresence {
    public val hasValue: Boolean
}
