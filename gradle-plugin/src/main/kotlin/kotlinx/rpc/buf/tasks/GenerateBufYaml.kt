/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.buf.tasks

import kotlinx.rpc.buf.BUF_YAML
import kotlinx.rpc.protoc.DefaultProtoTask
import kotlinx.rpc.protoc.ProtoTask
import kotlinx.rpc.util.ensureRegularFileExists
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import java.io.File
import javax.inject.Inject

/**
 * Generates/updates a Buf `buf.yaml` file.
 */
public abstract class GenerateBufYaml @Inject internal constructor(
    properties: ProtoTask.Properties,
) : DefaultProtoTask(properties) {
    @get:Input
    internal abstract val protoSourceDir: Property<String>

    @get:Input
    internal abstract val importSourceDir: Property<String>

    @get:Input
    internal abstract val withImport: Property<Boolean>

    /**
     * The `buf.yaml` file to generate/update.
     */
    @get:OutputFile
    public abstract val bufFile: Property<File>

    @TaskAction
    internal fun generate() {
        val file = bufFile.get()

        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }

        file.bufferedWriter(Charsets.UTF_8).use { writer ->
            writer.appendLine("version: v2")
            writer.appendLine("lint:")
            writer.appendLine("  use:")
            writer.appendLine("    - STANDARD")
            writer.appendLine("breaking:")
            writer.appendLine("  use:")
            writer.appendLine("    - FILE")

            writer.appendLine("modules:")

            val protoDirName = protoSourceDir.get()
            val protoDir = file.parentFile.resolve(protoDirName)
            if (protoDir.exists()) {
                val modulePath = protoDir.relativeTo(file.parentFile)
                writer.appendLine("  - path: $modulePath")
            }

            val importDirName = importSourceDir.get()
            val importDir = file.parentFile.resolve(importDirName)
            if (withImport.get() && importDir.exists()) {
                val modulePath = importDir.relativeTo(file.parentFile)
                writer.appendLine("  - path: $modulePath")
            }

            writer.flush()
        }
    }

    internal companion object {
        const val NAME_PREFIX: String = "generateBufYaml"
    }
}

internal fun Project.registerGenerateBufYamlTask(
    name: String,
    buildSourceSetsDir: File,
    buildSourceSetsProtoDir: File,
    buildSourceSetsImportDir: File,
    withImport: Provider<Boolean>,
    properties: ProtoTask.Properties,
    configure: GenerateBufYaml.() -> Unit = {},
): TaskProvider<GenerateBufYaml> {
    val capitalizeName = name.replaceFirstChar { it.uppercase() }
    val task = tasks.register("${GenerateBufYaml.NAME_PREFIX}$capitalizeName", GenerateBufYaml::class, properties)

    task.configure {
        protoSourceDir.convention(buildSourceSetsProtoDir.name)
        importSourceDir.convention(buildSourceSetsImportDir.name)
        this.withImport.convention(withImport)

        val bufYamlFile = buildSourceSetsDir
            .resolve(BUF_YAML)
            .ensureRegularFileExists()

        bufFile.convention(bufYamlFile)

        configure()
    }

    return task
}
