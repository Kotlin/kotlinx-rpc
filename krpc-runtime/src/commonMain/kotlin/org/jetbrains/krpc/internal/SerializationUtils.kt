package org.jetbrains.krpc.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.*
import kotlinx.serialization.modules.SerializersModule
import org.jetbrains.krpc.RPCMessage
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

@InternalKRPCApi
fun SerializersModule.rpcSerializerForType(type: KType): KSerializer<Any?> {
    return when (type.classifier) {
        Flow::class, SharedFlow::class, StateFlow::class -> buildContextual(type)
        else -> serializer(type)
    }
}

@InternalKRPCApi
fun unsupportedSerialFormatError(serialFormat: SerialFormat): Nothing {
    error("Unsupported serial format ${serialFormat::class}, only StringFormat and BinaryFormats are supported")
}

@InternalKRPCApi
fun unexpectedDataFormatForProvidedSerialFormat(data: RPCMessage.Data, shouldBeBinary: Boolean): Nothing {
    val (expected, actual) = when {
        shouldBeBinary -> "BinaryFormat" to "string"
        else -> "StringFormat" to "binary"
    }

    error("Unexpected message format for provided serial format: " +
            "message is in $actual format (${data::class}), but provided SerialFormat is $expected")
}

@InternalKRPCApi
fun decodeMessageData(serialFormat: SerialFormat, dataSerializer: KSerializer<Any?>, data: RPCMessage.Data): Any? {
    return when (serialFormat) {
        is StringFormat -> {
            if (data !is RPCMessage.Data.StringData) {
                unexpectedDataFormatForProvidedSerialFormat(data, shouldBeBinary = false)
            }

            serialFormat.decodeFromString(dataSerializer, data.data)
        }

        is BinaryFormat -> {
            if (data !is RPCMessage.Data.BinaryData) {
                unexpectedDataFormatForProvidedSerialFormat(data, shouldBeBinary = true)
            }

            serialFormat.decodeFromByteArray(dataSerializer, data.data)
        }

        else -> {
            unsupportedSerialFormatError(serialFormat)
        }
    }
}
