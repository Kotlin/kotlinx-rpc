@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*

/**
* A protocol buffer message type.
*/
@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.TypeInternal.CODEC::class)
public interface Type { 
    /**
    * The fully qualified message name.
    */
    public val name: String
    /**
    * The list of fields.
    */
    public val fields: List<com.google.protobuf.kotlin.Field>
    /**
    * The list of types appearing in `oneof` definitions in this type.
    */
    public val oneofs: List<kotlin.String>
    /**
    * The protocol buffer options.
    */
    public val options: List<com.google.protobuf.kotlin.Option>
    /**
    * The source context.
    */
    public val sourceContext: com.google.protobuf.kotlin.SourceContext
    /**
    * The source syntax.
    */
    public val syntax: com.google.protobuf.kotlin.Syntax
    /**
    * The source edition string, only valid when syntax is SYNTAX_EDITIONS.
    */
    public val edition: String

    /**
    * Copies the original message, including unknown fields.
    */
    public fun copy(body: com.google.protobuf.kotlin.TypeInternal.() -> Unit = {}): com.google.protobuf.kotlin.Type

    public companion object
}

/**
* A single field of a message type.
*/
@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.FieldInternal.CODEC::class)
public interface Field { 
    /**
    * The field type.
    */
    public val kind: com.google.protobuf.kotlin.Field.Kind
    /**
    * The field cardinality.
    */
    public val cardinality: com.google.protobuf.kotlin.Field.Cardinality
    /**
    * The field number.
    */
    public val number: Int
    /**
    * The field name.
    */
    public val name: String
    /**
    * The field type URL, without the scheme, for message or enumeration
    * types. Example: `"type.googleapis.com/google.protobuf.Timestamp"`.
    */
    public val typeUrl: String
    /**
    * The index of the field type in `Type.oneofs`, for message or enumeration
    * types. The first type has index 1; zero means the type is not in the list.
    */
    public val oneofIndex: Int
    /**
    * Whether to use alternative packed wire representation.
    */
    public val packed: Boolean
    /**
    * The protocol buffer options.
    */
    public val options: List<com.google.protobuf.kotlin.Option>
    /**
    * The field JSON name.
    */
    public val jsonName: String
    /**
    * The string value of the default value of this field. Proto2 syntax only.
    */
    public val defaultValue: String

    /**
    * Copies the original message, including unknown fields.
    */
    public fun copy(body: com.google.protobuf.kotlin.FieldInternal.() -> Unit = {}): com.google.protobuf.kotlin.Field

    /**
    * Basic field types.
    */
    public sealed class Kind(public open val number: Int) { 
        /**
        * Field type unknown.
        */
        public data object TYPE_UNKNOWN: Kind(number = 0)

        /**
        * Field type double.
        */
        public data object TYPE_DOUBLE: Kind(number = 1)

        /**
        * Field type float.
        */
        public data object TYPE_FLOAT: Kind(number = 2)

        /**
        * Field type int64.
        */
        public data object TYPE_INT64: Kind(number = 3)

        /**
        * Field type uint64.
        */
        public data object TYPE_UINT64: Kind(number = 4)

        /**
        * Field type int32.
        */
        public data object TYPE_INT32: Kind(number = 5)

        /**
        * Field type fixed64.
        */
        public data object TYPE_FIXED64: Kind(number = 6)

        /**
        * Field type fixed32.
        */
        public data object TYPE_FIXED32: Kind(number = 7)

        /**
        * Field type bool.
        */
        public data object TYPE_BOOL: Kind(number = 8)

        /**
        * Field type string.
        */
        public data object TYPE_STRING: Kind(number = 9)

        /**
        * Field type group. Proto2 syntax only, and deprecated.
        */
        public data object TYPE_GROUP: Kind(number = 10)

        /**
        * Field type message.
        */
        public data object TYPE_MESSAGE: Kind(number = 11)

        /**
        * Field type bytes.
        */
        public data object TYPE_BYTES: Kind(number = 12)

        /**
        * Field type uint32.
        */
        public data object TYPE_UINT32: Kind(number = 13)

        /**
        * Field type enum.
        */
        public data object TYPE_ENUM: Kind(number = 14)

        /**
        * Field type sfixed32.
        */
        public data object TYPE_SFIXED32: Kind(number = 15)

        /**
        * Field type sfixed64.
        */
        public data object TYPE_SFIXED64: Kind(number = 16)

        /**
        * Field type sint32.
        */
        public data object TYPE_SINT32: Kind(number = 17)

        /**
        * Field type sint64.
        */
        public data object TYPE_SINT64: Kind(number = 18)

        public data class UNRECOGNIZED(override val number: Int): Kind(number)

