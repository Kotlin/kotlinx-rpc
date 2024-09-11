/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import util.kotlinVersionParsed
import util.libs

plugins {
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.kotlinx.rpc) apply false
    alias(libs.plugins.conventions.kover)
    alias(libs.plugins.conventions.gradle.doctor)
    alias(libs.plugins.binary.compatibility.validator)
    alias(libs.plugins.atomicfu)
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

val executeNpmLogin by tasks.registering {
    val registryUrl = "https://packages.jetbrains.team/npm/p/krpc/build-deps/"

    // To prevent leaking of credentials in VCS on dev machine use the build directory config file
    val buildYarnConfigFile = File(project.rootDir, "build/js/.yarnrc")
    val buildYarnYmlConfigFile = File(project.rootDir, "build/js/.yarnrc.yml")

    val spaceUsername: String? = getSpaceUsername()
    val spacePassword: String? = getSpacePassword()

    doLast {
        if (spaceUsername == null || spacePassword == null) {
            return@doLast
        }

        if (spacePassword.split(".").size != 3) {
            error("Unexpected Space Token format")
        }

        val outputYarnYmlText = """       
            npmRegistryServer: "$registryUrl"
            npmAlwaysAuth: true
            npmAuthToken: "$spacePassword"
        """.trimIndent()

        buildYarnConfigFile.createNewFile()
        buildYarnConfigFile.writeText("registry: $registryUrl")
        buildYarnYmlConfigFile.createNewFile()
        buildYarnYmlConfigFile.writeText(outputYarnYmlText)
    }

    outputs.file(buildYarnConfigFile).withPropertyName("buildOutputYarnFile")
    outputs.file(buildYarnYmlConfigFile).withPropertyName("buildOutputYarnYmlFile")
}

plugins.withType(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin::class.java).configureEach {
    rootProject.extensions.configure(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension::class.java) {
        download = true
        downloadBaseUrl = "https://packages.jetbrains.team/files/p/krpc/build-deps/"
    }

    tasks.named("kotlinNpmInstall").configure {
        dependsOn(executeNpmLogin)
    }
}

// necessary for CI js tests
rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin> {
    rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension> {
        ignoreScripts = false
        download = true
        downloadBaseUrl = "https://packages.jetbrains.team/files/p/krpc/build-deps/"
    }
}
