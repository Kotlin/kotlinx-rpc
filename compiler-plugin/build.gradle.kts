/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
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

println(
    "[Compiler Plugin] kotlinx.rpc project version: $version, " +
            "Kotlin version: $kotlinLangVersion, " +
            "Compiler version: $kotlinCompilerVersion"
)

whenForIde {
    println("For-ide project mode enabled")
}
