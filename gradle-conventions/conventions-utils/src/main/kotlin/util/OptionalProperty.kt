/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.Project
import kotlin.reflect.KProperty

class OptionalProperty(private val target: Project) {
    operator fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return getValue("kotlinx.rpc.${property.name}")
    }

    fun getValue(propName: String): Boolean {
        return when {
            target.hasProperty(propName) -> (target.properties[propName] as String).toBoolean()
            else -> false
        }
    }
}

fun Project.optionalProperty(): OptionalProperty {
    return OptionalProperty(this)
}

fun Project.optionalProperty(name: String): Boolean {
    return OptionalProperty(this).getValue(name)
}
