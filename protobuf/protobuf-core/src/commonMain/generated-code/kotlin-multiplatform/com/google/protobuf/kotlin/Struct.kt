@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlin.jvm.JvmInline
import kotlinx.rpc.grpc.codec.WithCodec
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.GeneratedProtoMessage

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
@WithCodec(StructInternal.CODEC::class)
public interface Struct {
    /**
    * Unordered map of dynamically typed values.
    */
    public val fields: Map<String, Value>

    public companion object
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
@WithCodec(ValueInternal.CODEC::class)
public interface Value {
    /**
    * The kind of value.
    */
    public val kind: Kind?

    /**
    * The kind of value.
    */
    public sealed interface Kind {
        /**
        * Represents a null value.
        */
        @JvmInline
        public value class NullValue(public val value: com.google.protobuf.kotlin.NullValue): Kind

        /**
        * Represents a double value.
        */
        @JvmInline
        public value class NumberValue(public val value: Double): Kind

        /**
        * Represents a string value.
        */
        @JvmInline
        public value class StringValue(public val value: String): Kind

        /**
        * Represents a boolean value.
        */
        @JvmInline
        public value class BoolValue(public val value: Boolean): Kind

        /**
        * Represents a structured value.
        */
        @JvmInline
        public value class StructValue(public val value: Struct): Kind

        /**
        * Represents a repeated `Value`.
        */
        @JvmInline
        public value class ListValue(public val value: com.google.protobuf.kotlin.ListValue): Kind
    }

    public companion object
}

/**
* `ListValue` is a wrapper around a repeated field of values.
* 
* The JSON representation for `ListValue` is JSON array.
*/
@GeneratedProtoMessage
@WithCodec(ListValueInternal.CODEC::class)
public interface ListValue {
    /**
    * Repeated field of dynamically typed values.
    */
    public val values: List<Value>

    public companion object
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
