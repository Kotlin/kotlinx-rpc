/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// The Gradle plugin and the builds that include this build resolve the compiler plugin by this name.
rootProject.name = "kotlinx-rpc-compiler-plugin"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("../gradle-conventions")
    includeBuild("../gradle-conventions-settings")

    repositories {
        gradlePluginPortal()
        maven("https://packages.jetbrains.team/maven/p/compiler-plugin-dev-kit/eap") {
            content {
                includeGroupAndSubgroups("org.jetbrains.kotlin.compiler.plugin.devkit")
            }
        }
    }

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
    id("conventions-repositories")
    id("conventions-version-resolution")
    id("conventions-develocity")
    kotlin("compiler.plugin.devkit")
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

includeRootAsPublic()
