/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import java.io.File
import java.nio.file.Files
import java.nio.file.Path

plugins {
    alias(libs.plugins.gradle.kotlin.dsl)
}

fun Path.name() = fileName?.toString().orEmpty()

fun filterDirectory(sourceSetPath: Path, filter: (Path) -> Boolean): List<File> {
    return Files.newDirectoryStream(sourceSetPath).use { it.toList() }.filter(filter).map { it.toFile() }
}

val pluginsSource: Path = layout.projectDirectory.dir("../develocity/src/main/kotlin").asFile.toPath()

val plugins = filterDirectory(pluginsSource) {
    Files.isRegularFile(it) && it.name().endsWith(".settings.gradle.kts")
}.map { it.name.substringBefore('.') }

plugins.forEach { name ->
    gradlePlugin {
        plugins {
            create(name) {
                id = name
                implementationClass = "EmptySettingsPlugin"
            }
        }

        logger.info("Applied $name precompiled plugin as stub")
    }
}
