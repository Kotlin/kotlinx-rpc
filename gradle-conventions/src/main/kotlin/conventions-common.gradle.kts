/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import io.gitlab.arturbosch.detekt.Detekt
import util.configureJvmPublication
import util.configureKmpPublication
import util.configureKrpcPublication
import util.libs

plugins {
    id("io.gitlab.arturbosch.detekt")
}

val kotlinVersion: String by extra
val globalRootDir: String by extra

val publishingExtension = project.extensions.findByType<PublishingExtension>()

// for some reason jvm publication will be registered by module itself, for example for gradle plugins
val skipJvmPublication: Boolean? by extra

if (name.startsWith("krpc")) {
    if (publishingExtension != null) {
        publishingExtension.configureKrpcPublication()
    } else {
        plugins.withId("org.jetbrains.kotlin.jvm") {
            configureJvmPublication(skipJvmPublication == true)
        }

        plugins.withId("org.jetbrains.kotlin.multiplatform") {
            configureKmpPublication()
        }
    }
}

val globalDetektDir = "$globalRootDir/detekt"

// https://detekt.dev/docs/gettingstarted/gradle#options-for-detekt-configuration-closure
detekt {
    toolVersion = libs.versions.detekt.analyzer.get()

    buildUponDefaultConfig = false

    // TC will fail only on Detekt codestyle configuration
    // Otherwise it would be annoying during development process
    ignoreFailures = true

    config.setFrom("$globalDetektDir/config.yaml")
    baseline = file("$globalDetektDir/baseline.xml")
}

val detektGlobalReportsDir = "$globalDetektDir/reports/${rootProject.name}"

tasks.withType<Detekt>().configureEach {
    reports {
        listOf(html, xml, sarif).forEach { report ->
            report.required.set(true)
            val output = "$detektGlobalReportsDir/${report.type.reportId}/${project.name}.${report.type.extension}"
            report.outputLocation.set(file(output))
        }
    }
}

// https://detekt.dev/docs/gettingstarted/gradle#disabling-detekt-from-the-check-task
// 'build' task depends on check, and we don't want to run detekt all the time
afterEvaluate {
    tasks.named("check") {
        setDependsOn(dependsOn.filterNot {
            it is TaskProvider<*> && it.name.contains("detekt")
        })
    }
}

if (kotlinVersion >= "1.8.0") {
    apply(plugin = "org.jetbrains.kotlinx.kover")

    val thisProject = project

    rootProject.configurations.matching { it.name == "kover" }.all {
        rootProject.dependencies.add("kover", thisProject)
    }
}
