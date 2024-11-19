/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("DuplicatedCode")

package util

import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra

val Project.spacePassword get(): String? {
    return (this.extra["spacePassword"] as String?)
}

val Project.localProperties get(): java.util.Properties {
    return (this.extra["localProperties"] as java.util.Properties)
}

val Project.useProxyRepositories get(): Boolean {
    return (this.extra["useProxyRepositories"] as Boolean)
}
