/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.filterDirectory
import util.name
import java.nio.file.Files
import java.nio.file.Path

plugins {
    alias(libs.plugins.gradle.kotlin.dsl)
}

val pluginsSource: Path = layout.projectDirectory.dir("../latest-only/src/main/kotlin").asFile.toPath()

val plugins = filterDirectory(pluginsSource) {
    Files.isRegularFile(it) && it.name().endsWith(".gradle.kts")
}.map { it.name.substringBefore('.') }

plugins.forEach { name ->
    gradlePlugin {
        plugins {
            create(name) {
                id = name
                implementationClass = "EmptyPlugin"
            }
        }

        logger.info("Applied $name precompiled plugin as stub")
    }
}
