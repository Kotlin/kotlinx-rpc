@file:OptIn(InternalRpcApi::class)
@file:Suppress("ClassName")

package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.internal.GeneratedProtoEnum
import kotlinx.rpc.protobuf.internal.GeneratedProtoMessage

/**
 * Represents a JSON object.
 * 
 * An unordered key-value map, intending to perfectly capture the semantics of a
 * JSON object. This enables parsing any arbitrary JSON payload as a message
 * field in ProtoJSON format.
 * 
 * This follows RFC 8259 guidelines for interoperable JSON: notably this type
 * cannot represent large Int64 values or `NaN`/`Infinity` numbers,
 * since the JSON format generally does not support those values in its number
 * type.
 * 
 * If you do not intend to parse arbitrary JSON into your message, a custom
 * typed message should be preferred instead of using this type.
 */
@GeneratedProtoMessage
public interface Struct {
    /**
     * Unordered map of dynamically typed values.
     */
    public val fields: Map<String, Value>
}

/**
 * Represents a JSON value.
 * 
 * `Value` represents a dynamically typed value which can be either
 * null, a number, a string, a boolean, a recursive struct value, or a
 * list of values. A producer of value is expected to set one of these
 * variants. Absence of any variant is an invalid state.
 */
@GeneratedProtoMessage
public interface Value {
    /**
     * Represents a JSON `null`.
     */
    public val nullValue: NullValue

    /**
     * Represents a JSON number. Must not be `NaN`, `Infinity` or
     * `-Infinity`, since those are not supported in JSON. This also cannot
     * represent large Int64 values, since JSON format generally does not
     * support them in its number type.
     */
    public val numberValue: Double

    /**
     * Represents a JSON string.
     */
    public val stringValue: String

    /**
     * Represents a JSON boolean (`true` or `false` literal in JSON).
     */
    public val boolValue: Boolean

    /**
     * Represents a JSON object.
     */
    public val structValue: Struct

    /**
     * Represents a JSON array.
     */
    public val listValue: ListValue
}

/**
 * Represents a JSON array.
 */
@GeneratedProtoMessage
public interface ListValue {
    /**
     * Repeated field of dynamically typed values.
     */
    public val values: List<Value>
}

/**
 * Represents a JSON `null`.
 * 
 * `NullValue` is a sentinel, using an enum with only one value to represent
 * the null value for the `Value` type union.
 * 
 * A field of type `NullValue` with any value other than `0` is considered
 * invalid. Most ProtoJSON serializers will emit a `Value` with a `null_value`
 * set as a JSON `null` regardless of the integer value, and so will round trip
 * to a `0` value.
 */
@GeneratedProtoEnum
public sealed class NullValue(public open val number: Int) {
    /**
     * Null value.
     */
    public data object NULL_VALUE: NullValue(number = 0)

    public data class UNRECOGNIZED(override val number: Int): NullValue(number)

    public companion object {
        public val entries: List<NullValue> by lazy { listOf(NULL_VALUE) }
    }
}
