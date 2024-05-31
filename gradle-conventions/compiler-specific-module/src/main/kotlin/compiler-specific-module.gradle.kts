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

        val core = sourceSetPath.resolve(Const.CORE_SOURCE_SET).toFile()

        // version-specific source sets
        val vsSets = Files.newDirectoryStream(sourceSetPath).use { it.toList() }.filter {
            Files.isDirectory(it) && it.name().matches(directoryNameRegex)
        }.map { it.toFile() }

        // choose 'latest' if there are no more specific ones
        val mostSpecificApplicable = vsSets.mostSpecificByVersionOrNull(kotlinVersion)
            ?: vsSets.singleOrNull { it.name == Const.LATEST_SOURCE_SET }
            ?: run {
                logger.info("No version specific sources sets, but '${core.name}'")
                set.kotlin.setSrcDirs(listOf(core)) // 'core' source set instead of 'kotlin'
                set.configureResources(sourceSetPath, core.name)
                return@forEach
            }

        logger.info(
            "${project.name}: included version specific source sets: " +
                    "${core.name}, ${mostSpecificApplicable.name}"
        )

        set.kotlin.setSrcDirs(listOf(core, mostSpecificApplicable)) // 'core' source set instead of 'kotlin'
        set.configureResources(sourceSetPath, core.name, mostSpecificApplicable.name)

        val excluded = vsSets.filter { it != mostSpecificApplicable }
        logger.info("${project.name}: excluded version specific source sets: [${excluded.joinToString { it.name }}]")
    }
}

fun KotlinSourceSet.configureResources(sourceSetPath: Path, vararg versionNames: String) {
    val vsResources = sourceSetPath.resolve(Const.RESOURCES).toFile()
    resources.setSrcDirs(
        versionNames.map { vsResources.resolve(it) }
    )

    // 'resources' property does not work alone in gradle 7.5.1 with kotlin 1.7.0 (no idea why),
    // so we adjust task contents as well
    tasks.withType<ProcessResources>().configureEach {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        from(versionNames.map { vsResources.resolve(it) })
        include { it.file.parentInAllowList(versionNames) }
    }
}

fun File.parentInAllowList(allowList: Array<out String>): Boolean {
    val parent = toPath().parent?.toFile()
    // will skip v_1_7 for 1.7.0, as it's parent is resources
    // but will allow META-INF, as it's parent is v_1_7
    if (parent?.name in allowList) {
        return true
    }

    // allow META-INF contents
    return untilAllowedParentOrNull(allowList) != null
}

tailrec fun File.untilAllowedParentOrNull(allowList: Array<out String>): File? {
    if (name in allowList) {
        return null
    }

    val parent = toPath().parent?.toFile()
    return if (parent?.name in allowList) this else parent?.untilAllowedParentOrNull(allowList)
}

plugins.withId(Const.KOTLIN_JVM_PLUGIN_ID) {
    the<KotlinJvmProjectExtension>().sourceSets.applyCompilerSpecificSourceSets()
}

plugins.withId(Const.KOTLIN_MULTIPLATFORM_PLUGIN_ID) {
    the<KotlinMultiplatformExtension>().sourceSets.applyCompilerSpecificSourceSets()
}

