/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.enableContextParameters

plugins {
    alias(libs.plugins.conventions.devkit.compiler.plugin)
    alias(libs.plugins.conventions.no.psi.element)
}

group = "org.jetbrains.kotlinx"
version = libs.versions.kotlinx.rpc.get()

logger.lifecycle("[Compiler Plugin] kotlinx.rpc project version: $version")

pluginDevKit {
    componentRegistrar = "kotlinx.rpc.codegen.RpcCompilerPlugin"
    commandLineProcessor = "kotlinx.rpc.codegen.RpcCommandLineProcessor"
    generateTestsClass = "kotlinx.rpc.codegen.test.GenerateTestsKt"

    testDataLibraries {
        // The project is in the root build. See tests/compiler-plugin-test-libs.
        register("rpcRuntime", isTransitive = true) {
            jvm("org.jetbrains.kotlinx:compiler-plugin-test-libs:$version")
        }
    }

    versionHierarchy {
        splitDev(2, 3, 20)
        splitDev(2, 4)
        splitDev(2, 4, 20)
        splitDev(2, 5)
    }
}

enableContextParameters()