        public companion object { 
            public val entries: List<Kind> by lazy { listOf(TYPE_UNKNOWN, TYPE_DOUBLE, TYPE_FLOAT, TYPE_INT64, TYPE_UINT64, TYPE_INT32, TYPE_FIXED64, TYPE_FIXED32, TYPE_BOOL, TYPE_STRING, TYPE_GROUP, TYPE_MESSAGE, TYPE_BYTES, TYPE_UINT32, TYPE_ENUM, TYPE_SFIXED32, TYPE_SFIXED64, TYPE_SINT32, TYPE_SINT64) }
        }
    }

    /**
    * Whether a field is optional, required, or repeated.
    */
    public sealed class Cardinality(public open val number: Int) { 
        /**
        * For fields with unknown cardinality.
        */
        public data object CARDINALITY_UNKNOWN: Cardinality(number = 0)

        /**
        * For optional fields.
        */
        public data object CARDINALITY_OPTIONAL: Cardinality(number = 1)

        /**
        * For required fields. Proto2 syntax only.
        */
        public data object CARDINALITY_REQUIRED: Cardinality(number = 2)

        /**
        * For repeated fields.
        */
        public data object CARDINALITY_REPEATED: Cardinality(number = 3)

        public data class UNRECOGNIZED(override val number: Int): Cardinality(number)

        public companion object { 
            public val entries: List<Cardinality> by lazy { listOf(CARDINALITY_UNKNOWN, CARDINALITY_OPTIONAL, CARDINALITY_REQUIRED, CARDINALITY_REPEATED) }
        }
    }

    public companion object
}

/**
* Enum type definition.
*/
@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.EnumInternal.CODEC::class)
public interface Enum { 
    /**
    * Enum type name.
    */
    public val name: String
    /**
    * Enum value definitions.
    */
    public val enumvalue: List<com.google.protobuf.kotlin.EnumValue>
    /**
    * Protocol buffer options.
    */
    public val options: List<com.google.protobuf.kotlin.Option>
    /**
    * The source context.
    */
    public val sourceContext: com.google.protobuf.kotlin.SourceContext
    /**
    * The source syntax.
    */
    public val syntax: com.google.protobuf.kotlin.Syntax
    /**
    * The source edition string, only valid when syntax is SYNTAX_EDITIONS.
    */
    public val edition: String

    /**
    * Copies the original message, including unknown fields.
    */
    public fun copy(body: com.google.protobuf.kotlin.EnumInternal.() -> Unit = {}): com.google.protobuf.kotlin.Enum

    public companion object
}

/**
* Enum value definition.
*/
@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.EnumValueInternal.CODEC::class)
public interface EnumValue { 
    /**
    * Enum value name.
    */
    public val name: String
    /**
    * Enum value number.
    */
    public val number: Int
    /**
    * Protocol buffer options.
    */
    public val options: List<com.google.protobuf.kotlin.Option>

    /**
    * Copies the original message, including unknown fields.
    */
    public fun copy(body: com.google.protobuf.kotlin.EnumValueInternal.() -> Unit = {}): com.google.protobuf.kotlin.EnumValue

    public companion object
}

/**
* A protocol buffer option, which can be attached to a message, field,
* enumeration, etc.
*/
@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.OptionInternal.CODEC::class)
public interface Option { 
    /**
    * The option's name. For protobuf built-in options (options defined in
    * descriptor.proto), this is the short name. For example, `"map_entry"`.
    * For custom options, it should be the fully-qualified name. For example,
    * `"google.api.http"`.
    */
    public val name: String
    /**
    * The option's value packed in an Any message. If the value is a primitive,
    * the corresponding wrapper type defined in google/protobuf/wrappers.proto
    * should be used. If the value is an enum, it should be stored as an int32
    * value using the google.protobuf.Int32Value type.
    */
    public val value: com.google.protobuf.kotlin.Any

    /**
    * Copies the original message, including unknown fields.
    */
    public fun copy(body: com.google.protobuf.kotlin.OptionInternal.() -> Unit = {}): com.google.protobuf.kotlin.Option

    public companion object
}

/**
* The syntax in which a protocol buffer element is defined.
*/
public sealed class Syntax(public open val number: Int) { 
    /**
    * Syntax `proto2`.
    */
    public data object SYNTAX_PROTO2: Syntax(number = 0)

    /**
    * Syntax `proto3`.
    */
    public data object SYNTAX_PROTO3: Syntax(number = 1)

    /**
    * Syntax `editions`.
    */
    public data object SYNTAX_EDITIONS: Syntax(number = 2)

    public data class UNRECOGNIZED(override val number: Int): Syntax(number)

    public companion object { 
        public val entries: List<Syntax> by lazy { listOf(SYNTAX_PROTO2, SYNTAX_PROTO3, SYNTAX_EDITIONS) }
    }
}
