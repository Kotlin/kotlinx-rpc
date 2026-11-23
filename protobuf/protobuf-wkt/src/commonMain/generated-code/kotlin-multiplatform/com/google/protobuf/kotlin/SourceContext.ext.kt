@file:OptIn(InternalRpcApi::class)
@file:Suppress("unused")

package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    fileName = ...
 * }
 * ```
 */
public fun SourceContext.copy(body: SourceContext.Builder.() -> Unit = {}): SourceContext {
    return this.asInternal().copyInternal(body)
}
