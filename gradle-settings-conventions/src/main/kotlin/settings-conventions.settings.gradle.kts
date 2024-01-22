/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

fun Path.bufferedReader(
    charset: Charset = Charsets.UTF_8,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    vararg options: OpenOption
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
    const val EAP_VERSION_ENV_VAR_NAME = "EAP_VERSION"

    const val KSP_VERSION_ALIAS = "ksp"
    const val KRPC_CORE_VERSION_ALIAS = "krpc-core"
    const val KRPC_FULL_VERSION_ALIAS = "krpc-full"
    const val KOTLIN_VERSION_ALIAS = "kotlin-lang"

    const val VERSIONS_SECTION_NAME = "[versions]"

    const val GRADLE_WRAPPER_FOLDER = "gradle"
    const val LIBS_VERSION_CATALOG_PATH = "$GRADLE_WRAPPER_FOLDER/libs.versions.toml"
    const val KOTLIN_VERSIONS_LOOKUP_PATH = "$GRADLE_WRAPPER_FOLDER/kotlin-versions-lookup.csv"
}

// ### VERSION RESOLVING SECTION ###
// This sections of the plugin is responsible for setting properer libraries versions in the project
// according to the current Kotlin version that should be used.

// This plugin can be applied in different subprojects, so we need a way to find global project root for kRPC project
// to be able to resolve 'gradle/libs.versions.toml' and other files
fun findGlobalRootDirPath(start: Path, onDir: () -> Unit = {}): Path {
    var path = start

    // we assume that `gradle` folder can only be present in the root folder
    while (
        Files.newDirectoryStream(path).use { it.toList() }.none {
            Files.isDirectory(it) && it.fileName.toString() == SettingsConventions.GRADLE_WRAPPER_FOLDER
        }
    ) {
        path = path.parent ?: error("Unable to find root path for kRPC project")
        onDir()
    }

    gradle.rootProject {
        allprojects {
            this.extra["globalRootDir"] = path.toAbsolutePath().toString()
        }
    }

    return path
}

// Resolves 'gradle/kotlin-versions-lookup.csv'
fun loadLookupTable(rootDir: Path, kotlinVersion: String): Map<String, String> {
    val file = rootDir.resolve(SettingsConventions.KOTLIN_VERSIONS_LOOKUP_PATH).toFile()

    return file.readText()
        .split("\n")
        .takeIf { it.size >= 2 }
        ?.run {
            when (val versionsRow = singleOrNull { it.startsWith(kotlinVersion) }) {
                null -> null
                else -> first().asCsvValues() to versionsRow.asCsvValues()
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
}

fun String.asCsvValues(): List<String> {
    return split(",").map { it.trim() }.drop(1)
}

// Resolves [versions] section from 'gradle/libs.versions.toml' into map
//
// NOTE: I would love to use tomlj parser here, but I could import it :(
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
fun VersionCatalogBuilder.resolveKotlinVersion(versionCatalog: Map<String, String>): String {
    var kotlinCatalogVersion: String? = System.getenv(SettingsConventions.KOTLIN_VERSION_ENV_VAR_NAME)

    if (kotlinCatalogVersion != null) {
        version(SettingsConventions.KOTLIN_VERSION_ALIAS, kotlinCatalogVersion)
    } else {
        kotlinCatalogVersion = versionCatalog[SettingsConventions.KOTLIN_VERSION_ALIAS]
    }

    return kotlinCatalogVersion
        ?: error("Expected to resolve '${SettingsConventions.KOTLIN_VERSION_ALIAS}' version")
}

// Resolves core krpc version (without Kotlin version prefix) from Versions Catalog.
// Updates it with EAP_VERSION suffix of present.
// Sets krpc full version (with Kotlin version prefix) into Version Catalog.
fun VersionCatalogBuilder.resolveKrpcVersion(versionCatalog: Map<String, String>, kotlinVersion: String) {
    val eapVersion: String = System.getenv(SettingsConventions.EAP_VERSION_ENV_VAR_NAME)
        ?.let { "-eap-$it" } ?: ""
    val krpcCatalogVersion = versionCatalog[SettingsConventions.KRPC_CORE_VERSION_ALIAS]
        ?: error("Expected to resolve '${SettingsConventions.KRPC_CORE_VERSION_ALIAS}' version")
    val krpcVersion = krpcCatalogVersion + eapVersion
    val krpcFullVersion = "$kotlinVersion-$krpcVersion"

    version(SettingsConventions.KRPC_CORE_VERSION_ALIAS, krpcVersion)
    version(SettingsConventions.KRPC_FULL_VERSION_ALIAS, krpcFullVersion)
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            var isRoot = true
            val currentPath = file(".").toPath().toAbsolutePath()

            val rootDir = findGlobalRootDirPath(currentPath) { isRoot = false }

            // avoid "Problem: In version catalog libs, you can only call the 'from' method a single time."
            if (!isRoot) {
                from(files("$rootDir/${SettingsConventions.LIBS_VERSION_CATALOG_PATH}"))
            }

            val versionCatalog = resolveVersionCatalog(rootDir)

            val kotlinVersion = resolveKotlinVersion(versionCatalog)

            extra["kotlinVersion"] = kotlinVersion

            resolveKrpcVersion(versionCatalog, kotlinVersion)

            // Other Kotlin-dependant versions 
            val lookupTable = loadLookupTable(rootDir, kotlinVersion)

            logger.info("Resolved compiler specific dependencies versions (Kotlin $kotlinVersion):")
            lookupTable.forEach { (name, version) ->
                val fullVersion = when (name) {
                    SettingsConventions.KSP_VERSION_ALIAS -> {
                        "$kotlinVersion-${version}"
                    }

                    else -> {
                        version
                    }
                }

                logger.info("$name -> $fullVersion")
                version(name, fullVersion)
            }
        }
    }
}

// ### OTHER SETTINGS SECTION ###

gradle.rootProject {
    allprojects {
        buildscript {
            repositories {
                gradlePluginPortal()
                mavenCentral()
            }
        }

        repositories {
            mavenCentral()
        }
    }
}
