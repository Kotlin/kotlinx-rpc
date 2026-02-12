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
val testRuntimeClasspath: Configuration by project.configurations.getting

val globalRootDir: String by extra

/**
 * I should probably explain this.
 *
 * `kotlin-compiler` dependency has its inner dependency on `libs.intellij.util`.
 * In fact, it packs all necessary classes inside its jar (making it fat in some sense).
 * Amongst these packed classes there is `com.intellij.openapi.util.io.NioFiles`, which is used by the tests' runtime.
 *
 * `NioFiles` is problematic.
 * It was packed with kotlin-compiler jar, but Proguard which excluded `deleteRecursively` method from it.
 * And this method is called.
 * So tests fail with:
 * ```
 * java.lang.NoSuchMethodError: com.intellij.openapi.util.io.NioFiles.deleteRecursively(Ljava/nio/file/Path;)V
 * ```
 *
 * To mitigate, we need to load the proper `NioFiles` with all methods from the jar,
 * which wasn't striped by the Proguard.
 * This jar is `libs.intellij.util`.
 * But to load the class from it, we need to guarantee
 * that this jar is present earlier in the classloader's list, than the `kotlin-compiler` jar.
 *
 * `kotlin-compiler-embeddable` does pack the class inside its jar.
 * But if you try to use it, you would eventually get `java.lang.VerifyError: Bad type on operand stack`
 * and you don't want to fix it.
 *
 * So here we are.
 * This is bad, but hey, it is working!
 */
val testPriorityRuntimeClasspath: Configuration by configurations.creating

sourceSets.test.configure {
    runtimeClasspath = testPriorityRuntimeClasspath + sourceSets.test.get().runtimeClasspath
}

val testArtifacts: Configuration by configurations.creating

dependencies {
    testPriorityRuntimeClasspath(libs.intellij.util) { isTransitive = false }

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
    testDataClasspath(projects.grpc.grpcCodec)
    testDataClasspath(projects.grpc.grpcCore)
    testDataClasspath(projects.protobuf.protobufCore)
    testDataClasspath(libs.kotlinx.io.core)
    testDataClasspath(libs.coroutines.core)
}

val updateTestData = (project.findProperty("kotlin.test.update.test.data") as? String) ?: "false"

tasks.test {
    dependsOn(tasks.getByName("jar"))
    dependsOn(project(":core").tasks.getByName("jvmJar"))
    dependsOn(project(":utils").tasks.getByName("jvmJar"))
    dependsOn(project(":krpc:krpc-core").tasks.getByName("jvmJar"))
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
