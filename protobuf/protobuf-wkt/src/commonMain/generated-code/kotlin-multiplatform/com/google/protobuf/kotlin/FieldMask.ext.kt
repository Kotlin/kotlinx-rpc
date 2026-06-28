@file:OptIn(InternalRpcApi::class)
@file:Suppress("unused")

package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * Constructs a new message.
 * ```
 * val message = FieldMask {
 *    paths = ...
 * }
 * ```
 */
public operator fun FieldMask.Companion.invoke(body: FieldMask.Builder.() -> Unit): FieldMask {
    return FieldMaskInternal().apply(body)
}

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    paths = ...
 * }
 * ```
 */
public fun FieldMask.copy(body: FieldMask.Builder.() -> Unit = {}): FieldMask {
    return this.asInternal().copyInternal(body)
}
