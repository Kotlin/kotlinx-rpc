/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtoExtensionDescriptor
import kotlinx.rpc.protobuf.ProtobufConfig
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.reflect.safeCast

@InternalRpcApi
public class InternalExtensionDescriptor<@GeneratedProtoMessage T : Any, V : Any> private constructor(
    public override val messageType: KClass<T>,
    public val valueType: KClass<V>,
    public override val fieldNumber: Int,
    public val name: String,
    public val wireType: WireType,
    public val isRepeated: Boolean,
    public val isPacked: Boolean,
    public val defaultValue: V?,
    public val encode: (WireEncoder, Int, Any) -> Unit,
    public val decode: (Any?, WireDecoder) -> V,
    public val copy: (Any) -> V
): ProtoExtensionDescriptor<T, V> {
    public companion object {

        public fun <@GeneratedProtoMessage T : Any> bool(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: Boolean = false,
        ): InternalExtensionDescriptor<T, Boolean> = InternalExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.VARINT,
            messageType = extendee,
            valueType = Boolean::class,
            isRepeated = false,
            isPacked = false,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeBool(fieldNr, value.encodingCast<Boolean>()) },
            decode = { _, dec -> dec.readBool() },
            copy = { it as Boolean }
        )

        public fun <@GeneratedProtoMessage T : Any> int32(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: Int = 0,
        ): InternalExtensionDescriptor<T, Int> = InternalExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.VARINT,
            messageType = extendee,
            valueType = Int::class,
            isRepeated = false,
            isPacked = false,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeInt32(fieldNr, value.encodingCast<Int>()) },
            decode = { _, dec -> dec.readInt32() },
            copy = { it as Int },
        )

        public fun <@GeneratedProtoMessage T : Any> int64(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: Long = 0L,
        ): InternalExtensionDescriptor<T, Long> = InternalExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.VARINT,
            messageType = extendee,
            valueType = Long::class,
            isRepeated = false,
            isPacked = false,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeInt64(fieldNr, value.encodingCast<Long>()) },
            decode = { _, dec -> dec.readInt64() },
            copy = { it as Long },
        )

        public fun <@GeneratedProtoMessage T : Any> uint32(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: UInt = 0u,
        ): InternalExtensionDescriptor<T, UInt> = InternalExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.VARINT,
            messageType = extendee,
            valueType = UInt::class,
            isRepeated = false,
            isPacked = false,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeUInt32(fieldNr, value.encodingCast<UInt>()) },
            decode = { _, dec -> dec.readUInt32() },
            copy = { it as UInt },
        )

        public fun <@GeneratedProtoMessage T : Any> uint64(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: ULong = 0uL,
        ): InternalExtensionDescriptor<T, ULong> = InternalExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.VARINT,
            messageType = extendee,
            valueType = ULong::class,
            isRepeated = false,
            isPacked = false,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeUInt64(fieldNr, value.encodingCast<ULong>()) },
            decode = { _, dec -> dec.readUInt64() },
            copy = { it as ULong },
        )

        public fun <@GeneratedProtoMessage T : Any> sint32(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: Int = 0,
        ): InternalExtensionDescriptor<T, Int> = InternalExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.VARINT,
            messageType = extendee,
            valueType = Int::class,
            isRepeated = false,
            isPacked = false,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeSInt32(fieldNr, value.encodingCast<Int>()) },
            decode = { _, dec -> dec.readSInt32() },
            copy = { it as Int },
        )

        public fun <@GeneratedProtoMessage T : Any> sint64(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: Long = 0L,
        ): InternalExtensionDescriptor<T, Long> = InternalExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.VARINT,
            messageType = extendee,
            valueType = Long::class,
            isRepeated = false,
            isPacked = false,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeSInt64(fieldNr, value.encodingCast<Long>()) },
            decode = { _, dec -> dec.readSInt64() },
            copy = { it as Long },
        )

        public fun <@GeneratedProtoMessage T : Any> fixed32(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: UInt = 0u,
        ): InternalExtensionDescriptor<T, UInt> = InternalExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.FIXED32,
            messageType = extendee,
            valueType = UInt::class,
            isRepeated = false,
            isPacked = false,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeFixed32(fieldNr, value.encodingCast<UInt>()) },
            decode = { _, dec -> dec.readFixed32() },
            copy = { it as UInt },
        )

        public fun <@GeneratedProtoMessage T : Any> fixed64(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: ULong = 0uL,
        ): InternalExtensionDescriptor<T, ULong> = InternalExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.FIXED64,
            messageType = extendee,
            valueType = ULong::class,
            isRepeated = false,
            isPacked = false,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeFixed64(fieldNr, value.encodingCast<ULong>()) },
            decode = { _, dec -> dec.readFixed64() },
            copy = { it as ULong },
        )

        public fun <@GeneratedProtoMessage T : Any> sfixed32(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: Int = 0,
        ): InternalExtensionDescriptor<T, Int> = InternalExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.FIXED32,
            messageType = extendee,
            valueType = Int::class,
            isRepeated = false,
            isPacked = false,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeSFixed32(fieldNr, value.encodingCast<Int>()) },
            decode = { _, dec -> dec.readSFixed32() },
            copy = { it as Int },
        )

        public fun <@GeneratedProtoMessage T : Any> sfixed64(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: Long = 0L,
        ): InternalExtensionDescriptor<T, Long> = InternalExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.FIXED64,
            messageType = extendee,
            valueType = Long::class,
            isRepeated = false,
            isPacked = false,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeSFixed64(fieldNr, value.encodingCast<Long>()) },
            decode = { _, dec -> dec.readSFixed64() },
            copy = { it as Long },
        )

        public fun <@GeneratedProtoMessage T : Any> float(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: Float = 0f,
        ): InternalExtensionDescriptor<T, Float> = InternalExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.FIXED32,
            messageType = extendee,
            isRepeated = false,
            isPacked = false,
            valueType = Float::class,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeFloat(fieldNr, value.encodingCast<Float>()) },
            decode = { _, dec -> dec.readFloat() },
            copy = { it as Float },
        )

        public fun <@GeneratedProtoMessage T : Any> double(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: Double = 0.0,
        ): InternalExtensionDescriptor<T, Double> = InternalExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.FIXED64,
            messageType = extendee,
            isRepeated = false,
            isPacked = false,
            valueType = Double::class,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeDouble(fieldNr, value.encodingCast<Double>()) },
            decode = { _, dec -> dec.readDouble() },
            copy = { it as Double },
        )

        public fun <@GeneratedProtoMessage T : Any> bytes(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: ByteArray = byteArrayOf(),
        ): InternalExtensionDescriptor<T, ByteArray> = InternalExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.LENGTH_DELIMITED,
            messageType = extendee,
            valueType = ByteArray::class,
            isRepeated = false,
            isPacked = false,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeBytes(fieldNr, value.encodingCast<ByteArray>()) },
            decode = { _, dec -> dec.readBytes() },
            copy = { (it as ByteArray).copyOf() },
        )

        public fun <@GeneratedProtoMessage T : Any> string(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            defaultValue: String = "",
        ): InternalExtensionDescriptor<T, String> = InternalExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.LENGTH_DELIMITED,
            messageType = extendee,
            valueType = String::class,
            isRepeated = false,
            isPacked = false,
            defaultValue = defaultValue,
            encode = { enc, fieldNr, value -> enc.writeString(fieldNr, value.encodingCast<String>()) },
            decode = { _, dec -> dec.readString() },
            copy = { it as String },
        )

        public fun <@GeneratedProtoMessage T : Any, V : Any> enum(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            valueType: KClass<V>,
            encodeValue: (V) -> Int,
            decodeValue: (Int) -> V
        ): InternalExtensionDescriptor<T, V> = InternalExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.VARINT,
            messageType = extendee,
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
            decode = { _, dec -> decodeValue(dec.readEnum()) },
            copy = { valueType.cast(it) }
        )

        // TODO: Annotate V with @GeneratedProtoMessage
        public fun <@GeneratedProtoMessage T : Any, V : Any> message(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            valueType: KClass<V>,
            default: () -> V,
            asInternal: (V) -> InternalMessage,
            encodeWith: (InternalMessage, WireEncoder, ProtobufConfig?) -> Unit,
            decodeWith: (V, WireDecoder) -> Unit
        ): InternalExtensionDescriptor<T, V> = InternalExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.LENGTH_DELIMITED,
            messageType = extendee,
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
                msg
            },
            copy = {
                @Suppress("UNCHECKED_CAST")
                asInternal(it.encodingCast(valueType)).copyInternal() as V
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