/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.provideDelegate


enum class ActionApplied {
    Applied, NotApplied;
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

@Suppress("unused")
inline fun ExtensionAware.whenKotlinLatest(action: () -> Unit): ActionApplied {
    val isLatestKotlinVersion: Boolean by extra

    if (isLatestKotlinVersion) {
        action()
        return ActionApplied.Applied
    }

    return ActionApplied.NotApplied
}

infix fun ActionApplied.otherwise(body: () -> Unit) {
    if (this == ActionApplied.NotApplied) {
        body()
    }
}
