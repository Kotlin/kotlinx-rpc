/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.rpc) apply false
    alias(libs.plugins.atomicfu) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.kover.root.project) apply false
    alias(libs.plugins.binary.compatibility.validator)
}

object Const {
    const val KRPC_COMPILER_PLUGIN_MODULE_NAME = "kotlinx-rpc-compiler-plugin"
    const val INTERNAL_KRPC_API_ANNOTATION = "kotlinx.rpc.internal.InternalKRPCApi"
}

apiValidation {
    val compilerPluginModules = subprojects.single { it.name == Const.KRPC_COMPILER_PLUGIN_MODULE_NAME }.let {
        it.subprojects.map { sub -> sub.name }
    } + Const.KRPC_COMPILER_PLUGIN_MODULE_NAME

    ignoredProjects.addAll(
        listOf(
            "codegen-tests-jvm",
            "codegen-tests-mpp",
            "kotlinx-rpc-runtime-test",
            "kotlinx-rpc-utils",
            "kotlinx-rpc-utils-service-loader",
            "kotlinx-rpc-ksp-plugin",
        ) + compilerPluginModules
    )

    nonPublicMarkers.add(Const.INTERNAL_KRPC_API_ANNOTATION)
}

val kotlinVersion: String by extra

if (kotlinVersion >= "1.8.0") {
    apply(plugin = libs.plugins.kover.root.project.get().pluginId)
}

allprojects {
    group = "org.jetbrains.kotlinx"
    version = rootProject.libs.versions.krpc.full.get()
}

println("kotlinx.rpc project version: $version")

// If the prefix of the kPRC version is not Kotlin gradle plugin version - you have a problem :)
// Probably some dependency brings kotlin with higher version.
// To mitigate so, please refer to `gradle/kotlin-version-lookup.json`
// and it's usage in `gradle-settings-conventions/src/main/kotlin/settings-conventions.settings.gradle.kts`
val kotlinGPVersion = getKotlinPluginVersion()
if (!version.toString().startsWith(kotlinGPVersion)) {
    error("KGP version mismatch. Project version: $version, KGP version: $kotlinGPVersion")
}

// necessary for CI js tests
rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin> {
    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension>().ignoreScripts = false
}
