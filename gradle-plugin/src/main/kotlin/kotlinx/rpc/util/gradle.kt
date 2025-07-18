/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.util

import org.gradle.api.plugins.ExtensionAware

internal inline fun <reified T : Any, Container : ExtensionAware> Container.findOrCreate(name: String, noinline create: Container.() -> T): T =
    findOrCreate(name, T::class.java, create)

@Suppress("UNCHECKED_CAST")
private fun <T : Any, Container : ExtensionAware> Container.findOrCreate(name: String, typeClass: Class<T>, create: Container.() -> T): T =
    extensions.findByName(name)?.let {
        it as? T
            ?: error("Extension $name is already present, but of the wrong type: ${it::class} instead of $typeClass")
    } ?: create()
