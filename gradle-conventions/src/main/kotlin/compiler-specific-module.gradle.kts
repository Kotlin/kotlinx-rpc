/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import util.*
import java.nio.file.Files
import java.nio.file.Path
import kotlin.text.lowercase

val kotlinCompilerVersion: KotlinVersion by extra
val preserveDefaultSourceDirectories by optionalProperty()

fun NamedDomainObjectContainer<KotlinSourceSet>.applyCompilerSpecificSourceSets() {
    forEach { set ->
        val sourceDirs = set.kotlin.sourceDirectories.toList()
        val path = sourceDirs.firstOrNull() // one is ok in most cases because we need its parent directory
            ?: error(
                "Expected at least one source set dir for '${set.name}' source set (kotlin dir). " +
                        "Review the case and adjust the script"
            )

        val sourceSetPath = path.toPath().parent
        if (!Files.exists(sourceSetPath)) {
            return@forEach
        }

        val core = sourceSetPath.resolve(Dir.CORE_SOURCE_DIR).toFile()

        // version-specific source sets
        val vsSets = filterSourceDirsForCSM(sourceSetPath)

        // choose 'latest' if there are no more specific ones
        val mostSpecificApplicable = vsSets.mostSpecificVersionOrLatest(kotlinCompilerVersion)

        logger.lifecycle(
            "${project.name}: included version specific source sets: " +
                    "[${core.name}${mostSpecificApplicable?.let { ", ${it.name}" } ?: ""}]"
        )

        val newSourceDirectories = listOfNotNull(core, mostSpecificApplicable)

        if (preserveDefaultSourceDirectories) {
            set.kotlin.srcDirs(newSourceDirectories)
        } else {
            set.kotlin.setSrcDirs(newSourceDirectories) // 'core' source set instead of 'kotlin'
        }

        set.configureResources(sourceSetPath)

        val excluded = vsSets.filter { it != mostSpecificApplicable }
        logger.lifecycle(
            "${project.name}: excluded version specific source sets: [${excluded.joinToString { it.name }}]"
        )
    }
}

fun KotlinSourceSet.configureResources(sourceSetPath: Path) {
    val parent = sourceSetPath.parent.toAbsolutePath()
    if (!Files.exists(parent)) {
        error("Expected parent dir for ${sourceSetPath.toAbsolutePath()}")
    }

    // only works for jvm projects
    val resourcesName = if (name.lowercase().contains(Dir.MAIN_SOURCE_SET)) Dir.MAIN_RESOURCES else Dir.TEST_RESOURCES
    val resourcesDir = parent.resolve(resourcesName)

    if (!Files.exists(resourcesDir)) {
        return
    }

    val mostSpecificApplicable = filterSourceDirsForCSM(resourcesDir)
        .mostSpecificVersionOrLatest(kotlinCompilerVersion)

    val versionNames = listOfNotNull(Dir.CORE_SOURCE_DIR, mostSpecificApplicable?.name)

    resources.srcDirs(versionNames.map { resourcesDir.resolve(it).toFile() })
}

withKotlinJvmExtension {
    sourceSets.applyCompilerSpecificSourceSets()
}

withKotlinKmpExtension {
    sourceSets.applyCompilerSpecificSourceSets()
}
