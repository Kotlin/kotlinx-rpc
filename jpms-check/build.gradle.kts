/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.targets.hasJavaModule
import util.targets.javaModuleName

plugins {
    `java-library`
}

description = "Internal module for checking JPMS compliance"

tasks.register("generateModuleInfo") {
    val modules = project.rootProject.subprojects
        .filter { it.hasJavaModule }
        .map { it.javaModuleName() }

    val moduleInfoPath = project.projectDir.absolutePath + "/src/main/java/module-info.java"

    doLast {
        File(moduleInfoPath)
            .apply {
                parentFile.mkdirs()
                createNewFile()
            }
            .bufferedWriter().use { writer ->
                writer.write("module kotlinx.rpc.test.module {\n")
                modules.forEach { writer.write("\trequires $it;\n") }
                writer.write("}")
            }
    }
}

tasks.getByName<JavaCompile>("compileJava") {
    dependsOn("generateModuleInfo")

    val projectFiles = project.files()
    doFirst {
        options.compilerArgs.addAll(listOf("--module-path", classpath.asPath))
        classpath = projectFiles
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    rootProject.subprojects
        .forEach {
            val dep = generateSequence(it) { subProject -> subProject.parent }
                .toList()
                .dropLast(1)
                .reversed()
                .joinToString(":", prefix = ":") { segment -> segment.name }

            it.plugins.withId("maven-publish") {
                if (it.hasJavaModule) {
                    api(project(dep))
                }
            }
        }
}
