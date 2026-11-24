@file:OptIn(InternalRpcApi::class)
@file:Suppress("unused")

package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    value = ...
 * }
 * ```
 */
public fun DoubleValue.copy(body: DoubleValue.Builder.() -> Unit = {}): DoubleValue {
    return this.asInternal().copyInternal(body)
}

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    value = ...
 * }
 * ```
 */
public fun FloatValue.copy(body: FloatValue.Builder.() -> Unit = {}): FloatValue {
    return this.asInternal().copyInternal(body)
}

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    value = ...
 * }
 * ```
 */
public fun Int64Value.copy(body: Int64Value.Builder.() -> Unit = {}): Int64Value {
    return this.asInternal().copyInternal(body)
}

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    value = ...
 * }
 * ```
 */
public fun UInt64Value.copy(body: UInt64Value.Builder.() -> Unit = {}): UInt64Value {
    return this.asInternal().copyInternal(body)
}

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    value = ...
 * }
 * ```
 */
public fun Int32Value.copy(body: Int32Value.Builder.() -> Unit = {}): Int32Value {
    return this.asInternal().copyInternal(body)
}

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    value = ...
 * }
 * ```
 */
public fun UInt32Value.copy(body: UInt32Value.Builder.() -> Unit = {}): UInt32Value {
    return this.asInternal().copyInternal(body)
}

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    value = ...
 * }
 * ```
 */
public fun BoolValue.copy(body: BoolValue.Builder.() -> Unit = {}): BoolValue {
    return this.asInternal().copyInternal(body)
}

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    value = ...
 * }
 * ```
 */
public fun StringValue.copy(body: StringValue.Builder.() -> Unit = {}): StringValue {
    return this.asInternal().copyInternal(body)
}

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    value = ...
 * }
 * ```
 */
public fun BytesValue.copy(body: BytesValue.Builder.() -> Unit = {}): BytesValue {
    return this.asInternal().copyInternal(body)
}
