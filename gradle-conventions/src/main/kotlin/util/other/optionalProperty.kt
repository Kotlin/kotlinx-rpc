/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.other

import org.gradle.api.Project
import kotlin.reflect.KProperty
import kotlin.text.replaceFirstChar

class OptionalProperty(private val target: Project, private val subpaths: Array<out String>) {
    private var cachedValue: Boolean? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        return cachedValue ?: target.getValue(property.name, subpaths).also { cachedValue = it }
    }
}

fun Project.getValue(propertyName: String, subpaths: Array<out String>): Boolean {
    val subpathProperty = subpaths
        .joinToString(".", postfix = ".")
        .takeIf { it != "." && it.isNotEmpty() }
        ?: ""

    val subpathCamelCase = subpaths
        .joinToString("") { it.replaceFirstChar(Char::titlecase) }
        .replaceFirstChar { it.lowercase() }

    val name = propertyName
        .removePrefix(subpathCamelCase)
        .replaceFirstChar { it.lowercase() }

    val fullName = "kotlinx.rpc.$subpathProperty$name"

    return when {
        hasProperty(fullName) -> (properties[fullName] as? String)?.toBooleanStrictOrNull()
            ?: error("Invalid value for '$fullName' property: ${properties[fullName]}")
        else -> false
    }
}

fun Project.optionalProperty(vararg subpaths: String): OptionalProperty {
    return OptionalProperty(this, subpaths)
}

fun Project.optionalPropertyValue(name: String, vararg subpaths: String): Boolean {
    return getValue(name, subpaths)
}
