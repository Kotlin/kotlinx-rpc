/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.gradle.kotlin.dsl.the

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
val publishHostCore = project(":kotlinx-rpc-grpc-core-shim").tasks.named("publish${hostTarget.publicationTaskSuffix}PublicationToVerificationRepository")
val publishHostAnnotation = project(":kotlinx-rpc-grpc-core-shim-annotation").tasks.named("publish${hostTarget.publicationTaskSuffix}PublicationToVerificationRepository")
val testSourceSet = the<SourceSetContainer>()["test"]

fun Test.configureFixtureVerification() {
    dependsOn(publishHostCore, publishHostAnnotation)
    useJUnitPlatform()

    systemProperty("grpcShimVerificationRepoDir", verificationRepositoryDir.get().asFile.absolutePath)
    systemProperty("grpcShimVersion", rootProject.version.toString())
    systemProperty("grpcShimKotlinVersion", rootProject.libs.versions.kotlin.lang.get())
    systemProperty("grpcShimHostTargetDeclaration", hostTarget.declaration)
    systemProperty("grpcShimHostCompileTask", hostTarget.compileTask)
    systemProperty("grpcShimHostPublicationSuffix", hostTarget.publicationSuffix)
}

tasks.withType<Test>().configureEach {
    configureFixtureVerification()
}

fun registerTaggedTest(name: String, tag: String) = tasks.register<Test>(name) {
    group = "verification"
    description = "Runs grpc-shim fixture tests tagged '$tag'."
    testClassesDirs = testSourceSet.output.classesDirs
    classpath = testSourceSet.runtimeClasspath
    configureFixtureVerification()
    useJUnitPlatform {
        includeTags(tag)
    }
}

registerTaggedTest("negativeTest", "negative")
registerTaggedTest("positiveTest", "positive")
registerTaggedTest("scopeTest", "scope")
registerTaggedTest("artifactTest", "artifact")
