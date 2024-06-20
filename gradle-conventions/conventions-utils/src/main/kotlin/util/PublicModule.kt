/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra

/**
 * See gradle-settings-conventions/src/main/kotlin/includePublic.kt
 */
val Project.isPublicModule: Boolean get() {
    return extra.has("isPublicModule") && extra["isPublicModule"] == true
}
