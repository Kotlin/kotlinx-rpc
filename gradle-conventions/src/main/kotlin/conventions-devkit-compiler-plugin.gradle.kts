/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.compiler.plugin.devkit.DevKitCompilerConfigExtension
import util.configureRpcCompilerPlugin

plugins {
    id("conventions-common")
}

// The DevKit plugin is not in the `plugins` block, because Gradle then generates an accessor for each extension
// that exists after the plugin is applied. The DevKit plugin applies the `D8Plugin` of the Kotlin Gradle plugin,
// and `D8Plugin` registers an extension of the type `D8RootExtension`. The Kotlin Gradle plugin 2.5 deprecates
// this class with the error level, so the accessor doesn't compile.
apply(plugin = "org.jetbrains.kotlin.compiler.plugin.devkit.compiler-plugin")

configure<DevKitCompilerConfigExtension> {
    configureRpcCompilerPlugin()
}
