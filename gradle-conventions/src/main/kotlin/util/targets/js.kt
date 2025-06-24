/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.targets

import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import util.kmp
import util.other.libs
import java.io.File

fun KmpConfig.configureJs() {
    if (!js) {
        return
    }

    kmp {
        js(IR) {
            nodejs {
                testTask {
                    useMocha {
                        timeout = "100s"
                    }
                }
            }

            configureJsAndWasmJsTasks()
        }

        sourceSets {
            jsTest {
                puppeteer(libs.versions.puppeteer.get())
            }
        }
    }
}

fun KotlinSourceSet.puppeteer(version: String) {
    dependencies {
        implementation(npm("puppeteer", version))
    }
}

fun KotlinJsTargetDsl.configureJsAndWasmJsTasks() {
    (this as KotlinJsIrTarget).whenBrowserConfigured {
        testTask {
            useKarma {
                useChromeHeadless()
                useConfigDirectory(File(project.rootProject.projectDir, "karma"))
            }
        }
    }
}
