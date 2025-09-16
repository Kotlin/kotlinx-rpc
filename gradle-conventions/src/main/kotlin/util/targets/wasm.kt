/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.targets

import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import util.kmp
import util.other.libs
import util.setPublicArtifactId

@OptIn(ExperimentalWasmDsl::class)
fun KmpConfig.configureWasm() {
    fun KotlinTarget.configurePublication() {
        mavenPublication {
            setPublicArtifactId(project)
        }
    }

    kmp {
        if (wasmJs) {
            wasmJs {
                configureJsAndWasmJsTasks()

                browser()
                nodejs()
                if (wasmJsD8) {
                    // this platform needs some care KRPC-210
//                    d8()
                }

                binaries.library()
            }.configurePublication()

            sourceSets {
                wasmJsMain {
                    dependencies {
                        implementation(libs.kotlinx.browser)
                    }
                }

                wasmJsTest {
                    puppeteer(libs.versions.puppeteer.get())
                }
            }
        }

        if (wasmWasi) {
            wasmWasi {
                nodejs()

                binaries.library()
            }.configurePublication()
        }
    }
}
