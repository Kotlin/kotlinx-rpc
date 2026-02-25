/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf

import kotlinx.rpc.protobuf.internal.GeneratedProtoMessage
import kotlin.reflect.KClass

@SubclassOptInRequired
public interface ProtoExtensionDescriptor<@GeneratedProtoMessage T : Any, V : Any> {
    public val messageType: KClass<T>
    public val fieldNumber: Int
}

public fun buildProtoExtensionRegistry(builder: ProtoExtensionRegistry.Builder.() -> Unit): ProtoExtensionRegistry {
    return ProtoExtensionRegistry().also {
        it.newBuilder().apply(builder)
    }
}

public class ProtoExtensionRegistry internal constructor() {
    private val table: MutableMap<KClass<*>, MutableMap<Int, ProtoExtensionDescriptor<*, *>>> = mutableMapOf()

    public fun <@GeneratedProtoMessage T: Any> allExtensionsForMessage(messageType: KClass<T>): Map<Int, ProtoExtensionDescriptor<T, *>> {
        @Suppress ("UNCHECKED_CAST")
        return (table[messageType] ?: emptyMap()) as Map<Int, ProtoExtensionDescriptor<T, *>>
    }

    internal fun newBuilder(): Builder = Builder()
    public inner class Builder {
        public fun register(descriptor: ProtoExtensionDescriptor<*, *>) {
            val messageExtensions = table.getOrPut(descriptor.messageType) { mutableMapOf() }
            messageExtensions[descriptor.fieldNumber] = descriptor
        }

        public operator fun ProtoExtensionDescriptor<*, *>.unaryPlus() {
            register(this)
        }

        public fun registerAll(registry: ProtoExtensionRegistry) {
            return registry.table.flatMap { it.value.values }.forEach { register(it) }
        }
    }
}
