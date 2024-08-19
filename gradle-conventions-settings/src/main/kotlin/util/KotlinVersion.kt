/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.provideDelegate
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

fun String.kotlinVersionParsed(): KotlinVersion {
    val (major, minor, patch) = substringBefore('-').split(".").map { it.toInt() }
    return KotlinVersion(major, minor, patch)
}

fun filterSourceDirsForCSM(sourceSetPath: Path): List<File> {
    return filterDirectory(sourceSetPath) {
        Files.isDirectory(it) && it.name().matches(directoryNameRegex)
    }
}

// Versioning is used to sort version-specific source sets in the 'first comes more specific' order
// By 'more specific' we mean that '1.7.10' is more specific than '1.7'.
// So [1.7, 1.7.10, 1.9.10, 1.7.22, 1.9, 1, 1.7.0, latest, 1.8]
// will be sorted as [1.7.0, 1.7.10, 1.7.22, 1.7, 1.8, 1.9.10, 1.9, 1, latest]
// It's ok to have version '1'.
// For example, we may have '1.7' and '1' specific source sets.
// That would mean that all 1.7.* versions we compile with the '1.7' source set,
// and 1.8.+ up to 1.9.25 will be with the '1' source set
class CompilerModuleVersion(fullName: String, prefix: String) : Comparable<CompilerModuleVersion> {
    // For example, "v_1_7_10" -> "1.7.10"
    val version = fullName
        .removePrefix(prefix)
        .replace('_', '.')

    val kotlin by lazy {
        val parts = version.split('.')
            .map { it.toInt() }

        KotlinVersion(parts[0], parts.getOrNull(1) ?: 0, parts.getOrNull(2) ?: 0)
    }

    override fun compareTo(other: CompilerModuleVersion): Int {
        return when {
            version.length == other.version.length -> version.compareTo(other.version)
            version.length < other.version.length -> 1
            else -> -1
        }
    }
}

private fun Collection<File>.sortAndSelectBySemVer(
    prefix: String,
    selector: (CompilerModuleVersion) -> Boolean,
): File? {
    return filter { it.name.startsWith(prefix) }
        .map { it to CompilerModuleVersion(it.name, prefix) }
        .sortedBy { (_, semVer) -> semVer }
        .firstOrNull { (_, semVer) -> selector(semVer) }
        ?.first
}

fun Collection<File>.mostSpecificVersionOrLatest(kotlinVersion: KotlinVersion): File? {
    return mostSpecificByVersionOrNull(kotlinVersion)
        ?: singleOrNull { it.name == Dir.LATEST_SOURCE_DIR }
}

private fun Collection<File>.mostSpecificByVersionOrNull(kotlinVersion: KotlinVersion): File? {
    val (vPrefixed, prePrefixed) = partition { it.name.startsWith("v_") }

    return vPrefixed.sortAndSelectBySemVer("v_") { semVer ->
        kotlinVersion.toString().startsWith(semVer.version)
    } ?: prePrefixed.sortAndSelectBySemVer("pre_") { semVer ->
        kotlinVersion <= semVer.kotlin
    }
}

// matches:
// - latest
// - v_1
// - v_1_9
// - v_1_9_2
// - v_1_9_24
// - pre_1_9_20
// etc.
private val directoryNameRegex = "^(latest|(v|pre)(_\\d){1,3}\\d?)$".toRegex()

data class ActionApplied(val applied: Boolean)

inline fun ExtensionAware.whenKotlinIsAtLeast(
    major: Int,
    minor: Int,
    patch: Int = 0,
    action: () -> Unit,
): ActionApplied {
    val kotlinVersion: KotlinVersion by extra

    if (kotlinVersion.isAtLeast(major, minor, patch)) {
        action()

        return ActionApplied(true)
    }

    return ActionApplied(false)
}

inline fun ExtensionAware.whenKotlinLatest(action: () -> Unit): ActionApplied {
    val isLatestKotlinVersion: Boolean by extra

    if (isLatestKotlinVersion) {
        action()
    }

    return ActionApplied(isLatestKotlinVersion)
}

infix fun ActionApplied.otherwise(body: () -> Unit) {
    if (!applied) {
        body()
    }
}
