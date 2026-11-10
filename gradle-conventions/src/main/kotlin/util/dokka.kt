/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.tasks.DokkaGenerateTask
import org.jetbrains.dokka.gradle.engine.plugins.DokkaHtmlPluginParameters
import java.time.Year

fun DokkaHtmlPluginParameters.setupPage(globalRootDir: String) {
    val templatesDirectory = kotlin.io.path.Path(globalRootDir)
        .resolve("docs")
        .resolve("pages")
        .resolve("templates")
        .toFile()

    customAssets.from(
        "$globalRootDir/docs/pages/assets/logo-icon.svg",
        "$globalRootDir/docs/pages/assets/homepage.svg",
    )

    customStyleSheets.from(
        "$globalRootDir/docs/pages/assets/rpc-dev-notice.css",
    )

    footerMessage = "© ${Year.now()} JetBrains s.r.o and contributors. Apache License 2.0"
    homepageLink = "https://kotlin.github.io/kotlinx-rpc/get-started.html"

    templatesDir.set(templatesDirectory)
}

/**
 * Makes the Dokka tasks of this project fail on each warning that doesn't match one of [patterns].
 *
 * Use it only for warnings that a change in the project sources can't fix.
 * State the reason for each pattern at the call site.
 *
 * @param patterns regular expressions. A pattern must match the whole warning message.
 */
fun Project.ignoreDokkaWarnings(vararg patterns: String) {
    val ignoredPatterns = patterns.toList()

    pluginManager.withPlugin("org.jetbrains.dokka") {
        extensions.configure<DokkaExtension> {
            dokkaPublications.configureEach {
                // The check in `doLast` fails the task instead.
                failOnWarning = false
            }
        }

        tasks.withType<DokkaGenerateTask>().configureEach {
            val logFile = workerLogFile

            doLast {
                val ignored = ignoredPatterns.map(::Regex)
                val warnings = logFile.get().asFile.readLines()
                    .filter { it.startsWith(DOKKA_WARNING_PREFIX) }
                    .map { it.removePrefix(DOKKA_WARNING_PREFIX) }
                    .filter { warning -> ignored.none { it.matches(warning) } }

                if (warnings.isNotEmpty()) {
                    throw GradleException(
                        "Dokka reported ${warnings.size} warnings that aren't ignored:\n" +
                            warnings.joinToString("\n")
                    )
                }
            }
        }
    }
}

private const val DOKKA_WARNING_PREFIX = "[WARN] "
