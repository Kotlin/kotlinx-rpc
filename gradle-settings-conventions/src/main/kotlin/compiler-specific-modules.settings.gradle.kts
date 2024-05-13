/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

object CSM {
    const val SETTINGS_FILE = "settings.gradle.kts"
    const val BUILD_FILE = "build.gradle.kts"
    const val PROPERTIES_FILE = "gradle.properties"

    const val CSM_PROPERTY_NAME = "compiler-specific-module"

    const val KOTLIN_VERSION_EXTRA = "kotlinVersion"
}

// SemVer is used to sort compiler version specific submodules in the 'first comes more specific' order
// By 'more specific' we mean that '1.7.10' is more specific than '1.7'.
// So [1.7, 1.7.10, 1.9.10, 1.7.22, 1.9, 1, 1.7.0, 1.8]
// will be sorted as [1.7.0, 1.7.10, 1.7.22, 1.7, 1.8, 1.9.10, 1.9, 1]
// It's ok to have version '1'. For example, we may have '1.7' and '1' specific modules.
// That would mean, that all 1.7.* versions we compile with '1.7' module, and 1.8.+ up to 1.9.24 will be with '1' module
class CompilerModuleSemVer(fullName: String, prefix: String) : Comparable<CompilerModuleSemVer> {
    // For example, "compiler-plugin-1_7_10" -> "1.7.10"
    val version = fullName
        .removePrefix(prefix)
        .replace('_', '.')

    override fun compareTo(other: CompilerModuleSemVer): Int {
        return when {
            version.length == other.version.length -> version.compareTo(other.version)
            version.length < other.version.length -> 1
            else -> -1
        }
    }
}

// Passed from the `settings-conventions.settings.gradle.kts` plugin
val kotlinVersion = extra[CSM.KOTLIN_VERSION_EXTRA] as? String
    ?: error("Expected `${CSM.KOTLIN_VERSION_EXTRA}` extra property to be provided by `settings-conventions` plugin")

// dir - root dir for compiler specific module
// files - list of all files in that directory
//
// This function includes exactly two submodules of the root compiler specific module:
//   1. core submodule
//   2. version specific submodule that is the best suitable for the current Kotlin compiler version.
//
// IMPORTANT: it is expected that the root submodule is already included to the project,
// otherwise the exception will be thrown
fun includeCSM(dir: File, files: Array<File>) {
    val rootProjectDirName = dir.name

    val submodules = files.filter {
        it.isDirectory && it.name.startsWith(rootProjectDirName)
    }.toMutableSet()

    val core = submodules.singleOrNull { it.name == "$rootProjectDirName-core" }
    if (core == null) {
        error("Compiler Specific Module $rootProjectDirName should have `-core` module defined")
    }
    val compilerSubmodules = submodules - core

    val basePath = dir.absoluteFile
        .relativeTo(settingsDir.absoluteFile).path
        .replace(File.separator, ":")

    includePublic(":$basePath:$rootProjectDirName-core")

    val prefix = "$rootProjectDirName-"

    val currentCompilerModuleDirName = compilerSubmodules
        .map { it.name to CompilerModuleSemVer(it.name, prefix) }
        // example after sorted: [1.7.0, 1.7.10, 1.7.22, 1.7, 1.8, 1.9.10, 1.9, 1]
        .sortedBy { (_, semVer) -> semVer }
        .firstOrNull { (_, semVer) ->
            kotlinVersion.startsWith(semVer.version)
        }?.first
        ?: error("""
            Unable to find compiler specific submodule for $rootProjectDirName and Kotlin $kotlinVersion.
            Available modules: ${compilerSubmodules.joinToString { it.name }} 
        """.trimIndent())

    includePublic(":$basePath:$currentCompilerModuleDirName")

    val rootProjectName = "$KOTLINX_RPC_PREFIX$rootProjectDirName"
    gradle.projectsLoaded {
        rootProject.subprojects.find { it.name == rootProjectName }
            ?: error("Expected root project '$rootProjectName' to be included to the build manually")
    }
}

// search through all gradle modules for compiler specific ones
//
// IMPORTANT: compiler specific modules MUST NOT have any nested modules other than core and version specific ones,
// otherwise, they will be skipped
settingsDir.walkTopDown().onEnter { dir ->
    if (dir == settingsDir) {
        return@onEnter true
    }

    val isExcluded = dir.name.run {
        startsWith(".") ||
                startsWith("build") ||
                startsWith("src") ||
                equals("gradle") ||
                equals("kotlin-js-store") ||
                equals("karma") ||
                equals("gradle") ||
                equals("sample")
    }

    if (isExcluded) {
        return@onEnter false
    }

    val files = dir.listFiles() ?: return@onEnter false
    val propertiesFile = files.singleOrNull { it.name == CSM.PROPERTIES_FILE }

    when {
        files.any { it.name == CSM.SETTINGS_FILE } -> false // standalone projects are excluded

        // check if it is a gradle module
        propertiesFile != null && files.find { it.name == CSM.BUILD_FILE } != null -> {
            val isCompilerSpecificModule = propertiesFile.readLines(Charsets.UTF_8)
                .singleOrNull { it.startsWith(CSM.CSM_PROPERTY_NAME) }
                ?.substringAfter("=")
                ?.substringBefore("#") // avoid comments
                ?.trim()
                ?.toBoolean()
                ?: return@onEnter true // continue traversing

            if (isCompilerSpecificModule) {
                includeCSM(dir, files)
            }

            // proper CS module or not, there should not be any nested modules
            false
        }

        else -> true // continue traversing
    }
}.toList()
