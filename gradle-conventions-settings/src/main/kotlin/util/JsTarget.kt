/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.Action
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import java.io.File

fun ProjectKotlinConfig.configureJsAndWasmJs() {
    configureJsAndWasmJsTasks()

    kotlin {
        sourceSets {
            if (js) {
                jsTest {
                    puppeteer()
                }
            }

            if (wasmJs) {
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
    }
}

private fun KotlinSourceSet.puppeteer() {
    dependencies {
        implementation(npm("puppeteer", "*"))
    }
}

private fun ProjectKotlinConfig.configureJsAndWasmJsTasks() {
    kotlin {
        jsAnsWasmJs(this@configureJsAndWasmJsTasks) {
            nodejs {
                testTask {
                    useMocha {
                        timeout = "10000"
                    }
                }
            }

            (this as KotlinJsIrTarget).whenBrowserConfigured {
                testTask {
                    useKarma {
                        useChromeHeadless()
                        useConfigDirectory(File(project.rootProject.projectDir, "karma"))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalWasmDsl::class)
private fun KotlinMultiplatformExtension.jsAnsWasmJs(
    config: ProjectKotlinConfig,
    configure: Action<KotlinJsTargetDsl>,
) {
    listOfNotNull(
        if (config.js) js(IR) else null,
        if (config.wasmJs) wasmJs() else null,
    ).forEach { configure(it) }
}
