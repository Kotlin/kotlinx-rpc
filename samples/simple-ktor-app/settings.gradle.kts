/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

rootProject.name = "simple-ktor-app"

pluginManagement {
    repositories {
        maven(url = "https://maven.pkg.jetbrains.space/public/p/krpc/maven")
        gradlePluginPortal()
    }
}
