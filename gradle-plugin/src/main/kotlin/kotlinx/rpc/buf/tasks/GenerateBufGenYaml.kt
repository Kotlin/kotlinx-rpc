/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.buf.tasks

import kotlinx.rpc.buf.BUF_GEN_YAML
import kotlinx.rpc.proto.PROTO_FILES_DIR
import kotlinx.rpc.proto.PROTO_GROUP
import kotlinx.rpc.proto.ProtocPlugin
import kotlinx.rpc.proto.protoBuildDirSourceSets
import kotlinx.rpc.util.ensureRegularFileExists
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import java.io.File
import java.io.Serializable

internal data class ResolvedGrpcPlugin(
    val type: Type,
    val locator: List<String>,
    val out: String,
    val options: Map<String, Any?>,
    val strategy: String?,
    val includeImports: Boolean?,
    val includeWkt: Boolean?,
    val types: List<String>,
    val excludeTypes: List<String>,
) : Serializable {
    @Suppress("EnumEntryName", "detekt.EnumNaming")
    enum class Type {
        local, remote,
        ;
    }

    companion object {
        @Suppress("unused")
        private const val serialVersionUID: Long = 1L
    }
}

/**
 * Generates/updates Buf `buf.gen.yaml` file.
 */
public abstract class GenerateBufGenYaml : DefaultTask() {
    @get:Input
    internal abstract val plugins: ListProperty<ResolvedGrpcPlugin>

    /**
     * The `buf.gen.yaml` file to generate/update.
     */
    @get:OutputFile
    public abstract val bufGenFile: Property<File>

    init {
        group = PROTO_GROUP
    }

    @TaskAction
    @Suppress("detekt.CyclomaticComplexMethod", "detekt.NestedBlockDepth")
    internal fun generate() {
        val file = bufGenFile.get()
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }

        file.bufferedWriter(Charsets.UTF_8).use { writer ->
            writer.appendLine("version: v2")
            writer.appendLine("clean: true")
            writer.appendLine("plugins:")
            plugins.get().forEach { plugin ->
                val locatorLine = when (plugin.type) {
                    ResolvedGrpcPlugin.Type.local -> {
                        when (plugin.locator.size) {
                            0 -> error("Local plugin without locators")
                            1 -> plugin.locator.single()
                            else -> plugin.locator.joinToString(", ", prefix = "[", postfix = "]")
                        }
                    }

                    ResolvedGrpcPlugin.Type.remote -> {
                        plugin.locator.single()
                    }
                }

                writer.appendLine("  - ${plugin.type.name}: $locatorLine")
                if (plugin.strategy != null) {
                    writer.appendLine("    strategy: ${plugin.strategy}")
                }
                if (plugin.includeImports != null) {
                    writer.appendLine("    include_imports: ${plugin.includeImports}")
                }
                if (plugin.includeWkt != null) {
                    writer.appendLine("    include_wkt: ${plugin.includeWkt}")
                }
                if (plugin.types.isNotEmpty()) {
                    writer.appendLine("    types:")
                    plugin.types.forEach { type ->
                        writer.appendLine("      - $type")
                    }
                }
                if (plugin.excludeTypes.isNotEmpty()) {
                    writer.appendLine("    exclude_types:")
                    plugin.excludeTypes.forEach { type ->
                        writer.appendLine("      - $type")
                    }
                }
                writer.appendLine("    out: ${plugin.out}")
                if (plugin.options.isNotEmpty()) {
                    writer.appendLine("    opt:")
                    plugin.options.forEach { (key, value) ->
                        writer.appendLine("      - $key${if (value != null) "=$value" else ""}")
                    }
                }
            }

            writer.appendLine("inputs:")
            writer.appendLine("  - directory: $PROTO_FILES_DIR")

            writer.flush()
        }
    }

    internal companion object {
        const val NAME_PREFIX: String = "generateBufGenYaml"
    }
}

internal fun Project.registerGenerateBufGenYamlTask(
    name: String,
    dir: String,
    protocPlugins: Iterable<ProtocPlugin>,
    configure: GenerateBufGenYaml.() -> Unit = {},
): TaskProvider<GenerateBufGenYaml> {
    val capitalizeName = name.replaceFirstChar { it.uppercase() }
    return project.tasks.register<GenerateBufGenYaml>("${GenerateBufGenYaml.NAME_PREFIX}$capitalizeName") {
        val pluginsProvider = project.provider {
            protocPlugins.map { plugin ->
                if (!plugin.artifact.isPresent) {
                    throw GradleException(
                        "Artifact is not specified for protoc plugin ${plugin.name}. " +
                                "Use `local {}` or `remote {}` to specify it.")
                }

                val artifact = plugin.artifact.get()
                val locator = when (artifact) {
                    is ProtocPlugin.Artifact.Local -> artifact.executor.get()
                    is ProtocPlugin.Artifact.Remote -> listOf(artifact.locator.get())
                }

                ResolvedGrpcPlugin(
                    type = if (artifact is ProtocPlugin.Artifact.Local) {
                        ResolvedGrpcPlugin.Type.local
                    } else {
                        ResolvedGrpcPlugin.Type.remote
                    },
                    locator = locator,
                    options = plugin.options.get(),
                    out = plugin.name,
                    strategy = plugin.strategy.orNull?.name?.lowercase(),
                    includeImports = plugin.includeImports.orNull,
                    includeWkt = plugin.includeWkt.orNull,
                    types = plugin.types.get(),
                    excludeTypes = plugin.excludeTypes.get(),
                )
            }
        }

        plugins.set(pluginsProvider)

        val bufGenYamlFile = project.protoBuildDirSourceSets
            .resolve(dir)
            .resolve(BUF_GEN_YAML)
            .ensureRegularFileExists()

        bufGenFile.set(bufGenYamlFile)

        configure()
    }
}
