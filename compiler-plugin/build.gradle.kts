/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.whenForIde

plugins {
    id("empty") // resolve conventions classpath
}

val rpcVersion: String = libs.versions.kotlinx.rpc.get()
val kotlinCompilerVersion = libs.versions.kotlin.compiler.get()
val kotlinLangVersion = libs.versions.kotlin.lang.get()

allprojects {
    group = "org.jetbrains.kotlinx"

    version = if (kotlinCompilerVersion != kotlinLangVersion) {
        "$kotlinCompilerVersion-$rpcVersion"
    } else {
        "$kotlinLangVersion-$rpcVersion"
    }
}

logger.lifecycle(
    "[Compiler Plugin] kotlinx.rpc project version: $version, " +
            "Kotlin version: $kotlinLangVersion, " +
            "Compiler version: $kotlinCompilerVersion"
)

whenForIde {
    logger.lifecycle("[Compiler Plugin] For-ide project mode enabled")
}
