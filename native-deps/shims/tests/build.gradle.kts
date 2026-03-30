/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.conventions.jvm)
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
    jvmToolchain(17)
}

dependencies {
    testImplementation(gradleTestKit())
    testImplementation(platform(libs.junit5.bom))
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.junit5.jupiter)
    testImplementation(libs.junit5.jupiter.api)
    testRuntimeOnly(libs.junit5.platform.launcher)
}

data class HostTarget(
    val declaration: String,
    val compileTask: String,
    val publicationTaskSuffix: String,
    val publicationSuffix: String,
)

data class ShimVerificationConfig(
    val taskPrefix: String,
    val systemPropertyPrefix: String,
    val projectPath: String,
    val fixtureClassName: String,
)

fun currentHostTarget(): HostTarget {
    val os = System.getProperty("os.name").lowercase()
    val arch = System.getProperty("os.arch").lowercase()
    return when {
        os.contains("mac") && (arch.contains("aarch64") || arch.contains("arm64")) ->
            HostTarget("macosArm64()", "compileKotlinMacosArm64", "MacosArm64", "macosarm64")
        os.contains("mac") ->
            HostTarget("macosX64()", "compileKotlinMacosX64", "MacosX64", "macosx64")
        os.contains("linux") && (arch.contains("aarch64") || arch.contains("arm64")) ->
            HostTarget("linuxArm64()", "compileKotlinLinuxArm64", "LinuxArm64", "linuxarm64")
        os.contains("linux") ->
            HostTarget("linuxX64()", "compileKotlinLinuxX64", "LinuxX64", "linuxx64")
        else -> error("Unsupported verification host: os=$os arch=$arch")
    }
}

val hostTarget = currentHostTarget()
val verificationRepositoryDir = rootProject.layout.buildDirectory.dir("verification-repo")
val publishRootAnnotation = project(":kotlinx-rpc-native-shims-annotation").tasks.named("publishKotlinMultiplatformPublicationToVerificationRepository")
val publishHostAnnotation = project(":kotlinx-rpc-native-shims-annotation").tasks.named("publish${hostTarget.publicationTaskSuffix}PublicationToVerificationRepository")
val testSourceSet = the<SourceSetContainer>()["test"]
val shimVerificationConfigs = listOf(
    ShimVerificationConfig(
        taskPrefix = "grpc",
        systemPropertyPrefix = "grpcShim",
        projectPath = ":kotlinx-rpc-grpc-core-shim",
        fixtureClassName = "kotlinx.rpc.grpc.internal.GrpcShimFixtureTest",
    ),
    ShimVerificationConfig(
        taskPrefix = "protobuf",
        systemPropertyPrefix = "protobufShim",
        projectPath = ":kotlinx-rpc-protobuf-shim",
        fixtureClassName = "kotlinx.rpc.protobuf.internal.ProtobufShimFixtureTest",
    ),
)

fun Test.configureFixtureVerification(configs: List<ShimVerificationConfig> = shimVerificationConfigs) {
    dependsOn(
        configs.map { config ->
            project(config.projectPath).tasks.named("publish${hostTarget.publicationTaskSuffix}PublicationToVerificationRepository")
        } + publishRootAnnotation + publishHostAnnotation,
    )
    useJUnitPlatform()

    configs.forEach { config ->
        systemProperty("${config.systemPropertyPrefix}VerificationRepoDir", verificationRepositoryDir.get().asFile.absolutePath)
        systemProperty("${config.systemPropertyPrefix}Version", project(config.projectPath).version.toString())
        systemProperty("${config.systemPropertyPrefix}KotlinVersion", rootProject.libs.versions.kotlin.lang.get())
        systemProperty("${config.systemPropertyPrefix}HostTargetDeclaration", hostTarget.declaration)
        systemProperty("${config.systemPropertyPrefix}HostCompileTask", hostTarget.compileTask)
        systemProperty("${config.systemPropertyPrefix}HostPublicationSuffix", hostTarget.publicationSuffix)
    }
}

tasks.withType<Test>().configureEach {
    configureFixtureVerification()
}

fun registerTaggedTest(name: String, className: String, tag: String) = tasks.register<Test>(name) {
    group = "verification"
    description = "Runs $className fixture tests tagged '$tag'."
    testClassesDirs = testSourceSet.output.classesDirs
    classpath = testSourceSet.runtimeClasspath
    configureFixtureVerification()
    filter {
        includeTestsMatching(className)
    }
    useJUnitPlatform {
        includeTags(tag)
    }
}

listOf("negative", "positive", "scope", "artifact").forEach { tag ->
    val taskNameSuffix = tag.replaceFirstChar { it.uppercase() }
    shimVerificationConfigs.forEach { config ->
        registerTaggedTest(
            name = "${config.taskPrefix}${taskNameSuffix}Test",
            className = config.fixtureClassName,
            tag = tag,
        )
    }
}
