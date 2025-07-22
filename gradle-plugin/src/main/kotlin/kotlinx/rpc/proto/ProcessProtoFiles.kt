/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.proto

import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import java.io.File

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
    baseGenDir: Provider<File>,
    protoFiles: SourceDirectorySet,
    toDir: String,
    configure: ProcessProtoFiles.() -> Unit = {},
): TaskProvider<ProcessProtoFiles> {
    val capitalName = name.replaceFirstChar { it.uppercase() }

    return tasks.register<ProcessProtoFiles>("process${capitalName}ProtoFiles") {
        val protoGenDir = baseGenDir.map { it.resolve(toDir) }

        val allFiles = protoFiles.files

        from(protoFiles.srcDirs) {
            include {
                it.file in allFiles
            }
        }

        into(protoGenDir)

        doFirst {
            protoGenDir.get().deleteRecursively()
        }

        configure()
    }
}
