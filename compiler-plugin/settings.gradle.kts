/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

rootProject.name = "compiler-plugin"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("../gradle-conventions")
    includeBuild("../gradle-settings-conventions")
}

plugins {
    id("settings-conventions")
}

includeRootAsPublic()
