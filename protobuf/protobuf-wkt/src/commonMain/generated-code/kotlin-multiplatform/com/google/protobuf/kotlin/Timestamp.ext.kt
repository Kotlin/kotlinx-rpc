@file:OptIn(InternalRpcApi::class)
@file:Suppress("unused")

package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * Constructs a new message.
 * ```
 * val message = Timestamp {
 *    seconds = ...
 * }
 * ```
 */
public operator fun Timestamp.Companion.invoke(body: Timestamp.Builder.() -> Unit): Timestamp {
    return TimestampInternal().apply(body)
}

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    seconds = ...
 * }
 * ```
 */
public fun Timestamp.copy(body: Timestamp.Builder.() -> Unit = {}): Timestamp {
    return this.asInternal().copyInternal(body)
}
