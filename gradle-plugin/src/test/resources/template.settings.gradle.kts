/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

rootProject.name = "<test-name>"

buildCache {
    local {
        directory = "<build-cache-dir>"
    }
}

// TestKit builds use fresh Gradle caches on CI, so redirect public repositories to avoid upstream rate limits.
pluginManagement {
    repositories {
        repositories {
            maven("https://cache-redirector.jetbrains.com/repo.maven.apache.org/maven2")
            maven("<build-repo>")
            maven("https://cache-redirector.jetbrains.com/dl.google.com/dl/android/maven2")
            maven("https://cache-redirector.jetbrains.com/plugins.gradle.org/m2")
        }
    }
}

dependencyResolutionManagement {
    repositories {
        maven("https://cache-redirector.jetbrains.com/repo.maven.apache.org/maven2")
        maven("<build-repo>")
        maven("https://cache-redirector.jetbrains.com/dl.google.com/dl/android/maven2")
    }
}
