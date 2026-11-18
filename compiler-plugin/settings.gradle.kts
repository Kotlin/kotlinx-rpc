/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// The Gradle plugin and the builds that include this build resolve the compiler plugin by this name.
rootProject.name = "kotlinx-rpc-compiler-plugin"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("../gradle-conventions")
    includeBuild("../gradle-conventions-settings")

    // The repositories for the settings plugins and for the projects. They follow `kotlinx.rpc.useProxyRepositories`.
    // The `conventions-repositories` plugin runs the same script, so the `plugins` block doesn't apply it.
    apply(from = "../gradle-conventions-settings/src/main/kotlin/conventions-repositories.settings.gradle.kts")

    // Gradle resolves the settings plugins before it creates the version catalog,
    // so this block reads the DevKit version from the file.
    val devkitVersion = settings.rootDir.resolve("../versions-root/libs.versions.toml").readLines()
        .single { it.startsWith("devkit = ") }
        .substringAfter('"').substringBefore('"')

    plugins {
        kotlin("compiler.plugin.devkit") version devkitVersion
    }
}

plugins {
    id("conventions-version-resolution")
    id("conventions-develocity")
    kotlin("compiler.plugin.devkit")
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

includeRootAsPublic()
