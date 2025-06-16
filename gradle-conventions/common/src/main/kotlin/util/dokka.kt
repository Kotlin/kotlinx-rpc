/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import org.jetbrains.dokka.gradle.engine.plugins.DokkaHtmlPluginParameters
import java.time.Year

fun String.asDokkaVersion() = removeSuffix("-SNAPSHOT")

fun Project.applyDokka() {
    if (!isPublicModule) {
        return
    }

    plugins.apply(libs.plugins.dokka.get().pluginId)

    configure<DokkaExtension> {
        pluginsConfiguration.apply {
            (this as ExtensionAware).extensions.configure<DokkaHtmlPluginParameters> {
                footerMessage.set("© ${Year.now()} JetBrains s.r.o and contributors. Apache License 2.0")
            }
        }

        moduleName.set("$KOTLINX_RPC_PREFIX-${project.name}")

        dokkaSourceSets.configureEach {
            sourceLink {
                localDirectory.set(rootDir)
                remoteUrl("https://github.com/Kotlin/kotlinx-rpc/blob/${libs.versions.kotlinx.rpc.get().asDokkaVersion()}")
                remoteLineSuffix.set("#L")

                documentedVisibilities.set(
                    setOf(
                        VisibilityModifier.Public,
                        VisibilityModifier.Protected,
                    )
                )
            }
        }

        dokkaPublications.configureEach {
            suppressObviousFunctions.set(true)
            failOnWarning.set(true)
        }
    }

    val thisProject = project

    rootProject.configurations.matching { it.name == "dokka" }.all {
        rootProject.dependencies.add("dokka", thisProject)
    }
}
