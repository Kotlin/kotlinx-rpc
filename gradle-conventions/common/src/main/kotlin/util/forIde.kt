/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.Project

fun Project.whenForIde(block: () -> Unit): ActionApplied {
    val forIdeBuild by rootProject.optionalProperty()

    if (forIdeBuild) {
        block()
        return ActionApplied.Applied
    }

    return ActionApplied.NotApplied
}
