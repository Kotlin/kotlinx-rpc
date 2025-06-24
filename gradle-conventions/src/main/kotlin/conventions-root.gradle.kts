/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.other.asDokkaVersion
import util.other.libs
import util.tasks.configureNpm
import util.tasks.registerChangelogTask
import util.tasks.registerDumpPlatformTableTask
import util.tasks.registerVerifyPlatformTableTask
import java.time.Year

plugins {
    id("org.jetbrains.dokka")
}

// useful for dependencies introspection
// run ./gradlew htmlDependencyReport
// Report can normally be found in build/reports/project/dependencies/index.html
allprojects {
    plugins.apply("project-report")
}

dokka {
    val libDokkaVersion = libs.versions.kotlinx.rpc.get().asDokkaVersion()

    moduleVersion.set(libDokkaVersion)

    val pagesDirectory = layout.projectDirectory
        .dir("docs")
        .dir("pages")

    val dokkaVersionsDirectory = pagesDirectory
        .dir("api")
        .asFile

    val templatesDirectory = pagesDirectory
        .dir("templates")

    pluginsConfiguration {
        html {
            customAssets.from(
                "docs/pages/assets/logo-icon.svg",
                "docs/pages/assets/homepage.svg", // Doesn't work due to https://github.com/Kotlin/dokka/issues/4007
            )

            footerMessage = "© ${Year.now()} JetBrains s.r.o and contributors. Apache License 2.0"
            homepageLink = "https://kotlin.github.io/kotlinx-rpc/get-started.html"

            // replace with homepage.svg once the mentioned issue is resolved
            templatesDir.set(templatesDirectory)
        }
    }

    dokkaPublications.html {
        outputDirectory = dokkaVersionsDirectory
    }

    tasks.clean {
        delete(dokkaVersionsDirectory)
    }
}

dependencies {
    dokkaPlugin(libs.dokka.rpc.plugin)
}

configureNpm()

registerDumpPlatformTableTask()
registerVerifyPlatformTableTask()
registerChangelogTask()
