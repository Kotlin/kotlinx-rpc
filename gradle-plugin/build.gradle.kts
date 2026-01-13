/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import util.other.generateSource
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists
import kotlin.io.path.readLines

plugins {
    `kotlin-dsl`
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.conventions.gradle.publish)
    alias(libs.plugins.gradle.plugin.publish)
}

group = "org.jetbrains.kotlinx"
version = rootProject.libs.versions.kotlinx.rpc.get()

kotlin {
    explicitApi()

    jvmToolchain(17)

    compilerOptions.optIn.add("kotlinx.rpc.internal.InternalRpcApi")
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        apiVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0
        languageVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0
    }
}

dependencies {
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.android.gradle.plugin.api)

    testImplementation(libs.kotlin.gradle.plugin)
    testImplementation(gradleTestKit())
    testImplementation(platform(libs.junit5.bom))
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.junit5.jupiter)
    testImplementation(libs.junit5.jupiter.api)
    testRuntimeOnly(libs.junit5.platform.launcher)

    testImplementation(libs.logback.classic)
}

tasks.test {
    useJUnitPlatform()

    val protocGen = gradle.includedBuild("protoc-gen")
    dependsOn(protocGen.task(":grpc:publishAllPublicationsToBuildRepoRepository"))
    dependsOn(protocGen.task(":protobuf:publishAllPublicationsToBuildRepoRepository"))

    val compilerPlugin = gradle.includedBuild("compiler-plugin")
    dependsOn(compilerPlugin.task(":compiler-plugin-cli:publishAllPublicationsToBuildRepoRepository"))
    dependsOn(compilerPlugin.task(":compiler-plugin-k2:publishAllPublicationsToBuildRepoRepository"))
    dependsOn(compilerPlugin.task(":compiler-plugin-backend:publishAllPublicationsToBuildRepoRepository"))
    dependsOn(compilerPlugin.task(":compiler-plugin-common:publishAllPublicationsToBuildRepoRepository"))

    dependsOn(":publishAllPublicationsToBuildRepoRepository")
}

// This block is needed to show plugin tasks on --dry-run
//  and to not run task actions on ":plugin:task --dry-run".
//  The bug is known since June 2017 and still not fixed.
//  The workaround used below is described here: https://github.com/gradle/gradle/issues/2517#issuecomment-437490287
if (gradle.parent != null && gradle.parent!!.startParameter.isDryRun) {
    gradle.startParameter.isDryRun = true
}

gradlePlugin {
    plugins {
        create("plugin") {
            id = "org.jetbrains.kotlinx.rpc.plugin"

            displayName = "kotlinx.rpc Gradle Plugin"
            implementationClass = "kotlinx.rpc.RpcGradlePlugin"
            description = """
                The plugin ensures correct RPC configurations for your project, that will allow proper code generation. 
            """.trimIndent()
        }
    }
}

generateSource(
    name = "Versions",
    text = """
        package kotlinx.rpc

        /**
         * The version of the kotlinx.rpc library.
         */
        public const val LIBRARY_VERSION: String = "$version"

        /**
         * The version of the buf tool used to generate protobuf.
         */
        public const val BUF_TOOL_VERSION: String = "${libs.versions.buf.tool.get()}"

    """.trimIndent(),
    chooseSourceSet = { main }
)

val globalRootDir: String by extra

val androidHome = System.getenv("ANDROID_HOME")
    ?: System.getProperty("ANDROID_SDK_HOME")
    ?: Path(globalRootDir, "local.properties")
        .takeIf { it.exists() }
        ?.readLines()
        ?.find { it.startsWith("sdk.dir=") }
        ?.substringAfter("=")
        ?.trim()
    ?: error("ANDROID_HOME is not set")

generateSource(
    name = "TestVersions",
    text = """
        package kotlinx.rpc
        
        const val RPC_VERSION: String = "${libs.versions.kotlinx.rpc.get()}"
        
        const val ANDROID_HOME_DIR: String = "$androidHome"
        
        const val BUILD_REPO: String = "${Path(globalRootDir, "build", "repo").absolutePathString()}"
    """.trimIndent(),
    chooseSourceSet = { test }
)


logger.lifecycle("[Gradle Plugin] kotlinx.rpc project version: $version")
