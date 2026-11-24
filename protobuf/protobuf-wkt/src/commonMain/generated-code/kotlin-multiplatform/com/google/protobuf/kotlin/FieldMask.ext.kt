@file:OptIn(InternalRpcApi::class)
@file:Suppress("unused")

package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.InternalRpcApi

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
