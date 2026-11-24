@file:OptIn(InternalRpcApi::class)
@file:Suppress("unused")

package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.InternalForSubclassingRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    name = ...
 * }
 * ```
 */
public fun Type.copy(body: Type.Builder.() -> Unit = {}): Type {
    return this.asInternal().copyInternal(body)
}

/**
 * Returns the field-presence view for this [Type] instance.
 */
public val Type.presence: TypePresence get() = this.asInternal()._presence

/**
 * Returns the value of the `sourceContext` field if present, otherwise null.
 */
public val Type.sourceContextOrNull: SourceContext? get() = if (this.presence.hasSourceContext) this.sourceContext else null

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    kind = ...
 * }
 * ```
 */
public fun Field.copy(body: Field.Builder.() -> Unit = {}): Field {
    return this.asInternal().copyInternal(body)
}

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    name = ...
 * }
 * ```
 */
public fun Enum.copy(body: Enum.Builder.() -> Unit = {}): Enum {
    return this.asInternal().copyInternal(body)
}

/**
 * Returns the field-presence view for this [Enum] instance.
 */
public val Enum.presence: EnumPresence get() = this.asInternal()._presence

/**
 * Returns the value of the `sourceContext` field if present, otherwise null.
 */
public val Enum.sourceContextOrNull: SourceContext? get() = if (this.presence.hasSourceContext) this.sourceContext else null

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    name = ...
 * }
 * ```
 */
public fun EnumValue.copy(body: EnumValue.Builder.() -> Unit = {}): EnumValue {
    return this.asInternal().copyInternal(body)
}

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    name = ...
 * }
 * ```
 */
public fun Option.copy(body: Option.Builder.() -> Unit = {}): Option {
    return this.asInternal().copyInternal(body)
}

/**
 * Returns the field-presence view for this [Option] instance.
 */
public val Option.presence: OptionPresence get() = this.asInternal()._presence

/**
 * Returns the value of the `value` field if present, otherwise null.
 */
public val Option.valueOrNull: Any? get() = if (this.presence.hasValue) this.value else null

/**
 * Interface providing field-presence information for [Type] messages.
 * Retrieve it via the [Type.presence] extension property.
 */
@SubclassOptInRequired(InternalForSubclassingRpcApi::class)
public interface TypePresence {
    public val hasSourceContext: Boolean
}

/**
 * Interface providing field-presence information for [Enum] messages.
 * Retrieve it via the [Enum.presence] extension property.
 */
@SubclassOptInRequired(InternalForSubclassingRpcApi::class)
public interface EnumPresence {
    public val hasSourceContext: Boolean
}

/**
 * Interface providing field-presence information for [Option] messages.
 * Retrieve it via the [Option.presence] extension property.
 */
@SubclassOptInRequired(InternalForSubclassingRpcApi::class)
public interface OptionPresence {
    public val hasValue: Boolean
}
