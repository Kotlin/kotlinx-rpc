@file:OptIn(InternalRpcApi::class)
@file:Suppress("unused")

package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * Constructs a new message.
 * ```
 * val message = Any {
 *    typeUrl = ...
 * }
 * ```
 */
public operator fun Any.Companion.invoke(body: Any.Builder.() -> Unit): Any {
    return AnyInternal().apply(body)
}

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    typeUrl = ...
 * }
 * ```
 */
public fun Any.copy(body: Any.Builder.() -> Unit = {}): Any {
    return this.asInternal().copyInternal(body)
}
