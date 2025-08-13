@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.TypeInternal.CODEC::class)
public interface Type { 
    public val name: String
    public val fields: List<com.google.protobuf.kotlin.Field>
    public val oneofs: List<kotlin.String>
    public val options: List<com.google.protobuf.kotlin.Option>
    public val sourceContext: com.google.protobuf.kotlin.SourceContext
    public val syntax: com.google.protobuf.kotlin.Syntax
    public val edition: String

    public companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.FieldInternal.CODEC::class)
public interface Field { 
    public val kind: com.google.protobuf.kotlin.Field.Kind
    public val cardinality: com.google.protobuf.kotlin.Field.Cardinality
    public val number: Int
    public val name: String
    public val typeUrl: String
    public val oneofIndex: Int
    public val packed: Boolean
    public val options: List<com.google.protobuf.kotlin.Option>
    public val jsonName: String
    public val defaultValue: String

    public sealed class Kind(public open val number: Int) { 
        public object TYPE_UNKNOWN: Kind(number = 0)

        public object TYPE_DOUBLE: Kind(number = 1)

        public object TYPE_FLOAT: Kind(number = 2)

        public object TYPE_INT64: Kind(number = 3)

        public object TYPE_UINT64: Kind(number = 4)

        public object TYPE_INT32: Kind(number = 5)

        public object TYPE_FIXED64: Kind(number = 6)

        public object TYPE_FIXED32: Kind(number = 7)

        public object TYPE_BOOL: Kind(number = 8)

        public object TYPE_STRING: Kind(number = 9)

        public object TYPE_GROUP: Kind(number = 10)

        public object TYPE_MESSAGE: Kind(number = 11)

        public object TYPE_BYTES: Kind(number = 12)

        public object TYPE_UINT32: Kind(number = 13)

        public object TYPE_ENUM: Kind(number = 14)

        public object TYPE_SFIXED32: Kind(number = 15)

        public object TYPE_SFIXED64: Kind(number = 16)

        public object TYPE_SINT32: Kind(number = 17)

        public object TYPE_SINT64: Kind(number = 18)

        public data class UNRECOGNIZED(override val number: Int): Kind(number)

        public companion object { 
            public val entries: List<Kind> by lazy { listOf(TYPE_UNKNOWN, TYPE_DOUBLE, TYPE_FLOAT, TYPE_INT64, TYPE_UINT64, TYPE_INT32, TYPE_FIXED64, TYPE_FIXED32, TYPE_BOOL, TYPE_STRING, TYPE_GROUP, TYPE_MESSAGE, TYPE_BYTES, TYPE_UINT32, TYPE_ENUM, TYPE_SFIXED32, TYPE_SFIXED64, TYPE_SINT32, TYPE_SINT64) }
        }
    }

    public sealed class Cardinality(public open val number: Int) { 
        public object CARDINALITY_UNKNOWN: Cardinality(number = 0)

        public object CARDINALITY_OPTIONAL: Cardinality(number = 1)

        public object CARDINALITY_REQUIRED: Cardinality(number = 2)

        public object CARDINALITY_REPEATED: Cardinality(number = 3)

        public data class UNRECOGNIZED(override val number: Int): Cardinality(number)

        public companion object { 
            public val entries: List<Cardinality> by lazy { listOf(CARDINALITY_UNKNOWN, CARDINALITY_OPTIONAL, CARDINALITY_REQUIRED, CARDINALITY_REPEATED) }
        }
    }

    public companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.EnumInternal.CODEC::class)
public interface Enum { 
    public val name: String
    public val enumvalue: List<com.google.protobuf.kotlin.EnumValue>
    public val options: List<com.google.protobuf.kotlin.Option>
    public val sourceContext: com.google.protobuf.kotlin.SourceContext
    public val syntax: com.google.protobuf.kotlin.Syntax
    public val edition: String

    public companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.EnumValueInternal.CODEC::class)
public interface EnumValue { 
    public val name: String
    public val number: Int
    public val options: List<com.google.protobuf.kotlin.Option>

    public companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.OptionInternal.CODEC::class)
public interface Option { 
    public val name: String
    public val value: com.google.protobuf.kotlin.Any

    public companion object
}

public sealed class Syntax(public open val number: Int) { 
    public object SYNTAX_PROTO2: Syntax(number = 0)

    public object SYNTAX_PROTO3: Syntax(number = 1)

    public object SYNTAX_EDITIONS: Syntax(number = 2)

    public data class UNRECOGNIZED(override val number: Int): Syntax(number)

    public companion object { 
        public val entries: List<Syntax> by lazy { listOf(SYNTAX_PROTO2, SYNTAX_PROTO3, SYNTAX_EDITIONS) }
    }
}

