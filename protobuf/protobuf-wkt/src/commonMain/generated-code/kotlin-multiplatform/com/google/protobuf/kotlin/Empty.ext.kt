@file:OptIn(InternalRpcApi::class)
@file:Suppress("unused")

package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * Constructs a new message.
 * ```
 * val message = Empty { }
 * ```
 */
public operator fun Empty.Companion.invoke(body: Empty.Builder.() -> Unit): Empty {
    return EmptyInternal().apply(body)
}

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy()
 * ```
 */
public fun Empty.copy(body: Empty.Builder.() -> Unit = {}): Empty {
    return this.asInternal().copyInternal(body)
}
