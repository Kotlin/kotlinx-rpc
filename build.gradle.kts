/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import util.configureApiValidation
import util.configureNpm
import util.configureProjectReport
import util.libs

plugins {
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.kotlinx.rpc) apply false
    alias(libs.plugins.conventions.kover)
    alias(libs.plugins.conventions.gradle.doctor)
    alias(libs.plugins.atomicfu)
}

configureProjectReport()
configureNpm()
configureApiValidation()

val kotlinVersion = rootProject.libs.versions.kotlin.lang.get()

allprojects {
    group = "org.jetbrains.kotlinx"
    version = rootProject.libs.versions.kotlinx.rpc.get()
}

println("kotlinx.rpc project version: $version, Kotlin version: $kotlinVersion")

// If the prefix of the kPRC version is not Kotlin gradle plugin version – you have a problem :)
// Probably some dependency brings kotlin with the later version.
// To mitigate so, refer to `versions-root/kotlin-version-lookup.json`
// and its usage in `gradle-conventions-settings/src/main/kotlin/conventions-version-resolution.settings.gradle.kts`
val kotlinGPVersion = getKotlinPluginVersion()
if (kotlinVersion != kotlinGPVersion) {
    error("KGP version mismatch. Project version: $kotlinVersion, KGP version: $kotlinGPVersion")
}
