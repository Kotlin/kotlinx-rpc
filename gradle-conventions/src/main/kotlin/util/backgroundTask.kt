/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.Task
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import java.io.File
import java.io.Serializable
import java.util.concurrent.TimeUnit
import java.util.concurrent.ConcurrentHashMap


class RunInBackgroundConfig {
    internal val shellCommands: MutableList<RunShellCommandConfig> = mutableListOf()
}

fun Task.doInBackground(
    configure: RunInBackgroundConfig.() -> Unit = { },
) {
    // Extract all configuration at configuration time
    val config = RunInBackgroundConfig().apply(configure)
    val taskName = name
    val ids = config.shellCommands.map { "$taskName-${it.command}-${it.args.joinToString("-")}" }

    // Register (or reuse) a shared build service at configuration time, so task actions
    // don't need to access Project/Task APIs, which violates configuration cache rules
    val bgServiceProvider = project.gradle.sharedServices.registerIfAbsent(
        "backgroundProcessService",
        BackgroundProcessService::class.java
    ) { /* no params */ }

    doFirst {
        logger.debug("[$taskName] Starting background processes")
        config.shellCommands.forEachIndexed { index, command ->
            val process = ShellProcess(command)
            val processId = ids[index]
            bgServiceProvider.get().register(processId, process)
            process.start()
            logger.debug("[$taskName] Background process '$processId' started")
        }
    }

    doLast {
        logger.debug("[$taskName] Stopping background processes")
        ids.forEach { id ->
            bgServiceProvider.get().unregister(id)?.stop()
        }
    }

}

data class RunShellCommandConfig(
    var workingDir: File? = null,
    var command: String = "",
    var args: List<String> = emptyList(),
) : Serializable

fun RunInBackgroundConfig.commandLine(
    configure : RunShellCommandConfig.() -> Unit = { },
) {
   shellCommands.add(RunShellCommandConfig().apply(configure))
}

internal class ShellProcess(
    private val config: RunShellCommandConfig,
) : Serializable {
    private var process: Process? = null

    fun start() {
        check(process == null) { "Process is already started" }
        val command = listOf(config.command) + config.args

        val builder = ProcessBuilder(command)
            .inheritIO()
            .apply {
                if (config.workingDir != null) directory(config.workingDir)
            }

        process = builder.start()
        // give process a startup time of 100ms
        process?.waitFor(1000, TimeUnit.MILLISECONDS)
    }

    fun stop() {
        val proc = checkNotNull(process) { "Process is not started" }
        proc.destroy()
        val terminated = proc.waitFor(3, TimeUnit.SECONDS)
        if (!terminated) proc.destroyForcibly()
        process = null
    }
}

/**
 * BuildService-backed registry for background processes, configuration-cache friendly.
 */
internal abstract class BackgroundProcessService : BuildService<BuildServiceParameters.None>, AutoCloseable {
    private val processes = ConcurrentHashMap<String, ShellProcess>()

    fun register(id: String, process: ShellProcess) {
        check(processes.putIfAbsent(id, process) == null) { "Background process with id '$id' already exists" }
    }

    fun unregister(id: String): ShellProcess? = processes.remove(id)

    override fun close() {
        // Ensure all processes are stopped if the service is closed
        processes.values.forEach {
            runCatching { it.stop() }
        }
        processes.clear()
    }
}
