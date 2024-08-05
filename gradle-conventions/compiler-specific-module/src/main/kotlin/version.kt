/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import java.io.File
import java.nio.file.Files
import java.nio.file.Path

fun filterSourceDirs(sourceSetPath: Path): List<File> {
    return Files.newDirectoryStream(sourceSetPath).use { it.toList() }.filter {
        Files.isDirectory(it) && it.name().matches(directoryNameRegex)
    }.map { it.toFile() }
}

// Versioning is used to sort version-specific source sets in the 'first comes more specific' order
// By 'more specific' we mean that '1.7.10' is more specific than '1.7'.
// So [1.7, 1.7.10, 1.9.10, 1.7.22, 1.9, 1, 1.7.0, latest, 1.8]
// will be sorted as [1.7.0, 1.7.10, 1.7.22, 1.7, 1.8, 1.9.10, 1.9, 1, latest]
// It's ok to have version '1'.
// For example, we may have '1.7' and '1' specific source sets.
// That would mean that all 1.7.* versions we compile with the '1.7' source set,
// and 1.8.+ up to 1.9.24 will be with the '1' source set
class CompilerModuleVersion(fullName: String, prefix: String) : Comparable<CompilerModuleVersion> {
    // For example, "v_1_7_10" -> "1.7.10"
    val version = fullName
        .removePrefix(prefix)
        .replace('_', '.')

    override fun compareTo(other: CompilerModuleVersion): Int {
        return when {
            version.length == other.version.length -> version.compareTo(other.version)
            version.length < other.version.length -> 1
            else -> -1
        }
    }
}

fun Collection<File>.mostSpecificVersionOrLatest(kotlinVersion: String): File? {
    return mostSpecificByVersionOrNull(kotlinVersion)
        ?: singleOrNull { it.name == LATEST_SOURCE_DIR }
}

private fun Collection<File>.mostSpecificByVersionOrNull(kotlinVersion: String): File? {
    return map { it to CompilerModuleVersion(it.name, "v_") }
        .sortedBy { (_, semVer) -> semVer }
        .firstOrNull { (_, semVer) ->
            kotlinVersion.startsWith(semVer.version)
        }?.first
}

// matches:
// - latest
// - v_1
// - v_1_9
// - v_1_9_2
// - v_1_9_24
val directoryNameRegex = "^(latest|v(_\\d){1,3}\\d?)$".toRegex()
