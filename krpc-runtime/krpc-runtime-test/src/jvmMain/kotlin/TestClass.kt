package org.jetbrains.krpc.test

import kotlinx.serialization.Serializable

@Serializable
open class TestClass(val value: Int = 0) {
    override fun equals(other: Any?): Boolean {
        if (other !is TestClass) return false
        return value == other.value
    }
}

@Serializable
data class TestList<T : TestClass>(val value: Int = 42)

@Serializable
data class TestList2<out T : TestClass>(val value: Int = 42)
