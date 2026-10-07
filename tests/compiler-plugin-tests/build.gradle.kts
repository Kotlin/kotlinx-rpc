/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    alias(libs.plugins.conventions.jvm)
}

// this setup – courtesy of https://github.com/demiurg906/kotlin-compiler-plugin-template/tree/master

sourceSets {
    test {
        java.srcDir("src/test-gen")
    }
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}

val testDataClasspath: Configuration by configurations.creating

val globalRootDir: String by extra

val testArtifacts: Configuration by configurations.creating

dependencies {
    implementation(projects.core)
    implementation(projects.grpc.grpcCore)

    testArtifacts(libs.kotlin.stdlib)
    testArtifacts(libs.kotlin.stdlib.jdk8)
    testArtifacts(libs.kotlin.reflect)
    testArtifacts(libs.kotlin.test)
    testArtifacts(libs.kotlin.script.runtime)
    testArtifacts(libs.kotlin.annotations.jvm)

    // uncomment when serialization is needed for testing again
//    whenForIde {
//        testImplementation(libs.serialization.plugin.forIde) {
//            isTransitive = false
//        }
//    } otherwise {
//        testImplementation(libs.serialization.plugin)
//    }
//
//    testDataClasspath(libs.serialization.core)

    testImplementation(libs.compiler.plugin.common)
    testImplementation(libs.compiler.plugin.backend)
    testImplementation(libs.compiler.plugin.k2)
    testImplementation(libs.compiler.plugin.cli)

    testImplementation(libs.kotlin.reflect)
    testImplementation(libs.kotlin.compiler)
    testImplementation(libs.kotlin.compiler.test.framework)

    testImplementation(libs.junit4)

    testImplementation(platform(libs.junit5.bom))
    testImplementation(libs.junit5.jupiter)
    testImplementation(libs.junit5.platform.commons)
    testImplementation(libs.junit5.platform.launcher)
    testImplementation(libs.junit5.platform.runner)
    testImplementation(libs.junit5.platform.suite.api)

    testDataClasspath(projects.utils)
    testDataClasspath(projects.grpc.grpcMarshaller)
    testDataClasspath(projects.grpc.grpcCore)
    testDataClasspath(projects.protobuf.protobufLite)
    testDataClasspath(projects.tests.compilerPluginTests.compilerPluginProtos)
    testDataClasspath(libs.kotlinx.io.core)
    testDataClasspath(libs.coroutines.core)
}

val updateTestData = (project.findProperty("kotlin.test.update.test.data") as? String) ?: "false"

tasks.test {
    dependsOn(tasks.getByName("jar"))
    dependsOn(project(":core").tasks.getByName("jvmJar"))
    dependsOn(project(":utils").tasks.getByName("jvmJar"))
    dependsOn(project(":krpc:krpc-core").tasks.getByName("jvmJar"))
    dependsOn(project(":protobuf:protobuf-lite").tasks.getByName("jvmJar"))
    dependsOn(project(":tests:compiler-plugin-tests:compiler-plugin-protos").tasks.getByName("jvmJar"))
    dependsOn("generateTests")

    inputs.dir("src/testData")
        .ignoreEmptyDirectories()
        .normalizeLineEndings()
        .withPathSensitivity(PathSensitivity.RELATIVE)

    useJUnitPlatform()

    systemProperty("idea.ignore.disabled.plugins", "true")
    systemProperty("idea.home.path", rootDir)

    systemPropertyLogged("kotlinx.rpc.globalRootDir", globalRootDir)
    systemPropertyLogged("kotlin.test.update.test.data", updateTestData)

    setJarPathAsProperty("org.jetbrains.kotlin.test.kotlin-stdlib", "kotlin-stdlib")
    setJarPathAsProperty("org.jetbrains.kotlin.test.kotlin-stdlib-jdk8", "kotlin-stdlib-jdk8")
    setJarPathAsProperty("org.jetbrains.kotlin.test.kotlin-reflect", "kotlin-reflect")
    setJarPathAsProperty("org.jetbrains.kotlin.test.kotlin-test", "kotlin-test")
    setJarPathAsProperty("org.jetbrains.kotlin.test.kotlin-script-runtime", "kotlin-script-runtime")
    setJarPathAsProperty("org.jetbrains.kotlin.test.kotlin-annotations-jvm", "kotlin-annotations-jvm")

    systemPropertyLogged(
        name = "kotlinx.rpc.test.data.classpath.dependencies",
        value = testDataClasspath.files.joinToString(File.pathSeparator) { it.absolutePath },
    )
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        optIn.add("org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
        optIn.add("org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI")
    }
}

val generateTests = tasks.register<JavaExec>("generateTests") {
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass.set("kotlinx.rpc.codegen.test.GenerateTestsKt")
}

val isCI = System.getenv("TEAMCITY_VERSION") != null || System.getenv("GITHUB_ACTIONS") != null

tasks.named<KotlinCompile>("compileTestKotlin").configure {
    if (!isCI) {
        finalizedBy(generateTests)
    }
}

fun Test.setJarPathAsProperty(
    propName: String,
    jarName: String,
) {
    val includedRegex = "$jarName-\\d.*jar".toRegex()

    val path = testArtifacts
        .files
        .firstOrNull { includedRegex.matches(it.name) }
        ?: run {
            logger.warn("Can't find $jarName in testRuntimeClasspath configuration")
            return
        }

    systemPropertyLogged(propName, path)
}

fun Test.systemPropertyLogged(name: String, value: Any) {
    logger.info("Setting test prop $name=$value")
    systemProperty(name, value)
}
