/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    alias(libs.plugins.conventions.jvm)
}

// this setup – courtesy of https://github.com/demiurg906/kotlin-compiler-plugin-template/tree/master

repositories {
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
}

sourceSets {
    test {
        java.srcDir("src/test-gen")
    }
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}

dependencies {
    implementation(projects.core)

    testRuntimeOnly(libs.kotlin.test)
    testRuntimeOnly(libs.kotlin.script.runtime)
    testRuntimeOnly(libs.kotlin.annotations.jvm)

    testImplementation(libs.serialization.plugin)
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
}

val globalRootDir: String by extra

testDataRuntimeDependencies(
    libs.coroutines.core,
    libs.serialization.core,
)

tasks.test {
    dependsOn(project(":core").tasks.getByName("jvmJar"))
    dependsOn(project(":utils").tasks.getByName("jvmJar"))

    useJUnitPlatform()

    doFirst {
        systemProperty("kotlinx.rpc.globalRootDir", globalRootDir)

        val updateData = (project.findProperty("kotlin.test.update.test.data") as? String) ?: "false"
        systemProperty("kotlin.test.update.test.data", updateData)

        setJarPathAsProperty("org.jetbrains.kotlin.test.kotlin-stdlib", "kotlin-stdlib")
        setJarPathAsProperty("org.jetbrains.kotlin.test.kotlin-stdlib-jdk8", "kotlin-stdlib-jdk8")
        setJarPathAsProperty("org.jetbrains.kotlin.test.kotlin-reflect", "kotlin-reflect")
        setJarPathAsProperty("org.jetbrains.kotlin.test.kotlin-test", "kotlin-test")
        setJarPathAsProperty("org.jetbrains.kotlin.test.kotlin-script-runtime", "kotlin-script-runtime")
        setJarPathAsProperty("org.jetbrains.kotlin.test.kotlin-annotations-jvm", "kotlin-annotations-jvm")
    }
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

tasks.named<KotlinCompile>("compileTestKotlin").configure {
    finalizedBy(generateTests)
}

fun testDataRuntimeDependencies(vararg dependencyNotations: Provider<MinimalExternalModuleDependency>) {
    dependencyNotations.forEach {
        dependencies.implementation(it)
    }

    tasks.test {
        doFirst {
            setJarPathAsProperty(
                propName = "kotlinx.rpc.test.data.classpath.dependencies",
                jarNames = dependencyNotations.map { it.get().name + "-jvm" }.toTypedArray(),
                searchIn = project.configurations.runtimeClasspath,
            )
        }
    }
}

fun Test.setJarPathAsProperty(
    propName: String,
    vararg jarNames: String,
    searchIn: NamedDomainObjectProvider<Configuration> = project.configurations.testRuntimeClasspath,
) {
    val includedRegex = jarNames.toSet().joinToString("|", "(", ")") { jarName ->
        "$jarName-\\d.*jar"
    }.toRegex()

    val path = searchIn.get()
        .files
        .filter { includedRegex.matches(it.name) }
        .takeIf { it.isNotEmpty() }
        ?.joinToString(File.pathSeparator) { it.absolutePath }
        ?: run {
            logger.warn("Can't find any of ${jarNames.joinToString()} in ${searchIn.get().name}")
            return
        }

    logger.info("Setting prop $propName=$path")
    systemProperty(propName, path)
}
