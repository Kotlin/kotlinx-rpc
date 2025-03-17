/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import java.io.File

fun ProjectKotlinConfig.configureJs() {
    if (!js) {
        return
    }

    kmp {
        js(IR) {
            configureJsAndWasmJsTasks()
        }

        sourceSets {
            val jsTest by getting {
                puppeteer()
            }
        }
    }
}

fun KotlinSourceSet.puppeteer() {
    dependencies {
        implementation(npm("puppeteer", "*"))
    }
}

fun KotlinJsTargetDsl.configureJsAndWasmJsTasks() {
    nodejs {
        testTask {
            useMocha {
                timeout = "100s"
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
