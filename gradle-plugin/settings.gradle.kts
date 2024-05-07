/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

rootProject.name = "kotlinx-rpc-gradle-plugin"

pluginManagement {
    includeBuild("../gradle-conventions")
    includeBuild("../gradle-settings-conventions")
}

plugins {
    id("settings-conventions")
}

includePublic(":gradle-plugin-api")
includePublic(":gradle-plugin-all")
includePublic(":gradle-plugin-platform")
