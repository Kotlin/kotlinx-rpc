/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.kotlin.dsl.assign
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
