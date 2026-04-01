/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

data class CompositeVersion(
    val full: String,
    val base: String,
    val suffix: String,
)

fun Project.catalogVersion(alias: String): String =
    extensions.getByType<VersionCatalogsExtension>()
        .named("libs")
        .findVersion(alias)
        .get()
        .requiredVersion

fun Project.compositeCatalogVersion(alias: String): CompositeVersion {
    val full = catalogVersion(alias)
    val separatorIndex = full.lastIndexOf('-')
    check(separatorIndex > 0 && separatorIndex < full.lastIndex) {
        "Expected '$alias' to use '<base>-<suffix>' version format, but was '$full'"
    }
    return CompositeVersion(
        full = full,
        base = full.substring(0, separatorIndex),
        suffix = full.substring(separatorIndex + 1),
    )
}
