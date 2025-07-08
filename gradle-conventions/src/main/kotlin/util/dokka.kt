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
        "$globalRootDir/docs/pages/assets/homepage.svg", // Doesn't work due to https://github.com/Kotlin/dokka/issues/4007
    )

    footerMessage = "© ${Year.now()} JetBrains s.r.o and contributors. Apache License 2.0"
    homepageLink = "https://kotlin.github.io/kotlinx-rpc/get-started.html"

    // replace with homepage.svg once the mentioned issue is resolved
    templatesDir.set(templatesDirectory)
}
