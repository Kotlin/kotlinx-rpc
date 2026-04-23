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
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import java.io.File
import javax.inject.Inject

/**
 * Generates/updates a Buf `buf.yaml` file.
 *
 * The `sourceSetName` input makes the cache key unique per source set so outcomes stay
 * predictable (strict SUCCESS / UP_TO_DATE / FROM_CACHE per the usual Gradle semantics) —
 * without it, source sets with coincidentally identical dir names and existence flags would
 * share a cache entry and produce cross-source-set FROM_CACHE hits that are hard to reason
 * about in tests.
 *
 * **Warning to maintainers:** any filesystem state read inside the `@TaskAction` MUST be
 * declared as an `@Input` (or wired via a `Provider` bound to an `@Input Property`). Undeclared
 * filesystem reads break cache correctness silently — Gradle will cache-hit on stale output
 * when the undeclared input changes.
 */
@CacheableTask
public abstract class GenerateBufYaml @Inject internal constructor(
    properties: ProtoTask.Properties,
) : DefaultProtoTask(properties) {
    // Source set identity. Included as an @Input so each source set has a distinct cache key
    // even when its other inputs happen to coincide with another source set's.
    @get:Input
    internal abstract val sourceSetName: Property<String>

    @get:Input
    internal abstract val protoSourceDir: Property<String>

    @get:Input
    internal abstract val importSourceDir: Property<String>

    @get:Input
    internal abstract val withImport: Property<Boolean>

    // Backed by a Provider in the caller so directory existence is resolved during task
    // fingerprinting (after upstream process-proto tasks have run) and participates in the
    // cache key. Do not replace with a direct `File.exists()` call in the @TaskAction.
    @get:Input
    internal abstract val protoSourceDirExists: Property<Boolean>

    @get:Input
    internal abstract val importSourceDirExists: Property<Boolean>

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

        file.bufferedWriter().use { writer ->
            writer.appendLine("version: v2")
            writer.appendLine("lint:")
            writer.appendLine("  use:")
            writer.appendLine("    - STANDARD")
            writer.appendLine("breaking:")
            writer.appendLine("  use:")
            writer.appendLine("    - FILE")

            writer.appendLine("modules:")

            if (protoSourceDirExists.get()) {
                writer.appendLine("  - path: ${protoSourceDir.get()}")
            }

            if (withImport.get() && importSourceDirExists.get()) {
                writer.appendLine("  - path: ${importSourceDir.get()}")
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
    val task = tasks.register("${GenerateBufYaml.NAME_PREFIX}$name", GenerateBufYaml::class, properties)

    task.configure {
        sourceSetName.convention(name)
        protoSourceDir.convention(buildSourceSetsProtoDir.name)
        importSourceDir.convention(buildSourceSetsImportDir.name)
        this.withImport.convention(withImport)

        protoSourceDirExists.convention(provider { buildSourceSetsProtoDir.exists() })
        importSourceDirExists.convention(provider { buildSourceSetsImportDir.exists() })

        val bufYamlFile = buildSourceSetsDir
            .resolve(BUF_YAML)
            .ensureRegularFileExists()

        bufFile.convention(bufYamlFile)

        configure()
    }

    return task
}
