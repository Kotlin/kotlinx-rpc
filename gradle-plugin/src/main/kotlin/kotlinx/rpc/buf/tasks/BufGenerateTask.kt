/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.buf.tasks

import kotlinx.rpc.protoc.PROTO_GROUP
import kotlinx.rpc.rpcExtension
import kotlinx.rpc.protoc.ProtocPlugin
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskProvider
import java.io.File
import kotlinx.rpc.buf.BufGenerateExtension
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles

/**
 * Buf `generate` command.
 *
 * @see <a href="https://buf.build/docs/reference/cli/buf/generate/">buf generate</a>
 */
public abstract class BufGenerateTask : BufExecTask() {
    // unsued, but required for Gradle to properly recognise inputs
    @get:InputDirectory
    internal abstract val protoFilesDir: Property<File>

    // unsued, but required for Gradle to properly recognise inputs
    @get:InputDirectory
    internal abstract val importFilesDir: Property<File>

    /**
     * List of files used during `buf generate` command execution.
     *
     * @see [ProtocPlugin.Artifact.Local.executableFiles]
     */
    // unsued, but required for Gradle to properly recognise inputs
    @get:InputFiles
    public abstract val executableFiles: ListProperty<File>

    /**
     * Whether to include imports.
     *
     * @see <a href="https://buf.build/docs/reference/cli/buf/generate/#include-imports">
     *     buf generate --include-imports
     * </a>
     * @see [BufGenerateExtension.includeImports]
     */
    @get:Input
    public abstract val includeImports: Property<Boolean>

    /**
     * Whether to include Well-Known Types.
     *
     * Automatically sets [includeImports] to `true`.
     *
     * @see <a href="https://buf.build/docs/reference/cli/buf/generate/#include-wkt">buf generate --include-wkt</a>
     * @see [BufGenerateExtension.includeWkt]
     */
    @get:Input
    public abstract val includeWkt: Property<Boolean>

    /**
     * `--error-format` option.
     *
     * @see <a href="https://buf.build/docs/reference/cli/buf/generate/#error-format">buf generate --error-format</a>
     * @see [BufGenerateExtension.errorFormat]
     */
    @get:Input
    public abstract val errorFormat: Property<BufGenerateExtension.ErrorFormat>

    /**
     * Additional arguments for `buf generate` command.
     */
    @get:Input
    @get:Optional
    public abstract val additionalArgs: ListProperty<String>

    /**
     * The directory to output generated files.
     */
    @get:OutputDirectory
    public abstract val outputDirectory: Property<File>

    init {
        command.set("generate")

        val args = project.provider {
            buildList {
                add("--output"); add(outputDirectory.get().absolutePath)

                if (includeImports.get() || includeWkt.get()) {
                    add("--include-imports")
                }

                if (includeWkt.get()) {
                    add("--include-wkt")
                }

                val errorFormatValue = errorFormat.get()
                if (errorFormatValue != BufGenerateExtension.ErrorFormat.Default) {
                    add("--error-format"); add(errorFormatValue.cliValue)
                }
            } + additionalArgs.orNull.orEmpty()
        }

        this.args.set(args)
    }

    internal companion object {
        const val NAME_PREFIX: String = "bufGenerate"
    }
}

internal fun Project.registerBufGenerateTask(
    name: String,
    workingDir: File,
    outputDirectory: File,
    protoFilesDir: File,
    importFilesDir: File,
    configure: BufGenerateTask.() -> Unit = {},
): TaskProvider<BufGenerateTask> {
    val capitalName = name.replaceFirstChar { it.uppercase() }
    val bufGenerateTaskName = "${BufGenerateTask.NAME_PREFIX}$capitalName"

    return registerBufExecTask<BufGenerateTask>(bufGenerateTaskName, provider { workingDir }) {
        group = PROTO_GROUP
        description = "Generates code from .proto files using 'buf generate'"

        val generate = provider { rpcExtension().protoc.get().buf.generate }

        includeImports.set(generate.flatMap { it.includeImports })
        includeWkt.set(generate.flatMap { it.includeWkt })
        errorFormat.set(generate.flatMap { it.errorFormat })

        this.outputDirectory.set(outputDirectory)

        this.protoFilesDir.set(protoFilesDir)
        this.importFilesDir.set(importFilesDir)

        configure()
    }
}
