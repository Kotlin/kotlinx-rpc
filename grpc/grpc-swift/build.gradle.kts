/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import util.setPublicArtifactId
import util.targets.configureNativePublication

plugins {
    id("conventions-common")
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    explicitApi()

    val iosTargets = listOf(
        iosArm64(),
        iosSimulatorArm64(),
        iosX64(),
    )

    iosTargets.forEach { target ->
        target.mavenPublication {
            setPublicArtifactId(project)
        }
    }
    project.configureNativePublication(iosTargets)

    swiftPMDependencies {
        iosMinimumDeploymentTarget.set("18.0")
        discoverClangModulesImplicitly.set(false)
        localSwiftPackage(
            directory = project.layout.projectDirectory,
            products = listOf(product("GrpcSwiftBridge", platforms = setOf(iOS()))),
        )
    }
}
