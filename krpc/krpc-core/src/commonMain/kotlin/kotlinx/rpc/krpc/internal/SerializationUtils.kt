/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal

import kotlinx.rpc.descriptor.RpcCallable
import kotlinx.rpc.descriptor.RpcInvokator
import kotlinx.rpc.descriptor.RpcType
import kotlinx.rpc.descriptor.RpcTypeKrpc
import kotlinx.rpc.internal.rpcInternalKClass
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.serialization.*
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*
import kotlinx.serialization.modules.SerializersModule
import kotlin.reflect.KType

@OptIn(ExperimentalSerializationApi::class)
internal fun SerializersModule.buildContextualInternal(type: KType): KSerializer<Any?>? {
    val result = getContextual(
        kClass = type.rpcInternalKClass(),
        typeArgumentsSerializers = type.arguments.mapIndexed { i, typeArgument ->
            val typeArg = typeArgument.type
                ?: error("Unexpected star projection type at index $i in type arguments list of '$type'")

            buildContextualInternal(typeArg) ?: serializer(typeArg)
        }
    )

    @Suppress("UNCHECKED_CAST")
    return if (type.isMarkedNullable) result?.nullable else result as? KSerializer<Any?>
}

private fun SerializersModule.buildContextual(type: KType): KSerializer<Any?> {
    return buildContextualInternal(type) ?: serializer(type)
}

@OptIn(ExperimentalSerializationApi::class)
@InternalRpcApi
public fun SerializersModule.buildContextual(type: RpcType): KSerializer<Any?> {
    return type.annotations
        .filterIsInstance<Serializable>()
        .lastOrNull()
        ?.let {
            (type as? RpcTypeKrpc)?.serializers[it.with]
        }
        ?: buildContextual(type.kType)
}

@InternalRpcApi
public fun unsupportedSerialFormatError(serialFormat: SerialFormat): Nothing {
    error("Unsupported serial format ${serialFormat::class}, only StringFormat and BinaryFormats are supported")
}

internal fun unexpectedDataFormatForProvidedSerialFormat(
    data: KrpcCallMessage.Data,
    shouldBeBinary: Boolean,
): Nothing {
    val (expected, actual) = when {
        shouldBeBinary -> "BinaryFormat" to "string"
        else -> "StringFormat" to "binary"
    }

    error(
        "Unexpected message format for provided serial format: " +
                "message is in $actual format (${data::class}), but provided SerialFormat is $expected"
    )
}

@InternalRpcApi
public fun <T> decodeMessageData(
    serialFormat: SerialFormat,
    dataSerializer: KSerializer<T>,
    data: KrpcCallMessage.Data,
): T {
    return when (serialFormat) {
        is StringFormat -> {
            if (data !is KrpcCallMessage.Data.StringData) {
                unexpectedDataFormatForProvidedSerialFormat(data, shouldBeBinary = false)
            }

            serialFormat.decodeFromString(dataSerializer, data.data)
        }

        is BinaryFormat -> {
            if (data !is KrpcCallMessage.Data.BinaryData) {
                unexpectedDataFormatForProvidedSerialFormat(data, shouldBeBinary = true)
            }

            serialFormat.decodeFromByteArray(dataSerializer, data.data)
        }

        else -> {
            unsupportedSerialFormatError(serialFormat)
        }
    }
}

/**
 * Serializes RPC call arguments as a class-like structure keyed by parameter names.
 *
 * Optional parameters (declared with a default value):
 * - elements absent from the payload decode as [RpcInvokator.Absent], and the invokator applies the default;
 * - [RpcInvokator.Absent] elements are skipped on encode.
 *
 * Parameters unknown to the receiving side fail decoding unless the configured format
 * ignores unknown keys — adding an optional parameter is backward compatible
 * (old client, new server), not forward compatible (new client, old server).
 */
@InternalRpcApi
public class CallableParametersSerializer(
    private val callable: RpcCallable<*>,
    private val module: SerializersModule,
) : KSerializer<Array<Any?>> {
    private val callableSerializers = Array(callable.parameters.size) { i ->
        module.buildContextual(callable.parameters[i].type)
    }

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("CallableParametersSerializer") {
        for (i in callableSerializers.indices) {
            val param = callable.parameters[i]
            element(
                elementName = param.name,
                descriptor = callableSerializers[i].descriptor,
                annotations = param.type.annotations,
                isOptional = param.isOptional,
            )
        }
    }

    override fun serialize(
        encoder: Encoder,
        value: Array<Any?>,
    ) {
        encoder.encodeStructure(descriptor) {
            for (i in callable.parameters.indices) {
                if (value[i] === RpcInvokator.Absent) {
                    check(callable.parameters[i].isOptional) {
                        "Parameter '${callable.parameters[i].name}' of '${callable.name}' " +
                                "is not optional, but no argument was provided"
                    }

                    continue
                }

                encodeSerializableElement(descriptor, i, callableSerializers[i], value[i])
            }
        }
    }

    override fun deserialize(decoder: Decoder): Array<Any?> {
        return decoder.decodeStructure(descriptor) {
            // optional parameters not present in the payload stay Absent,
            // the invokator substitutes their default values
            val result = Array<Any?>(callable.parameters.size) { i ->
                if (callable.parameters[i].isOptional) RpcInvokator.Absent else null
            }

            while (true) {
                val index = decodeElementIndex(descriptor)
                if (index == CompositeDecoder.DECODE_DONE) {
                    break
                }

                if (index !in result.indices) {
                    throw SerializationException(
                        "Unexpected parameter index $index for '${callable.name}', " +
                                "expected 0..${result.lastIndex}"
                    )
                }

                result[index] = decodeSerializableElement(descriptor, index, callableSerializers[index])
            }

            result
        }
    }
}
