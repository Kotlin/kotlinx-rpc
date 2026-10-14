/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import java.lang.reflect.Field

/**
 * Reads a chain of private fields by reflection. For each name, it searches the class hierarchy
 * of the current object.
 *
 * Tests use it to check that configuration values reach grpc-java internals, because the builders
 * have no other way to read them. A grpc-java upgrade that renames a field makes the call throw
 * [NoSuchFieldException] with the name of the field.
 */
internal inline fun <reified R> Any.getField(vararg names: String): R {
    var current: Any = this
    for (name in names) {
        val field = findFieldInHierarchy(current::class.java, name)
            ?: throw NoSuchFieldException("Field '$name' not found in ${current::class.java}")
        field.isAccessible = true
        current = field.get(current) as Any
    }
    return current as R
}

internal fun findFieldInHierarchy(type: Class<*>, name: String): Field? {
    var current: Class<*>? = type
    while (current != null) {
        try {
            return current.getDeclaredField(name)
        } catch (_: NoSuchFieldException) {
            current = current.superclass
        }
    }
    return null
}
