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
 *    kind = ...
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
 *    kind = ...
 * }
 * ```
 */
public fun Value.copy(body: Value.Builder.() -> Unit = {}): Value {
    return this.asInternal().copyInternal(body)
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
