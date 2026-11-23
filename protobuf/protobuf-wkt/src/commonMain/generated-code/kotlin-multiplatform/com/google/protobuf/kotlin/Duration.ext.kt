@file:OptIn(InternalRpcApi::class)
@file:Suppress("unused")

package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    seconds = ...
 * }
 * ```
 */
public fun Duration.copy(body: Duration.Builder.() -> Unit = {}): Duration {
    return this.asInternal().copyInternal(body)
}
