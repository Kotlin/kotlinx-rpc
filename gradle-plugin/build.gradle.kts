/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import util.configureMetaTasks

plugins {
    alias(libs.plugins.conventions.jvm) apply false
    alias(libs.plugins.conventions.gradle.publish) apply false
    alias(libs.plugins.gradle.kotlin.dsl) apply false
    alias(libs.plugins.gradle.plugin.publish) apply false
}

subprojects {
    group = "org.jetbrains.kotlinx"
    version = rootProject.libs.versions.kotlinx.rpc.get()

    fun alias(notation: Provider<PluginDependency>): String {
        return notation.get().pluginId
    }

    afterEvaluate {
        plugins.apply(alias(rootProject.libs.plugins.conventions.jvm))

        configure<KotlinJvmProjectExtension> {
            explicitApi = ExplicitApiMode.Disabled
        }
    }
    plugins.apply(alias(rootProject.libs.plugins.gradle.kotlin.dsl))
    plugins.apply(alias(rootProject.libs.plugins.conventions.gradle.publish))

    // This block is needed to show plugin tasks on --dry-run
    //  and to not run task actions on ":plugin:task --dry-run".
    //  The bug is known since June 2017 and still not fixed.
    //  The workaround used below is described here: https://github.com/gradle/gradle/issues/2517#issuecomment-437490287
    if (gradle.parent != null && gradle.parent!!.startParameter.isDryRun) {
        gradle.startParameter.isDryRun = true
    }
}

configureMetaTasks(
    "publishAllPublicationsToBuildRepoRepository", // publish to locally (to the build/repo folder)
    "publishAllPublicationsToSpaceRepository", // publish to Space
    "publishPlugins", // publish to Gradle Plugin Portal
    "publishToMavenLocal", // for local plugin development
    "validatePlugins", // plugin validation
    "detekt", // run Detekt tasks
    "clean",
)
