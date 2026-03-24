/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import java.io.ByteArrayOutputStream

plugins {
    base
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.conventions.kmp) apply false
    alias(libs.plugins.conventions.jvm) apply false
}

group = "org.jetbrains.kotlinx"
val grpcVersion = providers.gradleProperty("grpcVersion").get()
val shimVersion = providers.gradleProperty("shimVersion").get()
version = "$grpcVersion-$shimVersion"

val verificationRepositoryDir = layout.buildDirectory.dir("verification-repo")

allprojects {
    group = rootProject.group
    version = rootProject.version

    plugins.withId("maven-publish") {
        extensions.configure<PublishingExtension> {
            repositories {
                maven {
                    name = "verification"
                    url = uri(rootProject.layout.buildDirectory.dir("verification-repo"))
                }
            }
        }
    }
}

tasks.named("assemble") {
    dependsOn(":core:assemble")
    dependsOn(":annotation:assemble")
}

tasks.register("publishAllPublicationsToVerificationRepository") {
    group = "publishing"
    dependsOn(":core:publishAllPublicationsToVerificationRepository")
    dependsOn(":annotation:publishAllPublicationsToVerificationRepository")
}

tasks.register("publishAllPublicationsToBuildRepoRepository") {
    group = "publishing"
    dependsOn(":core:publishAllPublicationsToBuildRepoRepository")
    dependsOn(":annotation:publishAllPublicationsToBuildRepoRepository")
}

tasks.register("publishToBuildRepo") {
    group = "publishing"
    dependsOn("publishAllPublicationsToBuildRepoRepository")
}

fun verificationCompileTaskName(): String {
    val os = System.getProperty("os.name").lowercase()
    val arch = System.getProperty("os.arch").lowercase()
    return when {
        os.contains("mac") && (arch.contains("aarch64") || arch.contains("arm64")) -> "compileKotlinMacosArm64"
        os.contains("mac") -> "compileKotlinMacosX64"
        os.contains("linux") && (arch.contains("aarch64") || arch.contains("arm64")) -> "compileKotlinLinuxArm64"
        os.contains("linux") -> "compileKotlinLinuxX64"
        else -> error("Unsupported verification host: os=$os arch=$arch")
    }
}

fun registerVerificationBuildTask(
    name: String,
    projectPath: String,
    expectFailure: Boolean,
    diagnosticSubstring: String? = null,
) = tasks.register(name) {
    group = "verification"

    doLast {
        verificationRepositoryDir.get().asFile.deleteRecursively()

        fun runNestedBuild(taskPath: String): Pair<Int, String> {
            val outputBuffer = ByteArrayOutputStream()
            val process = ProcessBuilder(
                "./gradlew",
                taskPath,
                "--no-daemon",
                "--console=plain",
                "--rerun-tasks",
                "--refresh-dependencies",
            )
                .directory(layout.projectDirectory.asFile)
                .redirectErrorStream(true)
                .start()
            process.inputStream.copyTo(outputBuffer)
            return process.waitFor() to outputBuffer.toString(Charsets.UTF_8)
        }

        val (publishExitCode, publishOutput) = runNestedBuild(":publishAllPublicationsToVerificationRepository")
        check(publishExitCode == 0) {
            "Expected verification publication to succeed, but it failed.\n$publishOutput"
        }

        val (exitCode, output) = runNestedBuild(projectPath)
        if (expectFailure) {
            check(exitCode != 0) {
                "Expected $projectPath to fail, but it succeeded.\n$output"
            }
            if (diagnosticSubstring != null) {
                check(output.contains(diagnosticSubstring)) {
                    "Expected $projectPath to mention '$diagnosticSubstring'.\n$output"
                }
            }
        } else {
            check(exitCode == 0) {
                "Expected $projectPath to succeed, but it failed.\n$output"
            }
        }
    }
}

val verifyUnsupportedApiNegative = registerVerificationBuildTask(
    name = "verifyUnsupportedApiNegative",
    projectPath = ":verification-negative:${verificationCompileTaskName()}",
    expectFailure = true,
    diagnosticSubstring = "internal implementation details",
)

val verifyUnsupportedApiPositive = registerVerificationBuildTask(
    name = "verifyUnsupportedApiPositive",
    projectPath = ":verification-positive:${verificationCompileTaskName()}",
    expectFailure = false,
)

val verifyUnsupportedApiScope = registerVerificationBuildTask(
    name = "verifyUnsupportedApiScope",
    projectPath = ":verification-scope:${verificationCompileTaskName()}",
    expectFailure = false,
)

tasks.register("verifyGrpcShimUnsupportedApiOptIn") {
    group = "verification"
    dependsOn(verifyUnsupportedApiNegative, verifyUnsupportedApiPositive, verifyUnsupportedApiScope)
}

tasks.named("check") {
    dependsOn("verifyGrpcShimUnsupportedApiOptIn")
}
