/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("DuplicatedCode")

package util.other

import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import java.util.Properties

val Project.spacePassword get(): String? {
    return (this.extra["spacePassword"] as String?)
}

val Project.localProperties get(): Properties {
    return (this.extra["localProperties"] as Properties)
}

val Project.useProxyRepositories get(): Boolean {
    return (this.extra["useProxyRepositories"] as Boolean)
}

fun Project.getSensitiveProperty(name: String?): String? {
    if (name == null) {
        error("Expected not null property 'name' for publication repository config")
    }

    return project.findProperty(name) as? String
        ?: System.getenv(name)
        ?: System.getProperty(name)
}
