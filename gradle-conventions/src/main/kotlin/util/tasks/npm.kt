/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.tasks

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootEnvSpec
import org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnPlugin
import org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnRootEnvSpec
import org.jetbrains.kotlin.gradle.targets.web.nodejs.BaseNodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.web.yarn.BaseYarnRootEnvSpec
import org.jetbrains.kotlin.gradle.targets.web.yarn.CommonYarnPlugin
import util.other.optionalProperty
import util.other.spacePassword
import util.other.useProxyRepositories
import java.io.File

private inline fun <
    reified Plugin : CommonYarnPlugin,
    reified Spec : BaseYarnRootEnvSpec,
    reified NodeJsExtension: BaseNodeJsRootExtension,
> Project.registerCustomNpmTasks(
    target: String,
    useProxy: Boolean,
) {
    val capitalizedTarget = target.replaceFirstChar { it.titlecase() }
    val login = tasks.register("execute${capitalizedTarget}NpmLogin") {
        if (!useProxyRepositories) {
            return@register
        }

        val registryUrl = "https://packages.jetbrains.team/npm/p/krpc/build-deps/"

        // To prevent leaking of credentials in VCS on dev machine use the build directory config file
        val buildYarnConfigFile = File(project.rootDir, "build/$target/.yarnrc")
        val buildNpmConfigFile = File(project.rootDir, "build/$target/.npmrc")

        val spacePassword: String? = spacePassword

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

    plugins.withType<Plugin>().configureEach {
        extensions.configure<Spec> {
            download = true

            if (useProxy) {
                downloadBaseUrl = "https://packages.jetbrains.team/files/p/krpc/build-deps/"
            }
        }

        extensions.configure<NodeJsExtension> {
            npmInstallTaskProvider.configure {
                dependsOn(login)
            }
        }
    }
}

@Suppress("UnusedReceiverParameter")
fun BaseYarnRootEnvSpec.configureNpmExtension(useProxy: Boolean, kotlinMasterBuild: Boolean) {
    ignoreScripts = false // if true - puppeteer won't install browsers

    yarnLockMismatchReport = if (useProxy && !kotlinMasterBuild) {
        YarnLockMismatchReport.FAIL
    } else {
        YarnLockMismatchReport.WARNING
    }
}

fun Project.configureNpm() {
    val kotlinMasterBuild by optionalProperty()
    val useProxy = useProxyRepositories

    registerCustomNpmTasks<YarnPlugin, YarnRootEnvSpec, NodeJsRootExtension>("js", useProxy)
    registerCustomNpmTasks<WasmYarnPlugin, WasmYarnRootEnvSpec, WasmNodeJsRootExtension>("wasm", useProxy)

    // necessary for CI js tests
    rootProject.plugins.withType<YarnPlugin> {
        rootProject.extensions.configure<YarnRootEnvSpec> {
            configureNpmExtension(useProxy, kotlinMasterBuild)
        }
    }

    rootProject.plugins.withType<WasmYarnPlugin> {
        rootProject.extensions.configure<WasmYarnRootEnvSpec> {
            configureNpmExtension(useProxy, kotlinMasterBuild)
        }
    }
}
