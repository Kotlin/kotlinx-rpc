/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import com.google.protobuf.kotlin.asInternal
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtobufConfig
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.reflect.safeCast

@InternalRpcApi
public class ExtensionDescriptor<T : Any, V : Any> private constructor(
    public val extendee: KClass<T>,
    public val valueType: KClass<V>,
    public val fieldNumber: Int,
    public val name: String,
    public val wireType: WireType,
    public val isRepeated: Boolean,
    public val isPacked: Boolean,
    public val defaultValue: V?,
    public val encode: (WireEncoder, Int, Any) -> Unit,
    public val decode: (Any?, WireDecoder) -> V
) {
    public companion object {

        public fun <T : Any> bool(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: Boolean = false,
        ): ExtensionDescriptor<T, Boolean> = ExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.VARINT,
            extendee = extendee,
            valueType = Boolean::class,
            isRepeated = false,
            isPacked = false,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeBool(fieldNr, value.encodingCast<Boolean>()) },
            decode = { _, dec -> dec.readBool() }
        )

        public fun <T : Any> int32(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: Int = 0,
        ): ExtensionDescriptor<T, Int> = ExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.VARINT,
            extendee = extendee,
            valueType = Int::class,
            isRepeated = false,
            isPacked = false,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeInt32(fieldNr, value.encodingCast<Int>()) },
            decode = { _, dec -> dec.readInt32() }
        )

        public fun <T : Any> int64(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: Long = 0L,
        ): ExtensionDescriptor<T, Long> = ExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.VARINT,
            extendee = extendee,
            valueType = Long::class,
            isRepeated = false,
            isPacked = false,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeInt64(fieldNr, value.encodingCast<Long>()) },
            decode = { _, dec -> dec.readInt64() }
        )

        public fun <T : Any> uint32(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: UInt = 0u,
        ): ExtensionDescriptor<T, UInt> = ExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.VARINT,
            extendee = extendee,
            valueType = UInt::class,
            isRepeated = false,
            isPacked = false,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeUInt32(fieldNr, value.encodingCast<UInt>()) },
            decode = { _, dec -> dec.readUInt32() }
        )

        public fun <T : Any> uint64(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: ULong = 0uL,
        ): ExtensionDescriptor<T, ULong> = ExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.VARINT,
            extendee = extendee,
            valueType = ULong::class,
            isRepeated = false,
            isPacked = false,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeUInt64(fieldNr, value.encodingCast<ULong>()) },
            decode = { _, dec -> dec.readUInt64() }
        )

        public fun <T : Any> sint32(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: Int = 0,
        ): ExtensionDescriptor<T, Int> = ExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.VARINT,
            extendee = extendee,
            valueType = Int::class,
            isRepeated = false,
            isPacked = false,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeSInt32(fieldNr, value.encodingCast<Int>()) },
            decode = { _, dec -> dec.readSInt32() }
        )

        public fun <T : Any> sint64(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: Long = 0L,
        ): ExtensionDescriptor<T, Long> = ExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.VARINT,
            extendee = extendee,
            valueType = Long::class,
            isRepeated = false,
            isPacked = false,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeSInt64(fieldNr, value.encodingCast<Long>()) },
            decode = { _, dec -> dec.readSInt64() }
        )

        public fun <T : Any> fixed32(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: UInt = 0u,
        ): ExtensionDescriptor<T, UInt> = ExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.FIXED32,
            extendee = extendee,
            valueType = UInt::class,
            isRepeated = false,
            isPacked = false,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeFixed32(fieldNr, value.encodingCast<UInt>()) },
            decode = { _, dec -> dec.readFixed32() }
        )

        public fun <T : Any> fixed64(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: ULong = 0uL,
        ): ExtensionDescriptor<T, ULong> = ExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.FIXED64,
            extendee = extendee,
            valueType = ULong::class,
            isRepeated = false,
            isPacked = false,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeFixed64(fieldNr, value.encodingCast<ULong>()) },
            decode = { _, dec -> dec.readFixed64() }
        )

        public fun <T : Any> sfixed32(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: Int = 0,
        ): ExtensionDescriptor<T, Int> = ExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.FIXED32,
            extendee = extendee,
            valueType = Int::class,
            isRepeated = false,
            isPacked = false,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeSFixed32(fieldNr, value.encodingCast<Int>()) },
            decode = { _, dec -> dec.readSFixed32() }
        )

        public fun <T : Any> sfixed64(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: Long = 0L,
        ): ExtensionDescriptor<T, Long> = ExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.FIXED64,
            extendee = extendee,
            valueType = Long::class,
            isRepeated = false,
            isPacked = false,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeSFixed64(fieldNr, value.encodingCast<Long>()) },
            decode = { _, dec -> dec.readSFixed64() }
        )

        public fun <T : Any> float(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: Float = 0f,
        ): ExtensionDescriptor<T, Float> = ExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.FIXED32,
            extendee = extendee,
            isRepeated = false,
            isPacked = false,
            valueType = Float::class,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeFloat(fieldNr, value.encodingCast<Float>()) },
            decode = { _, dec -> dec.readFloat() }
        )

        public fun <T : Any> double(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: Double = 0.0,
        ): ExtensionDescriptor<T, Double> = ExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.FIXED64,
            extendee = extendee,
            isRepeated = false,
            isPacked = false,
            valueType = Double::class,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeDouble(fieldNr, value.encodingCast<Double>()) },
            decode = { _, dec -> dec.readDouble() }
        )

        public fun <T : Any> bytes(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: ByteArray = byteArrayOf(),
        ): ExtensionDescriptor<T, ByteArray> = ExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.LENGTH_DELIMITED,
            extendee = extendee,
            valueType = ByteArray::class,
            isRepeated = false,
            isPacked = false,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeBytes(fieldNr, value.encodingCast<ByteArray>()) },
            decode = { _, dec -> dec.readBytes() }
        )

        public fun <T : Any> string(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: String = "",
        ): ExtensionDescriptor<T, String> = ExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.LENGTH_DELIMITED,
            extendee = extendee,
            valueType = String::class,
            isRepeated = false,
            isPacked = false,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeString(fieldNr, value.encodingCast<String>()) },
            decode = { _, dec -> dec.readString() }
        )

        public fun <T : Any, V : Any> enum(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            valueType: KClass<V>,
            encodeValue: (V) -> Int,
            decodeValue: (Int) -> V
        ): ExtensionDescriptor<T, V> = ExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.VARINT,
            extendee = extendee,
            valueType = valueType,
            isRepeated = false,
            isPacked = false,
            defaultValue = null,
            encode = { enc, fieldNr, value ->
                enc.writeEnum(
                    fieldNr = fieldNr,
                    value = encodeValue(value.encodingCast(valueType))
                )
            },
            decode = { _, dec -> decodeValue(dec.readEnum()) }
        )

        // TODO: Annotate V with @GeneratedProtoMessage
        public fun <T : Any, V : Any> message(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            valueType: KClass<V>,
            default: () -> V,
            asInternal: (V) -> InternalMessage,
            encodeWith: (InternalMessage, WireEncoder, ProtobufConfig?) -> Unit,
            decodeWith: (V, WireDecoder) -> Unit
        ): ExtensionDescriptor<T, V> = ExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.LENGTH_DELIMITED,
            extendee = extendee,
            valueType = valueType,
            isRepeated = false,
            isPacked = false,
            defaultValue = null,
            encode = { enc, fieldNr, value ->
                val internal = asInternal(value.encodingCast(valueType))
                enc.writeMessage(fieldNr, internal) { encodeWith(this, it, null) }
            },
            decode = { currentMessage, dec ->
                val msg = currentMessage?.let { valueType.cast(it) } ?: run { default() }
                decodeWith(msg, dec)
                return msg
            }
        )

    }
}

private inline fun <reified T : Any> Any.encodingCast(): T {
    return encodingCast(T::class)
}

private fun <T : Any> Any.encodingCast(valueType: KClass<T>): T {
    return valueType.safeCast(this) ?: throw ProtobufEncodingException(
        "Extension expected value of type ${valueType.simpleName}, got ${this::class.simpleName}"
    )
}