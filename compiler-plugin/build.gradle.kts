/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.otherwise
import util.whenForIde

plugins {
    alias(libs.plugins.conventions.gradle.doctor)
    id("build-util")
}

val rpcVersion: String = libs.versions.kotlinx.rpc.get()
val kotlinCompilerVersion = libs.versions.kotlin.compiler.get()
val kotlinLangVersion = libs.versions.kotlin.lang.get()

allprojects {
    group = "org.jetbrains.kotlinx"
    whenForIde {
        version = "$kotlinCompilerVersion-$rpcVersion"
    } otherwise {
        version = "$kotlinLangVersion-$rpcVersion"
    }
}
