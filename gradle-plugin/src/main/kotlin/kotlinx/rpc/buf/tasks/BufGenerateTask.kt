/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.buf.tasks

import kotlinx.rpc.protoc.PROTO_GROUP
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
import kotlinx.rpc.protoc.DefaultProtoSourceSet
import kotlinx.rpc.protoc.DefaultProtocExtension
import kotlinx.rpc.protoc.ProtoTask
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectories
import javax.inject.Inject

/**
 * Buf `generate` command.
 *
 * @see <a href="https://buf.build/docs/reference/cli/buf/generate/">buf generate</a>
 */
public abstract class BufGenerateTask @Inject internal constructor(
    properties: ProtoTask.Properties,
) : BufExecTask(properties) {
    /**
     * List of plugin names used during `buf generate` command execution.
     *
     * @see kotlinx.rpc.protoc.ProtoSourceSet.plugins
     * @see kotlinx.rpc.protoc.ProtocExtension.plugins
     * @see ProtocPlugin
     */
    @get:Input
    public abstract val pluginNames: ListProperty<String>

    /**
     * List of executable files used during `buf generate` command execution.
     *
     * @see [ProtocPlugin.Artifact.Local.executableFiles]
     */
    // unsued, but required for Gradle to properly recognize inputs
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
     * The directory to output generated files to, used as a `buf generate --output` argument,
     * not the directory for sources. For that see [outputSourceDirectories].
     */
    @get:OutputDirectory
    public abstract val outputDirectory: Property<File>

    /**
     * Generated source directories by plugin name.
     *
     * Can be used in [SourceDirectorySet.srcDir] or similar `srcDir` functions from other source set directories.
     */
    @get:OutputDirectories
    public val outputSourceDirectories: Provider<List<File>> = pluginNames.map { plugins ->
        val out = outputDirectory.get()
        plugins.map { out.resolve(it) }
    }

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
    protocExtension: DefaultProtocExtension,
    protoSourceSet: DefaultProtoSourceSet,
    workingDir: File,
    outputDirectory: File,
    includedPlugins: Provider<Set<ProtocPlugin>>,
    properties: ProtoTask.Properties,
    configure: BufGenerateTask.() -> Unit = {},
): TaskProvider<BufGenerateTask> {
    val capitalName = protoSourceSet.name.replaceFirstChar { it.uppercase() }
    val bufGenerateTaskName = "${BufGenerateTask.NAME_PREFIX}$capitalName"

    return registerBufExecTask<BufGenerateTask>(bufGenerateTaskName, provider { workingDir }, properties) {
        group = PROTO_GROUP
        description = "Generates code from .proto files using 'buf generate'"

        val generate = protocExtension.buf.generate

        includeImports.convention(generate.includeImports)
        includeWkt.convention(generate.includeWkt)
        errorFormat.convention(generate.errorFormat)

        this.outputDirectory.convention(outputDirectory)

        pluginNames.convention(includedPlugins.map { it.map { plugin -> plugin.name } })

        configure()
    }
}
