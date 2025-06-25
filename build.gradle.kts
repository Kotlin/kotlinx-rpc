/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import util.asDokkaVersion
import util.configureApiValidation
import util.configureNpm
import util.configureProjectReport
import util.registerDumpPlatformTableTask
import util.libs
import util.registerVerifyPlatformTableTask
import java.time.Year

plugins {
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.kotlinx.rpc) apply false
    alias(libs.plugins.conventions.kover)
    alias(libs.plugins.conventions.gradle.doctor)
    alias(libs.plugins.dokka)
    alias(libs.plugins.atomicfu)
    id("build-util")
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

        // enable versioning for stable
//        versioning {
//            version = libDokkaVersion
//            olderVersionsDir = dokkaVersionsDirectory
//        }
    }

    dokkaPublications.html {
        outputDirectory = dokkaVersionsDirectory
    }

    tasks.clean {
        delete(dokkaVersionsDirectory)
    }

    dokkaGeneratorIsolation = ProcessIsolation {
        // Configures heap size, use if start to fail with OOM on CI
//        maxHeapSize = "4g"
    }
}

dependencies {
    dokkaPlugin(libs.dokka.rpc.plugin)
}

configureProjectReport()
configureNpm()
configureApiValidation()

registerDumpPlatformTableTask()
registerVerifyPlatformTableTask()

val kotlinVersion = rootProject.libs.versions.kotlin.lang.get()
val kotlinCompiler = rootProject.libs.versions.kotlin.compiler.get()

allprojects {
    group = "org.jetbrains.kotlinx"
    version = rootProject.libs.versions.kotlinx.rpc.get()
}

println("[Core] kotlinx.rpc project version: $version, Kotlin version: $kotlinVersion, Compiler: $kotlinCompiler")

// If the prefix of the kPRC version is not Kotlin gradle plugin version – you have a problem :)
// Probably some dependency brings kotlin with the later version.
// To mitigate so, refer to `versions-root/kotlin-version-lookup.json`
// and its usage in `gradle-conventions-settings/src/main/kotlin/conventions-version-resolution.settings.gradle.kts`
val kotlinGPVersion = getKotlinPluginVersion()
if (kotlinVersion != kotlinGPVersion) {
    error("KGP version mismatch. Project version: $kotlinVersion, KGP version: $kotlinGPVersion")
}
