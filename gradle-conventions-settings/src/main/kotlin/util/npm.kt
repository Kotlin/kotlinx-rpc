/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import java.io.File

fun Project.configureNpm() {
    val executeNpmLogin by tasks.registering {
        val registryUrl = "https://packages.jetbrains.team/npm/p/krpc/build-deps/"

        // To prevent leaking of credentials in VCS on dev machine use the build directory config file
        val buildYarnConfigFile = File(project.rootDir, "build/js/.yarnrc")
        val buildNpmConfigFile = File(project.rootDir, "build/js/.npmrc")

        val spacePassword: String? = getSpacePassword()

        doLast {
            val outputYarnText = """
                registry: "$registryUrl"
            """.trimIndent()

            var outputNpmText = """
                registry="$registryUrl"
            """.trimIndent()

            if (spacePassword != null) {
                if (spacePassword.split(".").size != 3) {
                    throw GradleException("Unexpected Space Token format")
                }

                outputNpmText += System.lineSeparator() + """
                    always-auth=true
                    save-exact=true
                    ${registryUrl.removePrefix("https:")}:_authToken=$spacePassword
                """.trimIndent()
            }

            buildYarnConfigFile.createNewFile()
            buildYarnConfigFile.writeText(outputYarnText)
            buildNpmConfigFile.createNewFile()
            buildNpmConfigFile.writeText(outputNpmText)
        }

        outputs.file(buildYarnConfigFile).withPropertyName("buildOutputYarnFile")
        outputs.file(buildNpmConfigFile).withPropertyName("buildOutputNpmFile")
    }

    plugins.withType(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin::class.java).configureEach {
        rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
            download = true
            downloadBaseUrl = "https://packages.jetbrains.team/files/p/krpc/build-deps/"
        }

        tasks.named("kotlinNpmInstall").configure {
            dependsOn(executeNpmLogin)
        }
    }

    // necessary for CI js tests
    rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin> {
        rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension> {
            ignoreScripts = false
            download = true
            downloadBaseUrl = "https://packages.jetbrains.team/files/p/krpc/build-deps/"
        }
    }
}
