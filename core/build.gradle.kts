/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.applyAtomicfuPlugin

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
}

applyAtomicfuPlugin()

kotlin {
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
