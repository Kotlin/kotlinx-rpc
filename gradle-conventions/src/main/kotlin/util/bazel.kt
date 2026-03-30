/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.Task
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.io.File

// Shared Bazel/Konan helpers for native dependency builds.
// These are used by active native-deps projects such as grpc, grpc-shim, and protobuf-shim.
// Do not remove or simplify them as dead code without checking those builds first.

data class NativeDependencyTarget(
    val kotlinName: String,
    val bazelName: String,
    val publicationSuffix: String,
)

val nativeDependencyTargets = listOf(
    NativeDependencyTarget("iosArm64", "ios_arm64", "iosarm64"),
    NativeDependencyTarget("iosSimulatorArm64", "ios_simulator_arm64", "iossimulatorarm64"),
    NativeDependencyTarget("iosX64", "ios_x64", "iosx64"),
    NativeDependencyTarget("macosArm64", "macos_arm64", "macosarm64"),
    NativeDependencyTarget("macosX64", "macos_x64", "macosx64"),
    NativeDependencyTarget("tvosArm64", "tvos_arm64", "tvosarm64"),
    NativeDependencyTarget("tvosSimulatorArm64", "tvos_simulator_arm64", "tvossimulatorarm64"),
    NativeDependencyTarget("tvosX64", "tvos_x64", "tvosx64"),
    NativeDependencyTarget("watchosArm32", "watchos_arm32", "watchosarm32"),
    NativeDependencyTarget("watchosArm64", "watchos_arm64", "watchosarm64"),
    NativeDependencyTarget("watchosDeviceArm64", "watchos_device_arm64", "watchosdevicearm64"),
    NativeDependencyTarget("watchosSimulatorArm64", "watchos_simulator_arm64", "watchossimulatorarm64"),
    NativeDependencyTarget("watchosX64", "watchos_x64", "watchosx64"),
    NativeDependencyTarget("linuxArm64", "linux_arm64", "linuxarm64"),
    NativeDependencyTarget("linuxX64", "linux_x64", "linuxx64"),
)

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

fun Project.konanHomeProvider(): Provider<String> = providers.provider { findKonanHome() }

fun Project.requireGradleProperty(name: String): String = providers.gradleProperty(name).orNull
    ?: error("Missing $name in ${layout.projectDirectory.file("gradle.properties").asFile.absolutePath}")

fun Project.registerCheckBazelTask(name: String = "checkBazel"): TaskProvider<Exec> =
    tasks.register<Exec>(name) {
        group = "verification"
        description = "Checks that Bazelisk (bazel) is available on PATH"
        commandLine(
            "sh",
            "-c",
            "command -v bazel >/dev/null 2>&1 || command -v bazelisk >/dev/null 2>&1",
        )
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

fun Project.registerCheckKonanHomeTask(
    prepareKonanHome: TaskProvider<out Task>,
    konanHome: Provider<String>,
    name: String = "checkKonanHome",
): TaskProvider<Task> = tasks.register(name) {
    dependsOn(prepareKonanHome)
    doLast {
        val dir = file(konanHome.get())
        check(dir.isDirectory) {
            "KONAN_HOME does not exist: ${dir.absolutePath}"
        }
    }
}

fun Project.registerSyncBazelModuleVersionTask(
    moduleFile: File,
    version: String,
    variableName: String = "GRPC_VERSION",
    name: String = "syncGrpcVersionToBazelModule",
): TaskProvider<Task> = tasks.register(name) {
    doLast {
        val currentText = moduleFile.readText()
        val updatedText = currentText.replaceBazelModuleVersion(variableName, version, moduleFile)
        if (updatedText != currentText) {
            moduleFile.writeText(updatedText)
        }
    }
}

private fun String.replaceBazelModuleVersion(variableName: String, version: String, moduleFile: File): String {
    val regex = Regex("""(?m)^${Regex.escape(variableName)}\s*=\s*"[^"]*"$""")
    check(regex.containsMatchIn(this)) {
        "Failed to find $variableName assignment in ${moduleFile.absolutePath}"
    }
    return replace(regex, """$variableName = "$version"""")
}

fun KotlinMultiplatformExtension.registerNativeDependencyTargets() {
    iosArm64()
    iosSimulatorArm64()
    iosX64()
    macosArm64()
    macosX64()
    tvosArm64()
    tvosSimulatorArm64()
    tvosX64()
    watchosArm32()
    watchosArm64()
    watchosDeviceArm64()
    watchosSimulatorArm64()
    watchosX64()
    linuxArm64()
    linuxX64()
}

fun String.toTaskSuffix(): String = split('_').joinToString("") { part ->
    part.replaceFirstChar { char -> char.uppercase() }
}
