/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlin.io.path.createDirectories
import kotlin.io.path.writeText

// ADD NEW VERSIONS HERE
val versionDirs = mapOf(
    "v0_8" to CompatVersion("0.8.1", "2.2.0"),
    "v0_9" to CompatVersion("0.9.1", "2.2.0"),
    "v0_10" to CompatVersion("0.10.0", "2.2.20"),
)

// DON'T MODIFY BELOW THIS LINE

val globalRootDirValue: String = extra["globalRootDir"] as? String
    ?: error("globalRootDir property is not set")

val globalRootDir: java.nio.file.Path = java.nio.file.Path.of(globalRootDirValue)

val compatDir: java.nio.file.Path = globalRootDir
    .resolve("tests")
    .resolve("krpc-protocol-compatibility-tests")

val libVersion = settings.extra["libVersion"] as? String
    ?: error("libVersion property is not set")

class CompatVersion(
    val rpc: String,
    val kotlin: String,
)

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

    logger.lifecycle("Generating :tests:krpc-protocol-compatibility-tests:$dir")
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

logger.lifecycle("Generating krpc_compat/versions.kt")
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
