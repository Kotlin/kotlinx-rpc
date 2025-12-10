/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * Parameters for the background task BuildService.
 */
internal interface BackgroundTaskParameters : BuildServiceParameters {
    val commands: ListProperty<List<String>>
    val workingDir: Property<String>
}

/**
 * BuildService that owns background OS processes. It starts lazily on first use and
 * stops all processes at the end of the build.
 */
internal abstract class BackgroundTaskService : BuildService<BackgroundTaskParameters>, AutoCloseable {
    @Transient private var started = false
    @Transient private var processes: MutableList<Process> = mutableListOf()

    @Synchronized
    fun startIfNeeded() {
        if (started) return
        started = true

        val wd = parameters.workingDir.orNull?.takeIf { it.isNotEmpty() }?.let { File(it) }
        parameters.commands.get().forEach { cmd ->
            val pb = ProcessBuilder(cmd)
                .inheritIO()
                .apply { if (wd != null) directory(wd) }
            val p = pb.start()
            // give each process a short startup window
            p.waitFor(100, TimeUnit.MILLISECONDS)
            processes.add(p)
        }
    }

    @Synchronized
    fun stop() {
        processes.forEach { p ->
            runCatching {
                p.destroy()
                if (!p.waitFor(3, TimeUnit.SECONDS)) p.destroyForcibly()
            }
        }
        processes.clear()
    }

    override fun close() {
        stop()
    }
}


class BackgroundTaskConfig {
    internal val commands: MutableList<List<String>> = mutableListOf()
    var workingDir: File? = null

    fun commandLine(command: String, vararg args: String) {
        commands.add(listOf(command) + args)
    }
}

private fun stableConfigId(commands: List<List<String>>, workingDir: File?): String {
    return (commands.flatten() + (workingDir?.absolutePath ?: ""))
        .joinToString("\u0000")
}

internal fun Project.registerBackgroundTaskService(
    namePrefix: String,
    configure: BackgroundTaskConfig.() -> Unit,
): Provider<BackgroundTaskService> {
    val cfg = BackgroundTaskConfig().apply(configure)
    val id = stableConfigId(cfg.commands, cfg.workingDir)
    val serviceName = "$namePrefix-$id"

    return gradle.sharedServices.registerIfAbsent(serviceName, BackgroundTaskService::class.java) {
        parameters.commands.set(cfg.commands)
        parameters.workingDir.set(cfg.workingDir?.absolutePath ?: "")
    }
}

/**
 * Bind a background BuildService to this task.
 * The service starts lazily before task execution and terminates at the task end.
 */
fun Task.doInBackground(
    configure: BackgroundTaskConfig.() -> Unit = { },
) {
    val svc = project.registerBackgroundTaskService(namePrefix = "background-$name", configure)
    // Inform Gradle about the service usage for proper scheduling
    usesService(svc)

    doFirst { svc.get().startIfNeeded() }
    doLast  { svc.get().stop() }
}
