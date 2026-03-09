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

/**
 * Internal runtime descriptor for a protobuf extension field.
 */
@InternalRpcApi
public class InternalExtensionDescriptor<@GeneratedProtoMessage T : Any, V : Any> private constructor(
    /** Extendee message type used by [kotlinx.rpc.protobuf.ProtoExtensionRegistry] lookup. */
    public override val messageType: KClass<T>,
    /** Runtime value type used for safe casts from erased `Any` slots. */
    public val valueType: KClass<V>,
    /** Protobuf field number of this extension. */
    public override val fieldNumber: Int,
    /** Human-readable extension name used in debug/string rendering. */
    public val name: String,
    /** Canonical wire type for non-packed encoding. */
    public val wireType: WireType,
    /** Accepted wire types during decode (supports packed/unpacked compatibility). */
    public val acceptedWireTypes: Set<WireType> = setOf(wireType),
    /** Lazy default value returned by generated extension getters when unset. */
    public val defaultValue: Lazy<V>,
    /** Computes encoded size of the stored extension value, including the tag and potentially length */
    public val size: (fieldNumber: Int, value: Any) -> Int,
    /** Encodes the stored extension value to wire format. */
    public val encode: (encoder: WireEncoder, fieldNumber: Int, value: Any, config: ProtobufConfig?) -> Unit,
    /** Decodes one extension field occurrence from wire format.
     *
     * @param currentValue The current value of the extension, or `null` if the field is unset.
     *                     This is necessary for messages that are send in multiple batches and not at ones.
     */
    public val decode: (currentValue: Any?, decoder: WireDecoder, config: ProtobufConfig?) -> V,
    /** Optional packed decoder for repeated packable extensions. */
    public val decodePacked: ((currentValue: Any?, decoder: WireDecoder, config: ProtobufConfig?) -> V)? = null,
    /** Deep-copy strategy used when copying extension maps between messages. */
    public val copy: (value: Any) -> V,
    /** Packed payload encoder used by `packedRepeated(...)` element descriptors. */
    private val encodePacked: ((encoder: WireEncoder, fieldNumber: Int, value: List<V>) -> Unit)? = null,
    /** Packed payload decoder used by `packedRepeated(...)` element descriptors. */
    private val decodePackedValues: ((decoder: WireDecoder) -> List<V>)? = null,
): ProtoExtensionDescriptor<T, V> {
    // Packed-ness is fully determined by whether a packed decoder exists.
    public val isPacked: Boolean get() = decodePacked != null

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
            defaultValue = lazy { defaultValue },
            size = { fieldNr, value -> WireSize.tag(fieldNr, WireType.VARINT) + WireSize.bool(value.encodingCast<Boolean>()) },
            encode = { enc, fieldNr, value, _ -> enc.writeBool(fieldNr, value.encodingCast<Boolean>()) },
            decode = { _, dec, _ -> dec.readBool() },
            copy = { it as Boolean },
            encodePacked = { enc, fieldNr, value ->
                val fieldSize = value.sumOf(WireSize::bool)
                enc.writePackedBool(fieldNr, value, fieldSize)
            },
            decodePackedValues = { dec -> dec.readPackedBool() },
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
            defaultValue = lazy { defaultValue },
            size = { fieldNr, value -> WireSize.tag(fieldNr, WireType.VARINT) + WireSize.int32(value.encodingCast<Int>()) },
            encode = { enc, fieldNr, value, _ -> enc.writeInt32(fieldNr, value.encodingCast<Int>()) },
            decode = { _, dec, _ -> dec.readInt32() },
            copy = { it as Int },
            encodePacked = { enc, fieldNr, value ->
                val fieldSize = value.sumOf(WireSize::int32)
                enc.writePackedInt32(fieldNr, value, fieldSize)
            },
            decodePackedValues = { dec -> dec.readPackedInt32() },
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
            defaultValue = lazy { defaultValue },
            size = { fieldNr, value -> WireSize.tag(fieldNr, WireType.VARINT) + WireSize.int64(value.encodingCast<Long>()) },
            encode = { enc, fieldNr, value, _ -> enc.writeInt64(fieldNr, value.encodingCast<Long>()) },
            decode = { _, dec, _ -> dec.readInt64() },
            copy = { it as Long },
            encodePacked = { enc, fieldNr, value ->
                val fieldSize = value.sumOf(WireSize::int64)
                enc.writePackedInt64(fieldNr, value, fieldSize)
            },
            decodePackedValues = { dec -> dec.readPackedInt64() },
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
            defaultValue = lazy { defaultValue },
            size = { fieldNr, value -> WireSize.tag(fieldNr, WireType.VARINT) + WireSize.uInt32(value.encodingCast<UInt>()) },
            encode = { enc, fieldNr, value, _ -> enc.writeUInt32(fieldNr, value.encodingCast<UInt>()) },
            decode = { _, dec, _ -> dec.readUInt32() },
            copy = { it as UInt },
            encodePacked = { enc, fieldNr, value ->
                val fieldSize = value.sumOf(WireSize::uInt32)
                enc.writePackedUInt32(fieldNr, value, fieldSize)
            },
            decodePackedValues = { dec -> dec.readPackedUInt32() },
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
            defaultValue = lazy { defaultValue },
            size = { fieldNr, value -> WireSize.tag(fieldNr, WireType.VARINT) + WireSize.uInt64(value.encodingCast<ULong>()) },
            encode = { enc, fieldNr, value, _ -> enc.writeUInt64(fieldNr, value.encodingCast<ULong>()) },
            decode = { _, dec, _ -> dec.readUInt64() },
            copy = { it as ULong },
            encodePacked = { enc, fieldNr, value ->
                val fieldSize = value.sumOf(WireSize::uInt64)
                enc.writePackedUInt64(fieldNr, value, fieldSize)
            },
            decodePackedValues = { dec -> dec.readPackedUInt64() },
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
            defaultValue = lazy { defaultValue },
            size = { fieldNr, value -> WireSize.tag(fieldNr, WireType.VARINT) + WireSize.sInt32(value.encodingCast<Int>()) },
            encode = { enc, fieldNr, value, _ -> enc.writeSInt32(fieldNr, value.encodingCast<Int>()) },
            decode = { _, dec, _ -> dec.readSInt32() },
            copy = { it as Int },
            encodePacked = { enc, fieldNr, value ->
                val fieldSize = value.sumOf(WireSize::sInt32)
                enc.writePackedSInt32(fieldNr, value, fieldSize)
            },
            decodePackedValues = { dec -> dec.readPackedSInt32() },
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
            defaultValue = lazy { defaultValue },
            size = { fieldNr, value -> WireSize.tag(fieldNr, WireType.VARINT) + WireSize.sInt64(value.encodingCast<Long>()) },
            encode = { enc, fieldNr, value, _ -> enc.writeSInt64(fieldNr, value.encodingCast<Long>()) },
            decode = { _, dec, _ -> dec.readSInt64() },
            copy = { it as Long },
            encodePacked = { enc, fieldNr, value ->
                val fieldSize = value.sumOf(WireSize::sInt64)
                enc.writePackedSInt64(fieldNr, value, fieldSize)
            },
            decodePackedValues = { dec -> dec.readPackedSInt64() },
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
            defaultValue = lazy { defaultValue },
            size = { fieldNr, value -> WireSize.tag(fieldNr, WireType.FIXED32) + WireSize.fixed32(value.encodingCast<UInt>()) },
            encode = { enc, fieldNr, value, _ -> enc.writeFixed32(fieldNr, value.encodingCast<UInt>()) },
            decode = { _, dec, _ -> dec.readFixed32() },
            copy = { it as UInt },
            encodePacked = { enc, fieldNr, value -> enc.writePackedFixed32(fieldNr, value) },
            decodePackedValues = { dec -> dec.readPackedFixed32() },
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
            defaultValue = lazy { defaultValue },
            size = { fieldNr, value -> WireSize.tag(fieldNr, WireType.FIXED64) + WireSize.fixed64(value.encodingCast<ULong>()) },
            encode = { enc, fieldNr, value, _ -> enc.writeFixed64(fieldNr, value.encodingCast<ULong>()) },
            decode = { _, dec, _ -> dec.readFixed64() },
            copy = { it as ULong },
            encodePacked = { enc, fieldNr, value -> enc.writePackedFixed64(fieldNr, value) },
            decodePackedValues = { dec -> dec.readPackedFixed64() },
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
            defaultValue = lazy { defaultValue },
            size = { fieldNr, value -> WireSize.tag(fieldNr, WireType.FIXED32) + WireSize.sFixed32(value.encodingCast<Int>()) },
            encode = { enc, fieldNr, value, _ -> enc.writeSFixed32(fieldNr, value.encodingCast<Int>()) },
            decode = { _, dec, _ -> dec.readSFixed32() },
            copy = { it as Int },
            encodePacked = { enc, fieldNr, value -> enc.writePackedSFixed32(fieldNr, value) },
            decodePackedValues = { dec -> dec.readPackedSFixed32() },
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
            defaultValue = lazy { defaultValue },
            size = { fieldNr, value -> WireSize.tag(fieldNr, WireType.FIXED64) + WireSize.sFixed64(value.encodingCast<Long>()) },
            encode = { enc, fieldNr, value, _ -> enc.writeSFixed64(fieldNr, value.encodingCast<Long>()) },
            decode = { _, dec, _ -> dec.readSFixed64() },
            copy = { it as Long },
            encodePacked = { enc, fieldNr, value -> enc.writePackedSFixed64(fieldNr, value) },
            decodePackedValues = { dec -> dec.readPackedSFixed64() },
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
            valueType = Float::class,
            defaultValue = lazy { defaultValue },
            size = { fieldNr, value -> WireSize.tag(fieldNr, WireType.FIXED32) + WireSize.float(value.encodingCast<Float>()) },
            encode = { enc, fieldNr, value, _ -> enc.writeFloat(fieldNr, value.encodingCast<Float>()) },
            decode = { _, dec, _ -> dec.readFloat() },
            copy = { it as Float },
            encodePacked = { enc, fieldNr, value -> enc.writePackedFloat(fieldNr, value) },
            decodePackedValues = { dec -> dec.readPackedFloat() },
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
            valueType = Double::class,
            defaultValue = lazy { defaultValue },
            size = { fieldNr, value -> WireSize.tag(fieldNr, WireType.FIXED64) + WireSize.double(value.encodingCast<Double>()) },
            encode = { enc, fieldNr, value, _ -> enc.writeDouble(fieldNr, value.encodingCast<Double>()) },
            decode = { _, dec, _ -> dec.readDouble() },
            copy = { it as Double },
            encodePacked = { enc, fieldNr, value -> enc.writePackedDouble(fieldNr, value) },
            decodePackedValues = { dec -> dec.readPackedDouble() },
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
            defaultValue = lazy { defaultValue },
            size = { fieldNr, value ->
                val bytes = value.encodingCast<ByteArray>()
                WireSize.tag(fieldNr, WireType.LENGTH_DELIMITED) + WireSize.uInt32(bytes.size.toUInt()) + WireSize.bytes(bytes)
            },
            encode = { enc, fieldNr, value, _ -> enc.writeBytes(fieldNr, value.encodingCast<ByteArray>()) },
            decode = { _, dec, _ -> dec.readBytes() },
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
            defaultValue = lazy { defaultValue },
            size = { fieldNr, value ->
                val string = value.encodingCast<String>()
                val size = WireSize.string(string)
                WireSize.tag(fieldNr, WireType.LENGTH_DELIMITED) + WireSize.uInt32(size.toUInt()) + size
            },
            encode = { enc, fieldNr, value, _ -> enc.writeString(fieldNr, value.encodingCast<String>()) },
            decode = { _, dec, _ -> dec.readString() },
            copy = { it as String },
        )

        public fun <@GeneratedProtoMessage T : Any, V : Any> enum(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            valueType: KClass<V>,
            encodeValue: (V) -> Int,
            decodeValue: (Int) -> V,
            defaultValue: () -> V
        ): InternalExtensionDescriptor<T, V> = InternalExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.VARINT,
            messageType = extendee,
            valueType = valueType,
            defaultValue = lazy { defaultValue() },
            size = { fieldNr, value ->
                WireSize.tag(fieldNr, WireType.VARINT) + WireSize.enum(encodeValue(value.encodingCast(valueType)))
            },
            encode = { enc, fieldNr, value, _ ->
                enc.writeEnum(
                    fieldNr = fieldNr,
                    value = encodeValue(value.encodingCast(valueType))
                )
            },
            decode = { _, dec, _ -> decodeValue(dec.readEnum()) },
            copy = { valueType.cast(it) },
            encodePacked = { enc, fieldNr, value ->
                val fieldSize = value.sumOf { enumValue -> WireSize.enum(encodeValue(enumValue)) }
                enc.writePackedEnum(
                    fieldNr = fieldNr,
                    value = value.map(encodeValue),
                    fieldSize = fieldSize,
                )
            },
            decodePackedValues = { dec ->
                dec.readPackedEnum().map(decodeValue)
            },
        )

        public fun <@GeneratedProtoMessage T : Any, V : Any> message(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            valueType: KClass<V>,
            default: () -> V,
            asInternal: (V) -> InternalMessage,
            encodeWith: (V, WireEncoder, ProtobufConfig?) -> Unit,
            decodeWith: (V, WireDecoder, ProtobufConfig?) -> Unit
        ): InternalExtensionDescriptor<T, V> = InternalExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.LENGTH_DELIMITED,
            messageType = extendee,
            valueType = valueType,
            defaultValue = lazy { default() },
            size = { fieldNr, value ->
                val size = asInternal(value.encodingCast(valueType))._size
                WireSize.tag(fieldNr, WireType.LENGTH_DELIMITED) + WireSize.uInt32(size.toUInt()) + size
            },
            encode = { enc, fieldNr, value, config ->
                val value = value.encodingCast(valueType)
                val internal = asInternal(value)
                enc.writeMessage(fieldNr, internal) { encodeWith(value, it, config) }
            },
            decode = { currentMessage, dec, config ->
                val msg = currentMessage?.let { valueType.cast(it) } ?: run { default() }
                val internal = asInternal(msg)
                dec.readMessage(internal) { _, dec ->
                    decodeWith(msg, dec, config)
                }
                msg
            },
            copy = {
                @Suppress("UNCHECKED_CAST")
                asInternal(it.encodingCast(valueType)).copyInternal() as V
            }
        )

        public fun <@GeneratedProtoMessage T : Any, V : Any> group(
            fieldNumber: Int,
            name: String,
            extendee: KClass<T>,
            valueType: KClass<V>,
            default: () -> V,
            asInternal: (V) -> InternalMessage,
            encodeWith: (V, WireEncoder, ProtobufConfig?) -> Unit,
            decodeWith: (V, WireDecoder, ProtobufConfig?, KTag?) -> Unit,
        ): InternalExtensionDescriptor<T, V> = InternalExtensionDescriptor(
            fieldNumber = fieldNumber,
            name = name,
            wireType = WireType.START_GROUP,
            messageType = extendee,
            valueType = valueType,
            defaultValue = lazy { default() },
            size = { fieldNr, value ->
                val groupSize = asInternal(value.encodingCast(valueType))._size
                WireSize.tag(fieldNr, WireType.START_GROUP) +
                    groupSize +
                    WireSize.tag(fieldNr, WireType.END_GROUP)
            },
            encode = { enc, fieldNr, value, config ->
                val value = value.encodingCast(valueType)
                val internal = asInternal(value)
                enc.writeGroupMessage(fieldNr, internal) { encodeWith(value, it, config) }
            },
            decode = { currentMessage, dec, config ->
                val msg = currentMessage?.let { valueType.cast(it) } ?: run { default() }
                val internal = asInternal(msg)
                val startGroup = KTag(fieldNumber, WireType.START_GROUP)
                dec.readGroup(internal) { _, decoder ->
                    decodeWith(msg, decoder, config, startGroup)
                }
                msg
            },
            copy = {
                @Suppress("UNCHECKED_CAST")
                asInternal(it.encodingCast(valueType)).copyInternal() as V
            }
        )

        public fun <@GeneratedProtoMessage T : Any, E : Any> repeated(
            elementDescriptor: InternalExtensionDescriptor<T, E>,
            defaultValue: () -> List<E> = { emptyList() },
        ): InternalExtensionDescriptor<T, List<E>> {
            @Suppress("UNCHECKED_CAST")
            val listType = List::class as KClass<List<E>>

            return InternalExtensionDescriptor(
                fieldNumber = elementDescriptor.fieldNumber,
                name = elementDescriptor.name,
                wireType = elementDescriptor.wireType,
                messageType = elementDescriptor.messageType,
                valueType = listType,
                defaultValue = lazy(defaultValue),
                size = { _, value ->
                    // Non-packed repeated extensions write one field occurrence per element.
                    listType.cast(value).sumOf { rawElement ->
                        elementDescriptor.size(elementDescriptor.fieldNumber, rawElement)
                    }
                },
                encode = { enc, _, value, config ->
                    listType.cast(value).forEach { rawElement ->
                        elementDescriptor.encode(enc, elementDescriptor.fieldNumber, rawElement, config)
                    }
                },
                decode = { currentValue, dec, config ->
                    val result = currentValue?.let { listType.cast(it) as MutableList } ?: defaultValue().toMutableList()
                    result += elementDescriptor.decode(null, dec, config)
                    result
                },
                copy = { value ->
                    listType.cast(value).map { rawElement ->
                        elementDescriptor.copy(rawElement)
                    }
                },
            )
        }

        public fun <@GeneratedProtoMessage T : Any, E : Any> packedRepeated(
            elementDescriptor: InternalExtensionDescriptor<T, E>,
            defaultValue: () -> List<E> = { emptyList() },
        ): InternalExtensionDescriptor<T, List<E>> {
            val encodePacked = requireNotNull(elementDescriptor.encodePacked) {
                "Packed extension not supported for ${elementDescriptor.name}: ${elementDescriptor.valueType.simpleName}"
            }
            val decodePackedValues = requireNotNull(elementDescriptor.decodePackedValues) {
                "Packed extension not supported for ${elementDescriptor.name}: ${elementDescriptor.valueType.simpleName}"
            }

            @Suppress("UNCHECKED_CAST")
            val listType = List::class as KClass<List<E>>
            val elementTagSize = WireSize.tag(elementDescriptor.fieldNumber, elementDescriptor.wireType)

            return InternalExtensionDescriptor(
                fieldNumber = elementDescriptor.fieldNumber,
                name = elementDescriptor.name,
                wireType = WireType.LENGTH_DELIMITED,
                acceptedWireTypes = setOf(WireType.LENGTH_DELIMITED, elementDescriptor.wireType),
                messageType = elementDescriptor.messageType,
                valueType = listType,
                defaultValue = lazy(defaultValue),
                size = { _, value ->
                    val elements = listType.cast(value)
                    if (elements.isEmpty()) {
                        0
                    } else {
                        val payloadSize = elements.sumOf { rawElement ->
                            elementDescriptor.size(elementDescriptor.fieldNumber, rawElement) - elementTagSize
                        }
                        WireSize.tag(elementDescriptor.fieldNumber, WireType.LENGTH_DELIMITED) +
                            WireSize.uInt32(payloadSize.toUInt()) +
                            payloadSize
                    }
                },
                encode = { enc, _, value, _ ->
                    val elements = listType.cast(value)
                    if (elements.isNotEmpty()) {
                        encodePacked(enc, elementDescriptor.fieldNumber, elements)
                    }
                },
                decode = { currentValue, dec, config ->
                    val result = currentValue?.let { listType.cast(it) as MutableList } ?: defaultValue().toMutableList()
                    result += elementDescriptor.decode(null, dec, config)
                    result
                },
                decodePacked = { currentValue, dec, _ ->
                    val result = currentValue?.let { listType.cast(it) as MutableList } ?: defaultValue().toMutableList()
                    result += decodePackedValues(dec)
                    result
                },
                copy = { value ->
                    listType.cast(value).map { rawElement ->
                        elementDescriptor.copy(rawElement)
                    }
                },
            )
        }
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
