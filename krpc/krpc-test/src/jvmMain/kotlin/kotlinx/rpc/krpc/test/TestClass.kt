/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import kotlinx.serialization.Serializable

@Suppress("EqualsOrHashCode", "detekt.EqualsWithHashCodeExist")
@Serializable
open class TestClass(val value: Int = 0) {
    override fun equals(other: Any?): Boolean {
        if (other !is TestClass) return false
        return value == other.value
    }
}

@Serializable
data class TestList<@Suppress("unused") T : TestClass>(val value: Int = 42)

@Serializable
data class TestList2<@Suppress("unused") out T : TestClass>(val value: Int = 42)
