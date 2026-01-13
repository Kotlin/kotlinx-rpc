/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import java.io.File
import javax.inject.Inject

/**
 * Copy proto files to a temporary directory for Buf to process.
 */
public abstract class ProcessProtoFiles @Inject internal constructor(
    @get:Internal
    override val properties: ProtoTask.Properties,
) : Sync(), ProtoTask {
    init {
        group = PROTO_GROUP
    }
}

internal fun Project.registerProcessProtoFilesTask(
    name: String,
    destination: File,
    protoFilesDirectorySet: SourceDirectorySet,
    properties: ProtoTask.Properties,
    configure: ProcessProtoFiles.() -> Unit = {},
): TaskProvider<ProcessProtoFiles> {
    val capitalName = name.replaceFirstChar { it.uppercase() }

    val task = tasks.register("process${capitalName}ProtoFiles", ProcessProtoFiles::class, properties)

    task.configure {
        duplicatesStrategy = DuplicatesStrategy.FAIL

        from(files(protoFilesDirectorySet.sourceDirectories)) {
            include(protoFilesDirectorySet.includes)
            exclude(protoFilesDirectorySet.excludes)
        }

        into(destination)

        configure()
    }

    return task
}

internal fun Project.registerProcessProtoFilesImportsTask(
    name: String,
    destination: File,
    importsProvider: Provider<Set<ProtoSourceSet>>,
    rawImports: ConfigurableFileCollection,
    properties: ProtoTask.Properties,
    configure: ProcessProtoFiles.() -> Unit = {},
): TaskProvider<ProcessProtoFiles> {
    val capitalName = name.replaceFirstChar { it.uppercase() }

    val task = tasks.register("process${capitalName}ProtoFilesImports", ProcessProtoFiles::class, properties)
    task.configure {
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

    return task
}
