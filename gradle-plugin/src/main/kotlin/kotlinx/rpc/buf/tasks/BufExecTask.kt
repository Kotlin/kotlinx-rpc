/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.buf.tasks

import kotlinx.rpc.buf.BUF_EXECUTABLE_CONFIGURATION
import kotlinx.rpc.buf.BufExtension
import kotlinx.rpc.buf.execBuf
import kotlinx.rpc.protoc.PROTO_GROUP
import kotlinx.rpc.rpcExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import java.io.File
import kotlin.reflect.KClass
import kotlinx.rpc.buf.BUF_GEN_YAML
import kotlinx.rpc.buf.BUF_YAML
import kotlinx.rpc.buf.BufTasksExtension
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.Classpath

/**
 * Abstract base class for `buf` tasks.
 */
public abstract class BufExecTask : DefaultTask() {
    init {
        group = PROTO_GROUP
    }

    @get:InputFile
    internal abstract val bufExecutable: Property<File>

    @get:Input
    internal abstract val debug: Property<Boolean>

    /**
     * The `buf` command to execute.
     *
     * Example: `build`, `generate`, `lint`, `mod`, `push`, `version`.
     */
    @get:Input
    public abstract val command: Property<String>

    /**
     * Arguments for the `buf` command.
     */
    @get:Input
    public abstract val args: ListProperty<String>

    /**
     * The working directory for the `buf` command.
     */
    @get:InputDirectory
    // https://docs.gradle.org/current/userguide/incremental_build.html#sec:configure_input_normalization
    @get:Classpath
    public abstract val workingDir: Property<File>

    /**
     * The `buf.yaml` file to use via `--config` option.
     */
    @get:InputFile
    @get:Optional
    public abstract val configFile: Property<File>

    /**
     * @see [BufExtension.logFormat]
     */
    @get:Input
    public abstract val logFormat: Property<BufExtension.LogFormat>

    /**
     * @see [BufExtension.timeout]
     */
    @get:Input
    public abstract val bufTimeoutInWholeSeconds: Property<Long>

    @TaskAction
    internal fun exec() {
        execBuf(listOf(command.get()) + args.get())
    }
}

/**
 * Registers a [BufExecTask] of type [T].
 *
 * Use it to create custom `buf` tasks.
 *
 * These tasks are NOT automatically configured
 * to work with the generated [BUF_GEN_YAML] and [BUF_YAML] files and the corresponding workspace.
 *
 * For that use [BufTasksExtension.registerWorkspaceTask].
 */
public inline fun <reified T : BufExecTask> Project.registerBufExecTask(
    name: String,
    workingDir: Provider<File>,
    noinline configuration: T.() -> Unit,
): TaskProvider<T> = registerBufExecTask(T::class, name, workingDir, configuration)

@PublishedApi
internal fun <T : BufExecTask> Project.registerBufExecTask(
    clazz: KClass<T>,
    name: String,
    workingDir: Provider<File>,
    configuration: T.() -> Unit = {},
): TaskProvider<T> = tasks.register(name, clazz) {
    val executableConfiguration = configurations.getByName(BUF_EXECUTABLE_CONFIGURATION)
    bufExecutable.set(executableConfiguration.singleFile)
    this.workingDir.set(workingDir)

    val buf = provider { rpcExtension().protoc.buf }
    configFile.set(buf.flatMap { it.configFile })
    logFormat.set(buf.flatMap { it.logFormat })
    bufTimeoutInWholeSeconds.set(buf.flatMap { it.timeout.map { duration -> duration.inWholeSeconds } })
    debug.set(gradle.startParameter.logLevel == LogLevel.DEBUG)

    configuration()
}
