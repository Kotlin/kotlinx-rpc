/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.buf.tasks

import kotlinx.rpc.buf.BUF_YAML
import kotlinx.rpc.proto.IMPORT_PROTO_FILES_DIR
import kotlinx.rpc.proto.PROTO_FILES_DIR
import kotlinx.rpc.proto.PROTO_GROUP
import kotlinx.rpc.proto.protoBuildDirSourceSets
import kotlinx.rpc.util.ensureDirectoryExists
import kotlinx.rpc.util.ensureRegularFileExists
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import java.io.File

public abstract class BufYamlUpdate : DefaultTask() {
    @get:InputDirectory
    internal abstract val protoSourceDir: Property<File>

    @get:Optional
    @get:InputDirectory
    internal abstract val importSourceDir: Property<File>

    @get:OutputFile
    public abstract val bufFile: Property<File>

    init {
        group = PROTO_GROUP
    }

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

            val protoDir = protoSourceDir.get()
            if (protoDir.exists()) {
                val modulePath = protoDir.relativeTo(file.parentFile)
                writer.appendLine("  - path: $modulePath")
            }

            val importDir = importSourceDir.orNull
            if (importDir != null && importDir.exists()) {
                val modulePath = importDir.relativeTo(file.parentFile)
                writer.appendLine("  - path: $modulePath")
            }

            writer.flush()
        }
    }

    public companion object {
        public const val PREFIX_NAME: String = "bufYamlUpdate"
    }
}

internal fun Project.registerBufYamlUpdateTask(
    name: String,
    dir: String,
    withImport: Boolean,
    configure: BufYamlUpdate.() -> Unit = {},
): TaskProvider<BufYamlUpdate> {
    val capitalizeName = name.replaceFirstChar { it.uppercase() }
    return tasks.register<BufYamlUpdate>("${BufYamlUpdate.PREFIX_NAME}$capitalizeName") {
        val baseDir = project.protoBuildDirSourceSets.resolve(dir)
        val protoDir = baseDir
            .resolve(PROTO_FILES_DIR)
            .ensureDirectoryExists()

        protoSourceDir.set(protoDir)

        if (withImport) {
            val importDir = baseDir
                .resolve(IMPORT_PROTO_FILES_DIR)
                .ensureDirectoryExists()

            importSourceDir.set(importDir)
        }

        val bufYamlFile = baseDir
            .resolve(BUF_YAML)
            .ensureRegularFileExists()

        bufFile.set(bufYamlFile)

        configure()
    }
}
