/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.configureRpcCompilerPlugin

plugins {
    id("org.jetbrains.kotlin.compiler.plugin.devkit.gradle-plugin")
}

pluginDevKit {
    configureRpcCompilerPlugin()
}
