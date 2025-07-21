/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.buf.tasks

import kotlinx.rpc.buf.BUF_EXECUTABLE_CONFIGURATION
import kotlinx.rpc.buf.execBuf
import kotlinx.rpc.proto.PROTO_GROUP
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

public abstract class BufExecTask : DefaultTask() {
    init {
        group = PROTO_GROUP
    }

    @get:InputFile
    internal abstract val bufExecutable: Property<File>

    @get:Input
    public abstract val command: Property<String>

    @get:Input
    public abstract val args: ListProperty<String>

    @get:InputDirectory
    public abstract val workingDir: Property<File>

    @get:InputFile
    @get:Optional
    public abstract val configFile: Property<File>

    @TaskAction
    public fun exec() {
        execBuf(listOf(command.get()) + args.get())
    }
}

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
    configuration: T.() -> Unit,
): TaskProvider<T> = tasks.register(name, clazz) {
    val executableConfiguration = configurations.getByName(BUF_EXECUTABLE_CONFIGURATION)
    bufExecutable.set(executableConfiguration.singleFile)
    this.workingDir.set(workingDir)
    configFile.set(project.rpcExtension().grpc.buf.configFile)

    configuration()
}
