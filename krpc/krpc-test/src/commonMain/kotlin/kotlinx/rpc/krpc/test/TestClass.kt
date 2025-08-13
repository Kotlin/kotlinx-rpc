/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Suppress("EqualsOrHashCode", "detekt.EqualsWithHashCodeExist")
@Serializable
open class TestClass(val value: Int = 0) {
    override fun equals(other: Any?): Boolean {
        if (other !is TestClass) return false
        return value == other.value
    }
}

@Suppress("EqualsOrHashCode", "detekt.EqualsWithHashCodeExist")
@Serializable(with = TestClassThatThrowsWhileDeserialization.Serializer::class)
class TestClassThatThrowsWhileDeserialization(val value: Int = 0) {
    object Serializer : KSerializer<TestClassThatThrowsWhileDeserialization> {
        override val descriptor = Int.serializer().descriptor

        override fun serialize(encoder: Encoder, value: TestClassThatThrowsWhileDeserialization) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): TestClassThatThrowsWhileDeserialization {
            throw SerializationException("Its TestClassThatThrowsWhileDeserialization")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is TestClassThatThrowsWhileDeserialization) return false
        return value == other.value
    }
}

@Serializable
data class TestList<@Suppress("unused") T : TestClass>(val value: Int = 42)

@Serializable
data class TestList2<@Suppress("unused") out T : TestClass>(val value: Int = 42)
