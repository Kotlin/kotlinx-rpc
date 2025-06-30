/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.provideDelegate
import util.other.ActionApplied

fun String.kotlinVersionParsed(): KotlinVersion {
    val (major, minor, patch) = substringBefore('-').split(".").map { it.toInt() }
    return KotlinVersion(major, minor, patch)
}

@Suppress("unused")
inline fun ExtensionAware.whenKotlinCompilerIsAtLeast(
    major: Int,
    minor: Int,
    patch: Int = 0,
    action: () -> Unit = {},
): ActionApplied {
    val kotlinCompilerVersion: KotlinVersion by extra

    if (kotlinCompilerVersion.isAtLeast(major, minor, patch)) {
        action()

        return ActionApplied.Applied
    }

    return ActionApplied.NotApplied
}
