/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.proto

import org.gradle.api.Project
import java.io.File

internal val Project.protoBuildDir
    get() =
        layout.buildDirectory
            .dir(PROTO_BUILD_DIR)
            .get()
            .asFile

internal val Project.protoBuildDirSourceSets: File
    get() {
        return protoBuildDir.resolve(PROTO_BUILD_SOURCE_SETS)
    }

internal val Project.protoBuildDirSourceSetsKeep: File
    get() {
        return protoBuildDirSourceSets.resolve(".keep")
    }

internal val Project.protoBuildDirGenerated: File
    get() {
        return protoBuildDir.resolve(PROTO_BUILD_GENERATED)
    }
