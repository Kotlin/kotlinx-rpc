/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

rootProject.name = "gradle-plugin"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("../gradle-conventions")
    includeBuild("../gradle-conventions-settings")

    // The repositories for the settings plugins. They follow `kotlinx.rpc.useProxyRepositories`.
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

dependencyResolutionManagement {
    // for tests
    includeBuild("../protoc-gen")
    // Gradle replaces the compiler plugin artifact with the compiler plugin build.
    // See `pluginDevKit` in build.gradle.kts. The root build includes the compiler plugin build,
    // so this build includes it only when it is a standalone build.
    if (gradle.parent == null) {
        includeBuild("../compiler-plugin")
    }
    // for Dokka
    includeBuild("../dokka-plugin")
}

plugins {
    id("conventions-repositories")
    id("conventions-version-resolution")
    kotlin("compiler.plugin.devkit")
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

includeRootAsPublic()
