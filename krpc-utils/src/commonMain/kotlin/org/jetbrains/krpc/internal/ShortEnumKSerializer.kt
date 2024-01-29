/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.internal

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KClass

@InternalKRPCApi
abstract class ShortEnumKSerializer<E : Enum<E>>(
    kClass: KClass<E>,
    val unknownValue: E,
    val allValues: Iterable<E>,
) : KSerializer<E> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "ShortEnumKSerializer<${kClass.simpleName}>",
        kind = PrimitiveKind.SHORT, // short can hold 65536 entries, should be more than enough
    )

    override fun deserialize(decoder: Decoder): E {
        val value = decoder.decodeShort() + MODULO
        return allValues.singleOrNull { it.ordinal == value } ?: unknownValue
    }

    override fun serialize(encoder: Encoder, value: E) {
        val shortValue = (value.ordinal - MODULO).toShort()
        encoder.encodeShort(shortValue)
    }

    companion object {
        const val MODULO: Int = 32768
    }
}
