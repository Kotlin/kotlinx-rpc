/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.api.publish.maven.MavenPublication
import util.remapPublicArtifactBaseId
import util.registerNativeDependencyTargets

plugins {
    alias(libs.plugins.conventions.kmp)
    id("conventions-publishing")
}

kotlin {
    registerNativeDependencyTargets()
}

publishing {
    publications.withType(MavenPublication::class).configureEach {
        remapPublicArtifactBaseId(
            project = project,
            publishedBaseId = "kotlinx-rpc-grpc-core-shim-annotation",
        )
    }
}
