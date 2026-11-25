@file:OptIn(InternalRpcApi::class)
@file:Suppress("unused")

package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.InternalRpcApi

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
