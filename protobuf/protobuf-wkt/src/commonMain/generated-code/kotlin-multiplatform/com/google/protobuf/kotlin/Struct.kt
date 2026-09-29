@file:OptIn(InternalRpcApi::class)
@file:Suppress("ClassName")

package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.internal.GeneratedProtoMessage

/**
 * `Struct` represents a structured data value, consisting of fields
 * which map to dynamically typed values. In some languages, `Struct`
 * might be supported by a native representation. For example, in
 * scripting languages like JS a struct is represented as an
 * object. The details of that representation are described together
 * with the proto support for the language.
 * 
 * The JSON representation for `Struct` is JSON object.
 */
@GeneratedProtoMessage
public interface Struct {
    /**
     * Unordered map of dynamically typed values.
     */
    public val fields: Map<String, Value>
}

/**
 * `Value` represents a dynamically typed value which can be either
 * null, a number, a string, a boolean, a recursive struct value, or a
 * list of values. A producer of value is expected to set one of these
 * variants. Absence of any variant indicates an error.
 * 
 * The JSON representation for `Value` is JSON value.
 */
@GeneratedProtoMessage
public interface Value {
    /**
     * Represents a null value.
     */
    public val nullValue: NullValue

    /**
     * Represents a double value.
     */
    public val numberValue: Double

    /**
     * Represents a string value.
     */
    public val stringValue: String

    /**
     * Represents a boolean value.
     */
    public val boolValue: Boolean

    /**
     * Represents a structured value.
     */
    public val structValue: Struct

    /**
     * Represents a repeated `Value`.
     */
    public val listValue: ListValue
}

/**
 * `ListValue` is a wrapper around a repeated field of values.
 * 
 * The JSON representation for `ListValue` is JSON array.
 */
@GeneratedProtoMessage
public interface ListValue {
    /**
     * Repeated field of dynamically typed values.
     */
    public val values: List<Value>
}

/**
 * `NullValue` is a singleton enumeration to represent the null value for the
 * `Value` type union.
 * 
 * The JSON representation for `NullValue` is JSON `null`.
 */
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
