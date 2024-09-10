/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import java.io.File

fun Project.configureJs() {
    configureJsTasks()

    kotlin {
        sourceSets {
            jsTest {
                dependencies {
                    implementation(npm("puppeteer", "*"))
                }
            }
        }
    }
}

private fun Project.configureJsTasks() {
    kotlin {
        js(IR) {
            nodejs {
                testTask {
                    useMocha {
                        timeout = "10000"
                    }
                }
            }

            browser {
                testTask {
                    useKarma {
                        useChromeHeadless()
                        useConfigDirectory(File(project.rootProject.projectDir, "karma"))
                    }
                }
            }

            binaries.library()
        }
    }
}
