/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.other

import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import util.withKotlinJvmExtension
import util.withKotlinKmpExtension
import java.io.File
import javax.inject.Inject

abstract class GenerateSourceTask @Inject constructor(
    @get:Input val filename: String,
    @get:Input val text: String,
    @get:OutputDirectory val sourcesDir: File,
) : DefaultTask() {
    @TaskAction
    fun generate() {
        val sourceFile = File(sourcesDir, "${filename}.kt")
        sourceFile.writeText("// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!")
        sourceFile.appendText(System.lineSeparator())
        sourceFile.appendText("// Gradle task: $name")
        sourceFile.appendText(System.lineSeparator())
        sourceFile.appendText(System.lineSeparator())
        sourceFile.appendText(text)
    }
}

fun Project.generateSource(
    name: String,
    text: String,
    chooseSourceSet: NamedDomainObjectContainer<KotlinSourceSet>.() -> NamedDomainObjectProvider<KotlinSourceSet>,
) {
    withKotlinJvmExtension {
        generateSource(sourceSets, name, text, chooseSourceSet)
    }

    withKotlinKmpExtension {
        generateSource(sourceSets, name, text, chooseSourceSet)
    }
}

private fun Project.generateSource(
    sourceSets: NamedDomainObjectContainer<KotlinSourceSet>,
    name: String,
    text: String,
    chooseSourceSet: NamedDomainObjectContainer<KotlinSourceSet>.() -> NamedDomainObjectProvider<KotlinSourceSet>,
) {
    val sourceSet = chooseSourceSet(sourceSets)
    val sourcesDir = File(project.layout.buildDirectory.asFile.get(), "generated-sources/kotlin/${sourceSet.name}")

    val generatePluginVersionTask =
        tasks.register<GenerateSourceTask>("generateSources_$name", name, text, sourcesDir)

    sourceSet.configure {
        kotlin.srcDir(generatePluginVersionTask.map { it.sourcesDir })
    }
}
