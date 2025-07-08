/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import util.KOTLINX_RPC_PREFIX
import util.other.libs
import util.setupPage

plugins {
    id("org.jetbrains.dokka")
}

dokka {
    val globalRootDir: String by project.extra

    pluginsConfiguration {
        html {
            setupPage(globalRootDir)
        }
    }

    moduleName = "$KOTLINX_RPC_PREFIX-${project.name}"

    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory = rootDir
            remoteUrl("https://github.com/Kotlin/kotlinx-rpc/blob/${libs.versions.kotlinx.rpc.get()}")
            remoteLineSuffix = "#L"

            documentedVisibilities = setOf(
                VisibilityModifier.Public,
                VisibilityModifier.Protected,
            )
        }
    }

    dokkaPublications.configureEach {
        suppressObviousFunctions = true
        failOnWarning = true
    }
}

dependencies {
    dokkaPlugin(libs.dokka.rpc.plugin)
}

val thisProject = project

rootProject.configurations.matching { it.name == "dokka" }.all {
    rootProject.dependencies.dokka(thisProject)
}
