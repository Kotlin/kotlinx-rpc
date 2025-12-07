/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import java.io.File

/**
 * Copy proto files to a temporary directory for Buf to process.
 */
public abstract class ProcessProtoFiles internal constructor(): Sync() {
    init {
        group = PROTO_GROUP
    }
}

internal fun Project.registerProcessProtoFilesTask(
    name: String,
    destination: File,
    protoFilesDirectorySet: SourceDirectorySet,
    configure: ProcessProtoFiles.() -> Unit = {},
): TaskProvider<ProcessProtoFiles> {
    val capitalName = name.replaceFirstChar { it.uppercase() }

    return tasks.register<ProcessProtoFiles>("process${capitalName}ProtoFiles") {
        duplicatesStrategy = DuplicatesStrategy.FAIL

        from(files(protoFilesDirectorySet.sourceDirectories)) {
            include(protoFilesDirectorySet.includes)
            exclude(protoFilesDirectorySet.excludes)
        }

        into(destination)

        configure()
    }
}

internal fun Project.registerProcessProtoFilesImportsTask(
    name: String,
    destination: File,
    importsProvider: Provider<Set<ProtoSourceSet>>,
    rawImports: ConfigurableFileCollection,
    configure: ProcessProtoFiles.() -> Unit = {},
): TaskProvider<ProcessProtoFiles> {
    val capitalName = name.replaceFirstChar { it.uppercase() }

    return tasks.register<ProcessProtoFiles>("process${capitalName}ProtoFilesImports") {
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
        from(rawImports)

        into(destination)

        configure()
    }
}
