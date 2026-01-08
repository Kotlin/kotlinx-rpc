/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("LoggingSimilarMessage")

import kotlin.io.path.createDirectories
import kotlin.io.path.writeText

// ADD NEW VERSIONS HERE
val versionDirs = mapOf(
    "v0_8" to CompatVersion("0.8.1", "2.2.0"),
    "v0_9" to CompatVersion("0.9.1", "2.2.0"),
    "v0_10" to CompatVersion("0.10.0", "2.2.20"),
)

// DON'T MODIFY BELOW THIS LINE

class CompatVersion(
    val rpc: String,
    val kotlin: String,
)

// Only run this logic when applied to the main kotlinx-rpc project, not when gradle-conventions-settings builds itself
if (settings.rootProject.name == "kotlinx-rpc") {
    val globalRootDirValue: String = extra["globalRootDir"] as? String
        ?: error("globalRootDir property is not set")

    val globalRootDir: java.nio.file.Path = java.nio.file.Path.of(globalRootDirValue)

    val compatDir: java.nio.file.Path = globalRootDir
        .resolve("tests")
        .resolve("krpc-protocol-compatibility-tests")

    val libVersion = settings.extra["libVersion"] as? String
        ?: error("libVersion property is not set")

    versionDirs.forEach { (dir, version) ->
        val moduleDir = compatDir.resolve(dir)
        moduleDir.createDirectories()
        val buildFile = moduleDir.resolve("build.gradle.kts")
        buildFile.writeText(
            """
            /* THIS FILE IS AUTO-GENERATED, DO NOT EDIT! */
    
            import util.krpc_compat.setupCompat
    
            setupCompat("${version.rpc}", "${version.kotlin}")
    
        """.trimIndent()
        )
        val propertiesFile = moduleDir.resolve("gradle.properties")
        propertiesFile.writeText(
            """
            /* THIS FILE IS AUTO-GENERATED, DO NOT EDIT! */
            
            kotlin.compiler.runViaBuildToolsApi=true
        
        """.trimIndent()
        )

        logger.debug("Generating {}", globalRootDir.relativize(buildFile))
        include(":tests:krpc-protocol-compatibility-tests:$dir")
    }

    val versionsConventionsFile: java.nio.file.Path = globalRootDir
        .resolve("gradle-conventions")
        .resolve("src")
        .resolve("main")
        .resolve("kotlin")
        .resolve("util")
        .resolve("krpc_compat")
        .resolve("versions.kt")

    logger.debug("Generating {}", globalRootDir.relativize(versionsConventionsFile))
    versionsConventionsFile.writeText(
        """
            |/* THIS FILE IS AUTO-GENERATED, DO NOT EDIT! */
            |
            |package util.krpc_compat
            |
            |val krpcCompatVersions = mapOf(
            |    ${versionDirs.entries.joinToString("\n|    ") { "\"${it.key}\" to \"${it.value.rpc}\"," }}
            |
            |    "Latest" to "$libVersion", // current version
            |)
            |
        """.trimMargin()
    )
}
