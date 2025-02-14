/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path

fun Path.bufferedReader(
    charset: Charset = Charsets.UTF_8,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    vararg options: OpenOption,
): BufferedReader {
    return BufferedReader(
        InputStreamReader(
            Files.newInputStream(this, *options),
            charset
        ),
        bufferSize
    )
}

object SettingsConventions {
    const val KOTLIN_VERSION_ENV_VAR_NAME = "KOTLIN_VERSION"
    const val KOTLIN_COMPILER_VERSION_ENV_VAR_NAME = "KOTLIN_COMPILER_VERSION"
    const val LIBRARY_VERSION_ENV_VAR_NAME = "LIBRARY_VERSION"
    const val EAP_VERSION_ENV_VAR_NAME = "EAP_VERSION"

    const val LIBRARY_CORE_VERSION_ALIAS = "kotlinx-rpc"
    const val KOTLIN_VERSION_ALIAS = "kotlin-lang"
    const val KOTLIN_COMPILER_VERSION_ALIAS = "kotlin-compiler"

    const val VERSIONS_SECTION_NAME = "[versions]"

    const val VERSIONS_ROOT_PATH = "versions-root"
    const val LIBS_VERSION_CATALOG_PATH = "$VERSIONS_ROOT_PATH/libs.versions.toml"
    const val KOTLIN_VERSIONS_LOOKUP_PATH = "$VERSIONS_ROOT_PATH/kotlin-versions-lookup.csv"
}

// ### VERSION RESOLVING SECTION ###
// This sections of the plugin is responsible for setting properer libraries versions in the project
// according to the current Kotlin version that should be used.

// This plugin can be applied in different subprojects,
// so we need a way to find global project root for kotlinx.rpc project
// to be able to resolve 'versions-root/libs.versions.toml' and other files
fun findGlobalRootDirPath(start: Path): Path {
    var path = start

    // we assume that VERSIONS_ROOT_PATH folder can only be present in the root folder
    while (
        Files.newDirectoryStream(path).use { it.toList() }.none {
            Files.isDirectory(it) && it.fileName.toString() == SettingsConventions.VERSIONS_ROOT_PATH
        }
    ) {
        path = path.parent ?: error("Unable to find root path for kotlinx.rpc project")
    }

    gradle.rootProject {
        allprojects {
            this.extra["globalRootDir"] = path.toAbsolutePath().toString()
        }
    }

    return path
}

// Resolves 'versions-root/kotlin-versions-lookup.csv'
fun loadLookupTable(rootDir: Path, kotlinVersion: String): Pair<Map<String, String>, String> {
    val file = rootDir.resolve(SettingsConventions.KOTLIN_VERSIONS_LOOKUP_PATH).toFile()

    var latest = kotlinVersion
    val table = file.readText()
        .split("\n")
        .takeIf { it.size >= 2 }
        ?.run {
            first().asCsvValues() to when (val versionsRow = singleOrNull { it.startsWith(kotlinVersion) }) {
                // resolve latest for an unknown version
                // considers that unknown versions are too new and not yet added
                null -> {
                    latest = kotlinVersion
                    get(1).asCsvValues()
                }

                else -> {
                    latest = get(1).substringBefore(',')
                    versionsRow.asCsvValues()
                }
            }
        }
        ?.takeIf { (keys, values) -> keys.size == values.size }
        ?.let { (keys, values) ->
            keys.zip(values)
        }?.associate { it }
        ?: error(
            "Malformed Kotlin version in lookup table, " +
                    "should be proper CSV file with horizontal header of version names " +
                    "and vertical headers of Kotlin versions."
        )

    return table to latest
}

fun String.asCsvValues(): List<String> {
    return split(",").map { it.trim() }.drop(1)
}

// Resolves [versions] section from 'versions-root/libs.versions.toml' into map
//
// NOTE: I would love to use tomlj parser here, but I could not import it :(
fun resolveVersionCatalog(rootDir: Path): Map<String, String> {
    var versionsStarted = false
    val map = mutableMapOf<String, String>()

    rootDir.resolve(SettingsConventions.LIBS_VERSION_CATALOG_PATH).bufferedReader().use { reader ->
        while (true) {
            val line = reader.readLine() ?: break

            if (versionsStarted) {
                // versions section ended
                if (line.startsWith("[")) {
                    break
                }

                // remove comments
                val versionEntry = line.substringBefore("#").trim()
                if (versionEntry.isNotEmpty()) {
                    val (key, value) = versionEntry.split("=").map {
                        it.trim { ch -> ch == '"' || ch.isWhitespace() }
                    }
                    map[key] = value
                }
            } else {
                if (line.startsWith(SettingsConventions.VERSIONS_SECTION_NAME)) {
                    versionsStarted = true
                }
            }
        }
    }

    return map
}

