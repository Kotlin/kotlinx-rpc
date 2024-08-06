/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import java.nio.file.Files
import java.nio.file.Path

val kotlinVersion: String by extra

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

        val core = sourceSetPath.resolve(CORE_SOURCE_DIR).toFile()

        // version-specific source sets
        val vsSets = filterSourceDirs(sourceSetPath)

        // choose 'latest' if there are no more specific ones
        val mostSpecificApplicable = vsSets.mostSpecificVersionOrLatest(kotlinVersion)
            ?: run {
                logger.info("No version specific sources sets, but '${core.name}'")
                set.kotlin.setSrcDirs(listOf(core)) // 'core' source set instead of 'kotlin'
                set.configureResources(sourceSetPath)
                return@forEach
            }

        logger.info(
            "${project.name}: included version specific source sets: " +
                    "${core.name}, ${mostSpecificApplicable.name}"
        )

        set.kotlin.setSrcDirs(listOf(core, mostSpecificApplicable)) // 'core' source set instead of 'kotlin'
        set.configureResources(sourceSetPath)

        val excluded = vsSets.filter { it != mostSpecificApplicable }
        logger.info("${project.name}: excluded version specific source sets: [${excluded.joinToString { it.name }}]")
    }
}

fun KotlinSourceSet.configureResources(sourceSetPath: Path) {
    val parent = sourceSetPath.parent.toAbsolutePath()
    if (!Files.exists(parent)) {
        error("Expected parent dir for ${sourceSetPath.toAbsolutePath()}")
    }

    // only works for jvm projects
    val resourcesName = if (name.lowercase().contains(MAIN_SOURCE_SET)) MAIN_RESOURCES else TEST_RESOURCES
    val resourcesDir = parent.resolve(resourcesName)

    if (!Files.exists(resourcesDir)) {
        return
    }

    val mostSpecificApplicable = filterSourceDirs(resourcesDir)
        .mostSpecificVersionOrLatest(kotlinVersion)

    val versionNames = listOfNotNull(CORE_SOURCE_DIR, mostSpecificApplicable?.name)

    resources.srcDirs(versionNames.map { resourcesDir.resolve(it).toFile() })

    // 'resources' property does not work alone in gradle 7.5.1 with kotlin 1.7.* and 1.8.* (no idea why),
    // so we adjust task contents as well
    if (kotlinVersion.startsWith("1.8") || kotlinVersion.startsWith("1.7")) {
        // only works for jvm projects
        val resourcesTaskName = if (name == MAIN_SOURCE_SET) PROCESS_RESOURCES else PROCESS_TEST_RESOURCES
        tasks.withType<ProcessResources>().configureEach {
            if (name != resourcesTaskName) {
                return@configureEach
            }

            duplicatesStrategy = DuplicatesStrategy.EXCLUDE

            from(versionNames.map { resourcesDir.resolve(it) })
            include {
                // double check if the files are the right ones
                it.file.toPath().toAbsolutePath().startsWith(parent)
            }
        }
    }
}

plugins.withId(KOTLIN_JVM_PLUGIN_ID) {
    the<KotlinJvmProjectExtension>().sourceSets.applyCompilerSpecificSourceSets()
}

plugins.withId(KOTLIN_MULTIPLATFORM_PLUGIN_ID) {
    the<KotlinMultiplatformExtension>().sourceSets.applyCompilerSpecificSourceSets()
}
