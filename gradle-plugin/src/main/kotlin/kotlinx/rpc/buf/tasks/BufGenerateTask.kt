/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.buf.tasks

import kotlinx.rpc.proto.PROTO_GROUP
import kotlinx.rpc.rpcExtension
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskProvider
import java.io.File

public abstract class BufGenerateTask : BufExecTask() {
    @get:Input
    @get:Optional
    public abstract val additionalArgs: ListProperty<String>

    /** Whether to include imports. */
    @get:Input
    internal abstract val includeImports: Property<Boolean>

    /** The input proto files. */
    @get:InputFiles
    internal abstract val inputFiles: ConfigurableFileCollection

    /** The directory to output generated files. */
    @get:OutputDirectory
    internal abstract val outputDirectory: Property<File>

    init {
        command.set("generate")

        val args = project.provider {
            listOfNotNull(
                "--output", outputDirectory.get().absolutePath,
                if (includeImports.orNull == true) "--include-imports" else null,
            ) + additionalArgs.orNull.orEmpty()
        }

        this.args.set(args)
    }

    public companion object {
        internal const val NAME_PREFIX: String = "bufGenerate"
    }
}

internal fun Project.registerBufGenerateTask(
    name: String,
    workingDir: Provider<File>,
    inputFiles: Provider<FileCollection>,
    outputDirectory: Provider<File>,
    configure: BufGenerateTask.() -> Unit = {},
): TaskProvider<BufGenerateTask> {
    return registerBufExecTask<BufGenerateTask>(name, workingDir) {
        group = PROTO_GROUP
        description = "Generates code from .proto files"

        val generate = project.rpcExtension().grpc.buf.generate

        includeImports.set(generate.includeImports)
        this.inputFiles.setFrom(inputFiles)
        this.outputDirectory.set(outputDirectory)

        configure()
    }
}
