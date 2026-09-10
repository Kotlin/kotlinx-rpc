/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.other.generateSource

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
    alias(libs.plugins.atomicfu)
}

kotlin {
    compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")

    sourceSets {
        commonMain {
            dependencies {
                api(projects.utils)
                api(libs.coroutines.core)

                // TODO Remove after KRPC-178
                implementation(libs.serialization.core)

                implementation(libs.kotlin.reflect)
            }
        }

        jsMain {
            dependencies {
                implementation(libs.kotlin.js.wrappers)
            }
        }

        wasmJsMain {
            dependencies {
                implementation(libs.kotlinx.browser)
            }
        }
    }
}

generateSource(
    name = "CoreVersion",
    text = """
        package kotlinx.rpc.internal

        import kotlinx.rpc.internal.utils.InternalRpcApi

        @InternalRpcApi
        public const val KOTLINX_RPC_VERSION: String = "$version"
    """.trimIndent(),
    chooseSourceSet = { named("commonMain") },
)
