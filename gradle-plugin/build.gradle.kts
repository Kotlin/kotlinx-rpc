/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.plugin.compatibility.compatibility
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.tooling.core.KotlinToolingVersion
import util.other.generateSource
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists
import kotlin.io.path.readLines

plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.library.api.watchdog)
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.conventions.devkit.gradle.plugin)
    alias(libs.plugins.conventions.gradle.publish)
    alias(libs.plugins.gradle.plugin.publish)
}

group = "org.jetbrains.kotlinx"
version = rootProject.libs.versions.kotlinx.rpc.get()

kotlin {
    explicitApi()

    jvmToolchain(17)

    // The functional tests don't depend on the main source set, so they don't have the opt-in markers.
    target.compilations.matching { it.name != "functionalTest" }.configureEach {
        compileTaskProvider.configure {
            compilerOptions.optIn.add("kotlinx.rpc.internal.InternalRpcApi")
            compilerOptions.optIn.add("kotlinx.rpc.internal.InternalForSubclassingRpcApi")
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        apiVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2
        languageVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2
    }
}

apiWatchdog {
    enabled = true
}

pluginDevKit {
    compilerPlugin("kotlinx-rpc-compiler-plugin")

    // The DevKit runtime library is not on Maven Central, so the published plugin must not depend on it.
    addRuntimeDependency = false

    // The functional tests use the libraries of the root build. See `functionalTestRepository`.
    companionLibrariesKotlinVersion = KotlinToolingVersion(libs.versions.kotlin.lang.get())
}

buildConfig {
    // The default package is the compiler plugin ID.
    packageName = "kotlinx.rpc"

    forClass("PluginInfo") {
        useKotlinOutput {
            topLevelConstants = false
            internalVisibility = true
        }
    }
}

dependencies {
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.android.gradle.plugin.api)

    apiWatchdogExemptNonTransitive(gradleApi())
    apiWatchdogExemptNonTransitive(libs.kotlin.gradle.plugin)
    apiWatchdogExemptNonTransitive(libs.android.gradle.plugin)
    apiWatchdogExemptNonTransitive(libs.android.gradle.plugin.api)

    testImplementation(libs.kotlin.gradle.plugin)
    testImplementation(gradleTestKit())
    testImplementation(platform(libs.junit6.bom))
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.junit6.jupiter)
    testImplementation(libs.junit6.jupiter.api)
    testRuntimeOnly(libs.junit6.platform.launcher)

    testImplementation(libs.logback.classic)
}

tasks.test {
    useJUnitPlatform()

    val protocGen = gradle.includedBuild("protoc-gen")
    dependsOn(protocGen.task(":grpc:publishAllPublicationsToBuildRepoRepository"))
    dependsOn(protocGen.task(":protobuf:publishAllPublicationsToBuildRepoRepository"))

    val compilerPlugin = gradle.includedBuild("compiler-plugin")
    dependsOn(compilerPlugin.task(":publishAllPublicationsToBuildRepoRepository"))

    dependsOn(":publishAllPublicationsToBuildRepoRepository")
}

// The repository with the kotlinx.rpc libraries for the projects of the functional tests.
// The artifact is the build repository, and the root build publishes the libraries to resolve it.
// See tests/functional-test-repository.
val functionalTestRepository = configurations.dependencyScope("functionalTestRepository")

val functionalTestRepositoryFiles = configurations.resolvable("functionalTestRepositoryFiles") {
    extendsFrom(functionalTestRepository.get())

    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named("kotlinx-rpc-functional-test-repository"))
    }
}

// A standalone build of this directory doesn't have the projects of the root build.
val hasRootBuild = gradle.parent != null

if (hasRootBuild) {
    dependencies {
        functionalTestRepository("org.jetbrains.kotlinx:functional-test-repository:$version")
    }
}

tasks.withType<Test>().matching { it.name.endsWith("FunctionalTest") }.configureEach {
    if (!hasRootBuild) {
        val taskName = name

        doFirst {
            error(
                "The functional tests need the libraries of the root build. " +
                    "Run ':gradle-plugin:$taskName' from the root directory."
            )
        }

        return@configureEach
    }

    val repository = files(functionalTestRepositoryFiles)

    dependsOn(repository)

    // The task doesn't track the content of the build repository, and the publish tasks always run.
    outputs.upToDateWhen { false }

    jvmArgumentProviders.add(
        CommandLineArgumentProvider {
            listOf("-Dkotlinx.rpc.functionalTest.repository=${repository.singleFile.absolutePath}")
        }
    )
}

// This block is needed to show plugin tasks on --dry-run
//  and to not run task actions on ":plugin:task --dry-run".
//  The bug is known since June 2017 and still not fixed.
//  The workaround used below is described here: https://github.com/gradle/gradle/issues/2517#issuecomment-437490287
if (gradle.parent != null && gradle.parent!!.startParameter.isDryRun) {
    gradle.startParameter.isDryRun = true
}

dokka {
    moduleName = "Gradle Plugin"

    // Distinct modulePath is required for included build aggregation with Dokka V2
    modulePath.set("gradle-plugin")

    // Gradle API classes are compileOnly, which may cause unresolved link warnings
    dokkaPublications.configureEach {
        failOnWarning = false
    }
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

            compatibility {
                features {
                    configurationCache = true
                }
            }
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

val globalRootDir = extra["globalRootDir"] as String

val androidHome = System.getenv("ANDROID_HOME")
    ?: System.getProperty("ANDROID_SDK_HOME")
    ?: Path(globalRootDir, "local.properties")
        .takeIf { it.exists() }
        ?.readLines()
        ?.find { it.startsWith("sdk.dir=") }
        ?.substringAfter("=")
        ?.trim()
    ?: ""

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

apiWatchdog {
    javaInterop {
        enabled = false
    }
}
