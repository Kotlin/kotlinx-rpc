/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

rootProject.name = "<test-name>"

buildCache {
    local {
        directory = "<build-cache-dir>"
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("<build-repo>")
    }
}
