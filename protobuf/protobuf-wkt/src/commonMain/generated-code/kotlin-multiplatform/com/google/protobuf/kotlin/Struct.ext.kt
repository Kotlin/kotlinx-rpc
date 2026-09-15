@file:OptIn(InternalRpcApi::class)
@file:Suppress("unused")

package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * Constructs a new message.
 * ```
 * val message = Struct {
 *    fields = ...
 * }
 * ```
 */
public operator fun Struct.Companion.invoke(body: Struct.Builder.() -> Unit): Struct {
    return StructInternal().apply(body)
}

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    fields = ...
 * }
 * ```
 */
public fun Struct.copy(body: Struct.Builder.() -> Unit = {}): Struct {
    return this.asInternal().copyInternal(body)
}

/**
 * Constructs a new message.
 * ```
 * val message = Value {
 *    nullValue = ...
 * }
 * ```
 */
public operator fun Value.Companion.invoke(body: Value.Builder.() -> Unit): Value {
    return ValueInternal().apply(body)
}

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    nullValue = ...
 * }
 * ```
 */
public fun Value.copy(body: Value.Builder.() -> Unit = {}): Value {
    return this.asInternal().copyInternal(body)
}

/**
 * Returns the field-presence view for this [Value] instance.
 */
public val Value.presence: ValuePresence get() = this.asInternal()._presence

/**
 * Returns the value of the `nullValue` field if present, otherwise null.
 */
public val Value.nullValueOrNull: NullValue? get() = if (this.presence.hasNullValue) this.nullValue else null

/**
 * Returns the value of the `numberValue` field if present, otherwise null.
 */
public val Value.numberValueOrNull: Double? get() = if (this.presence.hasNumberValue) this.numberValue else null

/**
 * Returns the value of the `stringValue` field if present, otherwise null.
 */
public val Value.stringValueOrNull: String? get() = if (this.presence.hasStringValue) this.stringValue else null

/**
 * Returns the value of the `boolValue` field if present, otherwise null.
 */
public val Value.boolValueOrNull: Boolean? get() = if (this.presence.hasBoolValue) this.boolValue else null

/**
 * Returns the value of the `structValue` field if present, otherwise null.
 */
public val Value.structValueOrNull: Struct? get() = if (this.presence.hasStructValue) this.structValue else null

/**
 * Returns the value of the `listValue` field if present, otherwise null.
 */
public val Value.listValueOrNull: ListValue? get() = if (this.presence.hasListValue) this.listValue else null

/**
 * The active case of the `kind` oneof, or [ValueKindCase.NOT_SET].
 */
public val Value.kind: ValueKindCase get() = this.asInternal()._kindCase

/**
 * Clears the `kind` oneof regardless of its active case.
 */
public fun Value.Builder.clearKind() {
    this.asInternal().clearKindInternal()
}

/**
 * Exhaustive, typed dispatch on the active case of the `kind` oneof.
 */
public inline fun <R> Value.whenKind(
    nullValue: (NullValue) -> R,
    numberValue: (Double) -> R,
    stringValue: (String) -> R,
    boolValue: (Boolean) -> R,
    structValue: (Struct) -> R,
    listValue: (ListValue) -> R,
    notSet: () -> R,
): R {
    return when (this.kind) {
        ValueKindCase.NULL_VALUE -> nullValue(this.nullValue)
        ValueKindCase.NUMBER_VALUE -> numberValue(this.numberValue)
        ValueKindCase.STRING_VALUE -> stringValue(this.stringValue)
        ValueKindCase.BOOL_VALUE -> boolValue(this.boolValue)
        ValueKindCase.STRUCT_VALUE -> structValue(this.structValue)
        ValueKindCase.LIST_VALUE -> listValue(this.listValue)
        ValueKindCase.NOT_SET -> notSet()
    }
}

/**
 * Constructs a new message.
 * ```
 * val message = ListValue {
 *    values = ...
 * }
 * ```
 */
public operator fun ListValue.Companion.invoke(body: ListValue.Builder.() -> Unit): ListValue {
    return ListValueInternal().apply(body)
}

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    values = ...
 * }
 * ```
 */
public fun ListValue.copy(body: ListValue.Builder.() -> Unit = {}): ListValue {
    return this.asInternal().copyInternal(body)
}

/**
 * Interface providing field-presence information for [Value] messages.
 * Retrieve it via the [Value.presence] extension property.
 */
public interface ValuePresence {
    public val hasNullValue: Boolean

    public val hasNumberValue: Boolean

    public val hasStringValue: Boolean

    public val hasBoolValue: Boolean

    public val hasStructValue: Boolean

    public val hasListValue: Boolean
}

/**
 * Cases of the `kind` oneof of [Value].
 * Retrieve the active case via the [Value.kind] extension property.
 * The kind of value.
 */
public enum class ValueKindCase {
    /**
     * Represents a null value.
     */
    NULL_VALUE,
    /**
     * Represents a double value.
     */
    NUMBER_VALUE,
    /**
     * Represents a string value.
     */
    STRING_VALUE,
    /**
     * Represents a boolean value.
     */
    BOOL_VALUE,
    /**
     * Represents a structured value.
     */
    STRUCT_VALUE,
    /**
     * Represents a repeated `Value`.
     */
    LIST_VALUE,
    NOT_SET,
}
