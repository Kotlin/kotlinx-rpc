@file:OptIn(InternalRpcApi::class)
@file:Suppress("unused")

package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.InternalForSubclassingRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    name = ...
 * }
 * ```
 */
public fun Api.copy(body: Api.Builder.() -> Unit = {}): Api {
    return this.asInternal().copyInternal(body)
}

/**
 * Returns the field-presence view for this [Api] instance.
 */
public val Api.presence: ApiPresence get() = this.asInternal()._presence

/**
 * Returns the value of the `sourceContext` field if present, otherwise null.
 */
public val Api.sourceContextOrNull: SourceContext? get() = if (this.presence.hasSourceContext) this.sourceContext else null

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    name = ...
 * }
 * ```
 */
public fun Method.copy(body: Method.Builder.() -> Unit = {}): Method {
    return this.asInternal().copyInternal(body)
}

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    name = ...
 * }
 * ```
 */
public fun Mixin.copy(body: Mixin.Builder.() -> Unit = {}): Mixin {
    return this.asInternal().copyInternal(body)
}

/**
 * Interface providing field-presence information for [Api] messages.
 * Retrieve it via the [Api.presence] extension property.
 */
@SubclassOptInRequired(InternalForSubclassingRpcApi::class)
public interface ApiPresence {
    public val hasSourceContext: Boolean
}
