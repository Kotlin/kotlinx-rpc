/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("unused", "detekt.MissingPackageDeclaration")

import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.extra

/**
 * Includes a project by the given Gradle path and marks it as a public.
 */
fun Settings.includePublic(projectPath: String) {
    include(projectPath)

    gradle.rootProject {
        project(projectPath).extra["isPublicModule"] = true
    }
}

/**
 * Marks root project as public
 */
fun Settings.includeRootAsPublic() {
    gradle.rootProject {
        extra["isPublicModule"] = true
    }
}
