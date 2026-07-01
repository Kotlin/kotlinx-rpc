/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.buf.tasks

import kotlinx.rpc.protoc.ProtoTask
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskProvider
import java.io.File
import javax.inject.Inject

/**
 * Buf `dep update` command or copies existing `buf.lock` file.
 *
 * @see <a href="https://buf.build/docs/reference/cli/buf/dep/update/">buf dep update</a>
 */
public abstract class BufLockTask @Inject internal constructor(
    properties: ProtoTask.Properties,
) : BufExecTask(properties) {
    init {
        command.convention("dep")
        args.convention(listOf("update"))

        bufLockFile.convention(project.layout.file(workingDir.map { it.resolve("buf.lock") }))

        onlyIf { bsrDeps.get().isNotEmpty() }
    }

    /**
     * Declared BSR modules. If empty `buf dep update` is not run.
     */
    @get:Input
    public abstract val bsrDeps: ListProperty<String>

    /**
     * If defined, this file is used as the `buf.lock` instead of running `buf dep update`.
     */
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    @get:Optional
    public abstract val sourceLockFile: Property<File>

    /**
     * The `buf.lock` file to generate.
     */
    @get:OutputFile
    public abstract val bufLockFile: RegularFileProperty

    override fun exec() {
        val source = sourceLockFile.orNull
        if (source != null) {
            source.copyTo(bufLockFile.get().asFile, overwrite = true)
        } else {
            super.exec()
        }
    }

    internal companion object {
        const val NAME_PREFIX: String = "bufLock"
    }
}

internal fun Project.registerBufLockTask(
    name: String,
    workingDir: File,
    bsrDependencies: Provider<List<String>>,
    properties: ProtoTask.Properties,
    configure: BufLockTask.() -> Unit = {},
): TaskProvider<BufLockTask> {
    return registerBufExecTask(BufLockTask::class, "${BufLockTask.NAME_PREFIX}$name", provider { workingDir }, properties) {
        bsrDeps.convention(bsrDependencies)

        configure()
    }
}