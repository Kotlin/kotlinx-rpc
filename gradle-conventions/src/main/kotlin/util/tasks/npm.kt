/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.tasks

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.npm.BaseNpmExtension
import org.jetbrains.kotlin.gradle.targets.js.npm.LockFileMismatchReport
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmExtension
import org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.wasm.npm.WasmNpmExtension
import org.jetbrains.kotlin.gradle.targets.web.nodejs.BaseNodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.web.nodejs.BaseNodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.web.nodejs.CommonNodeJsRootPlugin
import util.other.optionalProperty
import util.other.spacePassword
import util.other.useProxyRepositories
import java.io.File

const val PUPPETEER_BROWSERS_DIR = ".puppeteer"

private inline fun <
    reified Plugin : CommonNodeJsRootPlugin,
    reified Spec : BaseNodeJsEnvSpec,
    reified RootExtension : BaseNodeJsRootExtension,
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

            val nodeExecutable = executable

            extensions.configure<RootExtension> {
                /**
                 * Long story short:
                 * We can use Yarn because of this: https://youtrack.jetbrains.com/issue/KT-78504
                 * So: `kotlin.js.yarn=false`
                 *
                 * When we use `npm` instead, it runs `npm install` task.
                 * That should install `puppeteer` package, which should run its script `install.mjs`.
                 * That script installs browsers for tests.
                 *
                 * If we pass `--ignore-scripts` to npm, the script won't be executed.
                 * KGP does it by default.
                 * So we set `ignoreScripts = false`.
                 * We set it for `NpmExtension` and `WasmNpmExtension` in `NodeJsRootPlugin` and `WasmNodeJsRootPlugin`
                 * respectively (and not their common supertype because it will not work)
                 *
                 * And this approach worked for Yarn.
                 * Script was executed, browsers were installed.
                 *
                 * For some reason, for `npm` it didn't work.
                 * Even with a proper flag (which I checked with a --debug flag).
                 *
                 * So we need to run the script manually AFTER `kotlinNpmInstall` (or `kotlinWasmNpmInstall`).
                 * But also, BEFORE every other action that comes after `kotlinNpmInstall` (or `kotlinWasmNpmInstall`),
                 * as otherwise there will be race in parallel tasks execution.
                 *
                 * Hence, all shenanigans.
                 */
                val puppeteerInstall = tasks.register<Exec>("puppeteerInstall$capitalizedTarget") {
                    commandLine(nodeExecutable.get(), "build/$target/node_modules/puppeteer/install.mjs")
                    workingDir = rootProject.projectDir

                    // keep in sync with <ROOT>/.puppeteer.cjs
                    outputs.dir(rootProject.projectDir.resolve(PUPPETEER_BROWSERS_DIR).resolve("browsers"))
                }

                npmInstallTaskProvider.configure {
                    dependsOn(login)
                    finalizedBy(puppeteerInstall)
                }

                tasks.matching { task ->
                    task.dependsOn.any { dependency ->
                        when (dependency) {
                            is Task -> dependency == npmInstallTaskProvider.get()
                            is TaskProvider<*> -> dependency == npmInstallTaskProvider
                            is String -> dependency == npmInstallTaskProvider.name
                            else -> false
                        }
                    }
                }.configureEach {
                    dependsOn(puppeteerInstall)
                }
            }
        }
    }
}

@Suppress("UnusedReceiverParameter")
fun BaseNpmExtension.configureNpmExtension(useProxy: Boolean, kotlinMasterBuild: Boolean) {
    // todo it still doesn't work for an unknown reason, see 'puppeteerInstall*' tasks above
    ignoreScripts = false // if true - puppeteer won't install browsers

    packageLockMismatchReport = if (useProxy && !kotlinMasterBuild) {
        LockFileMismatchReport.FAIL
    } else {
        LockFileMismatchReport.WARNING
    }
}

fun Project.configureNpm() {
    val kotlinMasterBuild by optionalProperty()
    val useProxy = useProxyRepositories

    registerCustomNpmTasks<NodeJsRootPlugin, NodeJsEnvSpec, NodeJsRootExtension>("js", useProxy)
    registerCustomNpmTasks<WasmNodeJsRootPlugin, WasmNodeJsEnvSpec, WasmNodeJsRootExtension>("wasm", useProxy)

    // necessary for CI js tests
    rootProject.plugins.withType<NodeJsRootPlugin> {
        rootProject.extensions.configure<NpmExtension> {
            configureNpmExtension(useProxy, kotlinMasterBuild)
        }
    }

    rootProject.plugins.withType<WasmNodeJsRootPlugin> {
        rootProject.extensions.configure<WasmNpmExtension> {
            configureNpmExtension(useProxy, kotlinMasterBuild)
        }
    }

    tasks.named<Delete>("clean") {
        delete(project.layout.projectDirectory.dir(PUPPETEER_BROWSERS_DIR))
    }
}
