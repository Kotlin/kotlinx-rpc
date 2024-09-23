/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.rpc.internal.InternalRPCApi
import kotlinx.serialization.*
import kotlinx.serialization.modules.SerializersModule
import kotlin.reflect.KClass
import kotlin.reflect.KType

@OptIn(ExperimentalSerializationApi::class)
internal fun SerializersModule.buildContextual(type: KType): KSerializer<Any?> {
    val result = getContextual(
        kClass = type.classifier as? KClass<*> ?: error("Unknown type $type"),
        typeArgumentsSerializers = type.arguments.map {
            rpcSerializerForType(
                it.type ?: error("No type information for $type<$it>")
            )
        }
    )
    @Suppress("UNCHECKED_CAST")
    return result as? KSerializer<Any?> ?: error("No serializer found for $type")
}

@InternalRPCApi
public fun SerializersModule.rpcSerializerForType(type: KType): KSerializer<Any?> {
    return when (type.classifier) {
        Flow::class, SharedFlow::class, StateFlow::class -> buildContextual(type)
        else -> serializer(type)
    }
}

@InternalRPCApi
public fun unsupportedSerialFormatError(serialFormat: SerialFormat): Nothing {
    error("Unsupported serial format ${serialFormat::class}, only StringFormat and BinaryFormats are supported")
}

internal fun unexpectedDataFormatForProvidedSerialFormat(
    data: RPCCallMessage.Data,
    shouldBeBinary: Boolean,
): Nothing {
    val (expected, actual) = when {
        shouldBeBinary -> "BinaryFormat" to "string"
        else -> "StringFormat" to "binary"
    }

    error("Unexpected message format for provided serial format: " +
            "message is in $actual format (${data::class}), but provided SerialFormat is $expected")
}

@InternalRPCApi
public fun decodeMessageData(
    serialFormat: SerialFormat,
    dataSerializer: KSerializer<Any?>,
    data: RPCCallMessage.Data,
): Any? {
    return when (serialFormat) {
        is StringFormat -> {
            if (data !is RPCCallMessage.Data.StringData) {
                unexpectedDataFormatForProvidedSerialFormat(data, shouldBeBinary = false)
            }

            serialFormat.decodeFromString(dataSerializer, data.data)
        }

        is BinaryFormat -> {
            if (data !is RPCCallMessage.Data.BinaryData) {
                unexpectedDataFormatForProvidedSerialFormat(data, shouldBeBinary = true)
            }

            serialFormat.decodeFromByteArray(dataSerializer, data.data)
        }

        else -> {
            unsupportedSerialFormatError(serialFormat)
        }
    }
}
