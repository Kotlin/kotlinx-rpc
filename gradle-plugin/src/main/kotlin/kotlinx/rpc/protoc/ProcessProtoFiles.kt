/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc

import kotlinx.rpc.util.ensureRegularFileExists
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
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
    destination: File,
    protoFiles: SourceDirectorySet,
    configure: ProcessProtoFiles.() -> Unit = {},
): TaskProvider<ProcessProtoFiles> {
    val capitalName = name.replaceFirstChar { it.uppercase() }

    return tasks.register<ProcessProtoFiles>("process${capitalName}ProtoFiles") {
        // this task deletes the destination directory if it results in NO-SOURCE,
        // which breaks the configuration cache for bufGenerate
        // so we prevent NO-SOURCE by creating a .keep file in the destination directory
        val keep = protoBuildDirSourceSetsKeep.ensureRegularFileExists()
        from(keep)

        from(files(protoFiles.sourceDirectories)) {
            include(protoFiles.includes)
            exclude(protoFiles.excludes)
        }

        into(destination)

        doFirst {
            destination.deleteRecursively()
        }

        configure()
    }
}

internal fun Project.registerProcessProtoFilesImportsTask(
    name: String,
    destination: File,
    importsProvider: Provider<List<DefaultProtoSourceSet>>,
    configure: ProcessProtoFiles.() -> Unit = {},
): TaskProvider<ProcessProtoFiles> {
    val capitalName = name.replaceFirstChar { it.uppercase() }

    return tasks.register<ProcessProtoFiles>("process${capitalName}ProtoFilesImports") {
        // this task deletes the destination directory if it results in NO-SOURCE,
        // which breaks the configuration cache for bufGenerate
        // so we prevent NO-SOURCE by creating a .keep file in the destination directory
        val keep = protoBuildDirSourceSetsKeep.ensureRegularFileExists()
        from(keep)

        duplicatesStrategy = DuplicatesStrategy.FAIL

        val allImports = importsProvider.map { list ->
            list.map { import ->
                files(import.sourceDirectories) {
                    include(import.includes)
                    exclude(import.excludes)
                }
            }
        }

        from(allImports)

        into(destination)

        doFirst {
            destination.deleteRecursively()
        }

        configure()
    }
}
