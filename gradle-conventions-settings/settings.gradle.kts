/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    // Keep it in sync with libs.versions.toml
    id("com.gradle.develocity") version "3.19.2"
}

rootProject.name = "gradle-conventions-settings"

// Code below is a hack because a chicken-egg problem, I can't use myself as a settings-plugin
apply(from = "src/main/kotlin/conventions-repositories.settings.gradle.kts")
apply(from = "src/main/kotlin/conventions-version-resolution.settings.gradle.kts")

include(":develocity")

// Should be in sync with ktorbuild.develocity.settings.gradle.kts
develocity {
    server = "https://ge.jetbrains.com"
}

val isCIRun = providers.environmentVariable("TEAMCITY_VERSION").isPresent ||
        providers.environmentVariable("GITHUB_ACTIONS").isPresent

buildCache {
    if (isCIRun) {
        local {
            isEnabled = false
        }
    }

    remote(develocity.buildCache) {
        isPush = isCIRun
        isEnabled = true
    }
}
