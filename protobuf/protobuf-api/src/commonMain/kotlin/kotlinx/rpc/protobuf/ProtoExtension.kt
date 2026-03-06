/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf

import kotlinx.rpc.protobuf.internal.GeneratedProtoMessage
import kotlin.reflect.KClass

/**
 * Protobuf extension descriptor.
 *
 * This interface is not meant to be implemented by users.
 * But is used by the protoc plugin to generate extension descriptors.
 */
@SubclassOptInRequired
public interface ProtoExtensionDescriptor<@GeneratedProtoMessage T : Any, V : Any> {
    /** Message type that can be extended by this descriptor. */
    public val messageType: KClass<T>

    /** Protobuf field number of the extension. */
    public val fieldNumber: Int
}

/**
 * Creates a [ProtoExtensionRegistry] using a DSL-style builder block.
 *
 * Example:
 * ```
 * val registry = ProtoExtensionRegistry {
 *     +MyMessage.myExtension
 * }
 * ```
 */
public fun ProtoExtensionRegistry(builder: ProtoExtensionRegistry.Builder.() -> Unit): ProtoExtensionRegistry {
    return ProtoExtensionRegistry().also {
        it.newBuilder().apply(builder)
    }
}

/**
 * Runtime registry of known extension descriptors.
 *
 * The registry can be passed to a [ProtobufConfig] to enable extension parsing.
 * During decoding, the protobuf runtime looks up extension descriptors by
 * `(message type, field number)` and delegates parsing to the matching descriptor.
 *
 * A registry can be created using [ProtoExtensionRegistry].
 *
 * Example:
 * ```
 * val registry = ProtoExtensionRegistry {
 *     +MyFileExtensions.myExtension
 *     +MyFileExtensions.anotherExtension
 * }
 * val config = ProtobufConfig(extensionRegistry = registry)
 * ```
 */
public class ProtoExtensionRegistry internal constructor() {
    private val table: MutableMap<KClass<*>, MutableMap<Int, ProtoExtensionDescriptor<*, *>>> = mutableMapOf()

    /**
     * Returns all registered extensions for [messageType], keyed by field number.
     */
    public fun <@GeneratedProtoMessage T: Any> allExtensionsForMessage(messageType: KClass<T>): Map<Int, ProtoExtensionDescriptor<T, *>> {
        @Suppress ("UNCHECKED_CAST")
        return (table[messageType] ?: emptyMap()) as Map<Int, ProtoExtensionDescriptor<T, *>>
    }

    internal fun newBuilder(): Builder = Builder()

    /**
     * Mutable builder used to register descriptors into this registry.
     *
     * Example:
     * ```
     * val registry = buildProtoExtensionRegistry {
     *     +MyMessage.myExtension
     *     +MyMessage.anotherExtension
     * }
     * ```
     */
    public inner class Builder {
        /**
         * Registers a single extension descriptor.
         *
         * If another descriptor with the same `(message type, field number)` is already
         * registered, it is replaced by this descriptor.
         *
         * Example:
         * ```
         * val registry = buildProtoExtensionRegistry {
         *     register(MyMessage.myExtension)
         * }
         * ```
         */
        public fun register(descriptor: ProtoExtensionDescriptor<*, *>) {
            val messageExtensions = table.getOrPut(descriptor.messageType) { mutableMapOf() }
            messageExtensions[descriptor.fieldNumber] = descriptor
        }

        /**
         * Registers a single extension descriptor.
         *
         * If another descriptor with the same `(message type, field number)` is already
         * registered, it is replaced by this descriptor.
         *
         * Example:
         * ```
         * val registry = buildProtoExtensionRegistry {
         *     +MyMessage.myExtension
         * }
         * ```
         */
        public operator fun ProtoExtensionDescriptor<*, *>.unaryPlus() {
            register(this)
        }

        /** Registers all descriptors from another registry. */
        public fun registerAll(registry: ProtoExtensionRegistry) {
            return registry.table.flatMap { it.value.values }.forEach { register(it) }
        }
    }
}
