/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import util.libs

plugins {
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.kotlinx.rpc) apply false
    alias(libs.plugins.conventions.kover)
    alias(libs.plugins.conventions.gradle.doctor)
    alias(libs.plugins.binary.compatibility.validator)

    if (libs.versions.atomicfu.get() >= "0.24.0") {
        alias(libs.plugins.atomicfu.new)
    } else {
        alias(libs.plugins.atomicfu.old)
    }
}

// useful for dependencies introspection
// run ./gradlew htmlDependencyReport
// Report can normally be found in build/reports/project/dependencies/index.html
allprojects {
    plugins.apply("project-report")
}

object Const {
    const val INTERNAL_RPC_API_ANNOTATION = "kotlinx.rpc.internal.utils.InternalRPCApi"
}

apiValidation {
    ignoredPackages.add("kotlinx.rpc.internal")
    ignoredPackages.add("kotlinx.rpc.krpc.internal")

    ignoredProjects.addAll(
        listOf(
            "compiler-plugin-tests",
            "krpc-test",
            "utils",
        )
    )

    nonPublicMarkers.add(Const.INTERNAL_RPC_API_ANNOTATION)
}

val kotlinVersionFull: String by extra

allprojects {
    group = "org.jetbrains.kotlinx"
    version = rootProject.libs.versions.kotlinx.rpc.get()
}

println("kotlinx.rpc project version: $version, Kotlin version: $kotlinVersionFull")

// If the prefix of the kPRC version is not Kotlin gradle plugin version – you have a problem :)
// Probably some dependency brings kotlin with the later version.
// To mitigate so, refer to `versions-root/kotlin-version-lookup.json`
// and its usage in `gradle-conventions-settings/src/main/kotlin/settings-conventions.settings.gradle.kts`
val kotlinGPVersion = getKotlinPluginVersion()
if (kotlinVersionFull != kotlinGPVersion) {
    error("KGP version mismatch. Project version: $kotlinVersionFull, KGP version: $kotlinGPVersion")
}

// necessary for CI js tests
rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin> {
    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension>().ignoreScripts = false
}
