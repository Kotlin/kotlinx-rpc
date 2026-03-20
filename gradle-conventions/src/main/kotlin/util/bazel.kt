/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import util.tasks.CheckExecutableTask

fun Project.findKonanHome(): String {
    val userHome = System.getProperty("user.home")
    val osName = System.getProperty("os.name").lowercase()
    val hostOs = when {
        osName.contains("mac") -> "macos"
        osName.contains("linux") -> "linux"
        osName.contains("windows") -> "windows"
        else -> error("Unsupported OS: $osName")
    }
    val hostArch = System.getProperty("os.arch").lowercase().let {
        when {
            it.contains("aarch64") || it.contains("arm64") -> "aarch64"
            it.contains("x86_64") || it.contains("amd64") -> "x86_64"
            else -> error("Unsupported arch: $it")
        }
    }

    val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
    val kotlinVersion = libs.findVersion("kotlin.compiler").get().requiredVersion
    return "$userHome/.konan/kotlin-native-prebuilt-$hostOs-$hostArch-$kotlinVersion"
}

fun Project.registerCheckBazelTask(name: String = "checkBazel"): TaskProvider<CheckExecutableTask> =
    tasks.register<CheckExecutableTask>(name) {
        exec.set("bazel")
        helpMessage.set("Install Bazel: https://bazel.build/")
    }

fun Project.registerPrepareKonanHomeTask(
    name: String = "prepareKonanHome",
    downloadTaskPath: String,
): TaskProvider<Exec> = tasks.register<Exec>(name) {
    val globalRootDir = (findProperty("globalRootDir") as String?)
        ?: layout.projectDirectory.asFile.resolve("../..").normalize().absolutePath
    val gradlew = if (System.getProperty("os.name").lowercase().contains("windows")) {
        "gradlew.bat"
    } else {
        "./gradlew"
    }

    onlyIf {
        !file(findKonanHome()).isDirectory
    }

    workingDir = file(globalRootDir)
    commandLine(gradlew, downloadTaskPath)
}
