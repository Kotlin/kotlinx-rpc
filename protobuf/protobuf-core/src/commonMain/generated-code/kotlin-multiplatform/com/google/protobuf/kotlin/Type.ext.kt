@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*

/**
* Constructs a new message.
* ```
* val message = Type {
*    name = ...
* }
* ```
*/
public operator fun com.google.protobuf.kotlin.Type.Companion.invoke(body: com.google.protobuf.kotlin.TypeInternal.() -> Unit): com.google.protobuf.kotlin.Type { 
    val msg = com.google.protobuf.kotlin.TypeInternal().apply(body)
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
public fun com.google.protobuf.kotlin.Type.copy(body: com.google.protobuf.kotlin.TypeInternal.() -> Unit = {}): com.google.protobuf.kotlin.Type { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.Type] instance.
*/
public val com.google.protobuf.kotlin.Type.presence: com.google.protobuf.kotlin.TypePresence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = Field {
*    kind = ...
* }
* ```
*/
public operator fun com.google.protobuf.kotlin.Field.Companion.invoke(body: com.google.protobuf.kotlin.FieldInternal.() -> Unit): com.google.protobuf.kotlin.Field { 
    val msg = com.google.protobuf.kotlin.FieldInternal().apply(body)
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
public fun com.google.protobuf.kotlin.Field.copy(body: com.google.protobuf.kotlin.FieldInternal.() -> Unit = {}): com.google.protobuf.kotlin.Field { 
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
public operator fun com.google.protobuf.kotlin.Enum.Companion.invoke(body: com.google.protobuf.kotlin.EnumInternal.() -> Unit): com.google.protobuf.kotlin.Enum { 
    val msg = com.google.protobuf.kotlin.EnumInternal().apply(body)
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
public fun com.google.protobuf.kotlin.Enum.copy(body: com.google.protobuf.kotlin.EnumInternal.() -> Unit = {}): com.google.protobuf.kotlin.Enum { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.Enum] instance.
*/
public val com.google.protobuf.kotlin.Enum.presence: com.google.protobuf.kotlin.EnumPresence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = EnumValue {
*    name = ...
* }
* ```
*/
public operator fun com.google.protobuf.kotlin.EnumValue.Companion.invoke(body: com.google.protobuf.kotlin.EnumValueInternal.() -> Unit): com.google.protobuf.kotlin.EnumValue { 
    val msg = com.google.protobuf.kotlin.EnumValueInternal().apply(body)
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
public fun com.google.protobuf.kotlin.EnumValue.copy(body: com.google.protobuf.kotlin.EnumValueInternal.() -> Unit = {}): com.google.protobuf.kotlin.EnumValue { 
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
public operator fun com.google.protobuf.kotlin.Option.Companion.invoke(body: com.google.protobuf.kotlin.OptionInternal.() -> Unit): com.google.protobuf.kotlin.Option { 
    val msg = com.google.protobuf.kotlin.OptionInternal().apply(body)
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
public fun com.google.protobuf.kotlin.Option.copy(body: com.google.protobuf.kotlin.OptionInternal.() -> Unit = {}): com.google.protobuf.kotlin.Option { 
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.Option] instance.
*/
public val com.google.protobuf.kotlin.Option.presence: com.google.protobuf.kotlin.OptionPresence get() = this.asInternal()._presence

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.Type] messages.
* Retrieve it via the [com.google.protobuf.kotlin.Type.presence] extension property.
*/
public interface TypePresence { 
    public val hasSourceContext: kotlin.Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.Enum] messages.
* Retrieve it via the [com.google.protobuf.kotlin.Enum.presence] extension property.
*/
public interface EnumPresence { 
    public val hasSourceContext: kotlin.Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.Option] messages.
* Retrieve it via the [com.google.protobuf.kotlin.Option.presence] extension property.
*/
public interface OptionPresence { 
    public val hasValue: kotlin.Boolean
}
