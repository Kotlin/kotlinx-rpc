@file:OptIn(InternalRpcApi::class)
@file:Suppress("unused")

package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy()
 * ```
 */
public fun Empty.copy(body: Empty.Builder.() -> Unit = {}): Empty {
    return this.asInternal().copyInternal(body)
}