// Uses KOTLIN_VERSION env var if present for project's Kotlin version and sets it into Version Catalog.
// Otherwise uses version from catalog.
fun VersionCatalogBuilder.resolveKotlinVersion(versionCatalog: Map<String, String>): Pair<String, String> {
    var kotlinCatalogVersion: String? = System.getenv(SettingsConventions.KOTLIN_VERSION_ENV_VAR_NAME)
    var kotlinCompilerVersion: String? = System.getenv(SettingsConventions.KOTLIN_COMPILER_VERSION_ENV_VAR_NAME)

    if (kotlinCatalogVersion != null) {
        version(SettingsConventions.KOTLIN_VERSION_ALIAS, kotlinCatalogVersion)
    } else {
        kotlinCatalogVersion = versionCatalog[SettingsConventions.KOTLIN_VERSION_ALIAS]
    }

    var catalogCompilerVersion = versionCatalog[SettingsConventions.KOTLIN_COMPILER_VERSION_ALIAS]
    if (kotlinCompilerVersion != null) {
        logger.info("Resolved Kotlin compiler version: $kotlinCompilerVersion")
        version(SettingsConventions.KOTLIN_COMPILER_VERSION_ALIAS, kotlinCompilerVersion)
    } else if (catalogCompilerVersion == "0.0.0") {
        kotlinCompilerVersion = kotlinCatalogVersion!!
        version(SettingsConventions.KOTLIN_COMPILER_VERSION_ALIAS, kotlinCompilerVersion)
    } else {
        kotlinCompilerVersion = catalogCompilerVersion
    }

    val resolvedLang = kotlinCatalogVersion
        ?: error("Expected to resolve '${SettingsConventions.KOTLIN_VERSION_ALIAS}' version")

    val resolvedCompiler = kotlinCompilerVersion
        ?: error("Expected to resolve '${SettingsConventions.KOTLIN_COMPILER_VERSION_ALIAS}' version")

    return resolvedLang to resolvedCompiler
}

// Resolves a core kotlinx.rpc version (without a Kotlin version prefix) from the Version Catalog.
// Uses LIBRARY_VERSION_ENV_VAR_NAME instead if present
fun VersionCatalogBuilder.resolveLibraryVersion(versionCatalog: Map<String, String>) {
    val libraryCoreVersion: String = System.getenv(SettingsConventions.LIBRARY_VERSION_ENV_VAR_NAME)
        ?.takeIf { it.isNotBlank() }
        ?: versionCatalog[SettingsConventions.LIBRARY_CORE_VERSION_ALIAS]
        ?: error("Expected to resolve '${SettingsConventions.LIBRARY_CORE_VERSION_ALIAS}' version")

    val eapVersion: String = System.getenv(SettingsConventions.EAP_VERSION_ENV_VAR_NAME)
        ?.let {
            when {
                it.isBlank() -> ""
                else -> "-$it"
            }
        } ?: ""

    val resultingVersion = when (eapVersion) {
        "" -> libraryCoreVersion
        else -> libraryCoreVersion.substringBefore('-') + eapVersion
    }

    version(SettingsConventions.LIBRARY_CORE_VERSION_ALIAS, resultingVersion)
}

fun String.kotlinVersionParsed(): KotlinVersion {
    val (major, minor, patch) = substringBefore('-').split(".").map { it.toInt() }
    return KotlinVersion(major, minor, patch)
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            val currentPath = file(".").toPath().toAbsolutePath()
            val rootDir = findGlobalRootDirPath(currentPath)

            from(files("$rootDir/${SettingsConventions.LIBS_VERSION_CATALOG_PATH}"))

            val versionCatalog = resolveVersionCatalog(rootDir)

            val (kotlinVersion, compilerVersion) = resolveKotlinVersion(versionCatalog)

            resolveLibraryVersion(versionCatalog)

            // Other Kotlin-dependant versions
            val (lookupTable, latestKotlin) = loadLookupTable(rootDir, kotlinVersion)

            val isLatestKotlin = latestKotlin == kotlinVersion

            extra["kotlinVersion"] = kotlinVersion.kotlinVersionParsed()
            extra["kotlinCompilerVersion"] = compilerVersion.kotlinVersionParsed()
            extra["isLatestKotlinVersion"] = isLatestKotlin

            gradle.rootProject {
                allprojects {
                    this.extra["kotlinVersion"] = kotlinVersion.kotlinVersionParsed()
                    this.extra["kotlinCompilerVersion"] = compilerVersion.kotlinVersionParsed()
                    this.extra["isLatestKotlinVersion"] = isLatestKotlin
                }
            }

            logger.info("Resolved compiler specific dependencies versions (Kotlin $kotlinVersion):")
            lookupTable.forEach { (name, version) ->
                logger.info("$name -> $version")
                version(name, version)
            }
        }
    }
}
