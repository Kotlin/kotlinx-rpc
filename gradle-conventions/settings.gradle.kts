/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

rootProject.name = "gradle-conventions"

pluginManagement {
    includeBuild("../gradle-settings-conventions")
}

plugins {
    id("settings-conventions")
}

include(":compiler-specific-module")
include(":conventions-utils")

val kotlinVersion: String by extra
val isLatestKotlinVersion: Boolean by extra

if (isLatestKotlinVersion) {
    include(":kover")
    include(":gradle-publish")
} else {
    include(":kover-stub")
    include(":gradle-publish-stub")
}

val kotlinVersionProject = include(":kotlin-version")

val kotlinVersionModuleDirName = if (kotlinVersion <= "1.9.20") {
    "kotlin-version-old"
} else {
    "kotlin-version-new"
}

project(":kotlin-version").projectDir = file(kotlinVersionModuleDirName)
