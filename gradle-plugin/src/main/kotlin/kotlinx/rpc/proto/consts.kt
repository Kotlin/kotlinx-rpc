/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.proto

import org.gradle.api.Project

public const val PROTO_GROUP: String = "proto"
public const val PROTO_FILES_DIR: String = "proto"
public const val PROTO_BUILD_DIR: String = "protoBuild"
public const val PROTO_SOURCE_DIRECTORY_NAME: String = "proto"
public const val PROTO_SOURCE_SETS: String = "protoSourceSets"

internal val Project.protoBuildDir
    get() =
        layout.buildDirectory
            .dir(PROTO_BUILD_DIR)
            .get()
            .asFile

internal val Project.protoBuildDirSourceSets
    get() = protoBuildDir.resolve("sourceSets")

internal val Project.protoBuildDirGenerated
    get() = protoBuildDir.resolve("generated")
