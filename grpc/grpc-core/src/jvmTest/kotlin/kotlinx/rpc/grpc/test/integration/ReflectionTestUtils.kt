/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import java.lang.reflect.Field

/**
 * Follows a chain of private fields via reflection, searching each class hierarchy.
 *
 * Used to assert that configuration values propagate into grpc-java internals, since there is
 * no injection seam to observe the builders directly. A grpc-java upgrade that renames one of
 * the reflected fields fails with a [NoSuchFieldException] naming the missing field.
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
