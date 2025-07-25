/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.proto

import kotlinx.rpc.util.ensureDirectoryExists
import kotlinx.rpc.util.ensureRegularFileExists
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import java.io.File
import java.nio.file.Files

/**
 * Copy proto files to a temporary directory for Buf to process.
 */
public abstract class ProcessProtoFiles : Copy() {
    init {
        group = PROTO_GROUP
    }
}

internal fun Project.registerProcessProtoFilesTask(
    name: String,
    destination: File,
    protoFiles: SourceDirectorySet,
    configure: ProcessProtoFiles.() -> Unit = {},
): TaskProvider<ProcessProtoFiles> {
    val capitalName = name.replaceFirstChar { it.uppercase() }

    return tasks.register<ProcessProtoFiles>("process${capitalName}ProtoFiles") {
        val allFiles = protoFiles.files

        // this task deletes the destination directory if it results in NO-SOURCE,
        // which breaks the configuration cache for bufGenerate
        // so we prevent No-SOURCE by creating a .keep file in the destination directory
        val keep = protoBuildDirSourceSetsKeep.ensureRegularFileExists()
        from(keep)

        from(protoFiles.srcDirs) {
            include {
                it.file in allFiles
            }
        }

        into(destination)

        doFirst {
            destination.deleteRecursively()
        }

        configure()
    }
}
