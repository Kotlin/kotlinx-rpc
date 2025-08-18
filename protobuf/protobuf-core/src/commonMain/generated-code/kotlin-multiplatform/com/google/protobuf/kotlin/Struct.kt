@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlin.jvm.JvmInline
import kotlinx.rpc.internal.utils.*

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.StructInternal.CODEC::class)
public interface Struct { 
    public val fields: Map<kotlin.String, com.google.protobuf.kotlin.Value>

    public companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.ValueInternal.CODEC::class)
public interface Value { 
    public val kind: com.google.protobuf.kotlin.Value.Kind?

    public sealed interface Kind { 
        @JvmInline
        public value class NullValue(
            public val value: com.google.protobuf.kotlin.NullValue,
        ): Kind

        @JvmInline
        public value class NumberValue(public val value: Double): Kind

        @JvmInline
        public value class StringValue(public val value: String): Kind

        @JvmInline
        public value class BoolValue(public val value: Boolean): Kind

        @JvmInline
        public value class StructValue(
            public val value: com.google.protobuf.kotlin.Struct,
        ): Kind

        @JvmInline
        public value class ListValue(
            public val value: com.google.protobuf.kotlin.ListValue,
        ): Kind
    }

    public companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.ListValueInternal.CODEC::class)
public interface ListValue { 
    public val values: List<com.google.protobuf.kotlin.Value>

    public companion object
}

public sealed class NullValue(public open val number: Int) { 
    public object NULL_VALUE: NullValue(number = 0)

    public data class UNRECOGNIZED(override val number: Int): NullValue(number)

    public companion object { 
        public val entries: List<NullValue> by lazy { listOf(NULL_VALUE) }
    }
}

