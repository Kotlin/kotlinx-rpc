/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import util.otherwise
import util.whenForIde

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

dependencies {
    testPriorityRuntimeClasspath(libs.intellij.util) { isTransitive = false }

    implementation(projects.core)

    testRuntimeOnly(libs.kotlin.test)
    testRuntimeOnly(libs.kotlin.script.runtime)
    testRuntimeOnly(libs.kotlin.annotations.jvm)

    whenForIde {
        testImplementation(libs.serialization.plugin.forIde) {
            isTransitive = false
        }
    } otherwise {
        testImplementation(libs.serialization.plugin)
    }

    testImplementation(libs.compiler.plugin.cli) {
        exclude(group = "org.jetbrains.kotlinx", module = "compiler-plugin-k2")
    }
    testImplementation(files("$globalRootDir/compiler-plugin/compiler-plugin-k2/build/libs/plugin-k2-for-tests.jar"))

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
    testDataClasspath(libs.coroutines.core)
    testDataClasspath(libs.serialization.core)
}

val updateTestData = (project.findProperty("kotlin.test.update.test.data") as? String) ?: "false"

tasks.test {
    dependsOn(tasks.getByName("jar"))
    dependsOn(project(":core").tasks.getByName("jvmJar"))
    dependsOn(project(":utils").tasks.getByName("jvmJar"))
    dependsOn(project(":krpc:krpc-core").tasks.getByName("jvmJar"))

    useJUnitPlatform()

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

val generateTests by tasks.creating(JavaExec::class) {
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass.set("kotlinx.rpc.codegen.test.GenerateTestsKt")
}

val isCI = System.getenv("TEAMCITY_VERSION") != null

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

    val path = testRuntimeClasspath
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
