/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

@OptIn(ExperimentalWasmDsl::class)
fun ProjectKotlinConfig.configureWasm() {
    fun KotlinTarget.configurePublication() {
        mavenPublication {
            setPublicArtifactId(project)
        }
    }

    kotlin {
        if (wasmJs) {
            wasmJs {
                configureJsAndWasmJsTasks()

                browser()
                nodejs()
                d8()

                binaries.library()
            }.configurePublication()

            sourceSets {
                wasmJsMain {
                    dependencies {
                        implementation(libs.kotlinx.browser)
                    }
                }

                wasmJsTest {
                    puppeteer()
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
