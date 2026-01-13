/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread


/**
 * Parameters for the background task BuildService.
 */
internal interface BackgroundTaskParameters : BuildServiceParameters {
    /** Full command with arguments for a single background process. */
    val command: org.gradle.api.provider.ListProperty<String>
    val workingDir: Property<String>
    val readyString: Property<String?>
    val readyTimeoutSec: Property<Int>
}

/**
 * BuildService that owns background OS processes. It starts lazily on first use and
 * stops all processes at the end of the build.
 */
internal abstract class BackgroundTaskService : BuildService<BackgroundTaskParameters>, AutoCloseable {
    @Transient private var started = false
    @Transient private var process: Process? = null
    @Transient private var outThread: Thread? = null
    @Transient private var errThread: Thread? = null

    @Transient private val logger = org.gradle.api.logging.Logging.getLogger(BackgroundTaskService::class.java)

    @Synchronized
    fun startIfNeeded() {
        if (started) return
        started = true

        val command = parameters.command.get()
        logger.info("Starting background process: $command")

        val wd = parameters.workingDir.orNull?.takeIf { it.isNotEmpty() }?.let { File(it) }
        val readyToken = parameters.readyString.orNull
        val timeoutSec = parameters.readyTimeoutSec.orNull ?: 30
        val latch = if (readyToken.isNullOrEmpty()) null else CountDownLatch(1)

        val pb = ProcessBuilder(command).apply { if (wd != null) directory(wd) }
        val p = pb.start()
        process = p

        // stdout reader
        outThread = thread(name = "background-task-stdout", isDaemon = true) {
            try {
                BufferedReader(InputStreamReader(p.inputStream)).use { br ->
                    var line: String?
                    while (br.readLine().also { line = it } != null) {
                        val text = line!!
                        println(text)
                        if (readyToken != null && text.contains(readyToken)) {
                            latch?.countDown()
                        }
                    }
                }
            } catch (_: Throwable) {}
        }

        // stderr reader
        errThread = thread(name = "background-task-stderr", isDaemon = true) {
            try {
                BufferedReader(InputStreamReader(p.errorStream)).use { br ->
                    var line: String?
                    while (br.readLine().also { line = it } != null) {
                        System.err.println(line!!)
                    }
                }
            } catch (_: Throwable) {}
        }

        // Wait for readiness
        if (latch != null) {
            logger.info("Waiting for readiness (${timeoutSec}s timeout)...")
            val ready = latch.await(timeoutSec.toLong(), TimeUnit.SECONDS)
            if (!ready) {
                stop()
                throw GradleException("Timeout waiting for background task readiness")
            }
            logger.info("Background task is ready.")
        } else {
            // small grace period to surface immediate failures
            runCatching { Thread.sleep(300) }
        }
    }

    @Synchronized
    fun stop() {
        val p = process ?: return
        runCatching { logger.info("Terminating background process (pid=${try { p.pid() } catch (_: Throwable) { "?" }})") }
        runCatching { p.destroy() }
        if (!runCatching { p.waitFor(3, TimeUnit.SECONDS) }.getOrDefault(true)) {
            runCatching { p.destroyForcibly() }
        }
        runCatching { outThread?.join(1000) }
        runCatching { errThread?.join(1000) }
        process = null
        outThread = null
        errThread = null
    }

    override fun close() {
        stop()
    }
}

class BackgroundTaskConfig {
    internal var command: List<String>? = null
    var workingDir: File? = null
    var readyString: String? = null
    var readyTimeoutSec: Int = 30

    fun commandLine(command: String, vararg args: String) {
        val newCmd = listOf(command) + args
        if (this.command != null) {
            throw GradleException("Only a single command is supported in doInBackground; duplicate commandLine() detected: ${this.command} and $newCmd")
        }
        this.command = newCmd
    }
}

private fun stableConfigId(command: List<String>, workingDir: File?): String {
    return (command + (workingDir?.absolutePath ?: ""))
        .joinToString("\u0000")
}

internal fun Project.registerBackgroundTaskService(
    namePrefix: String,
    configure: BackgroundTaskConfig.() -> Unit,
): Provider<BackgroundTaskService> {
    val cfg = BackgroundTaskConfig().apply(configure)
    val cmd = cfg.command ?: throw GradleException("doInBackground requires a commandLine to be specified")
    val id = stableConfigId(cmd, cfg.workingDir)
    val serviceName = "$namePrefix-$id"

    return gradle.sharedServices.registerIfAbsent(serviceName, BackgroundTaskService::class.java) {
        parameters.command.set(cmd)
        parameters.workingDir.set(cfg.workingDir?.absolutePath ?: "")
        parameters.readyString.set(cfg.readyString)
        parameters.readyTimeoutSec.set(cfg.readyTimeoutSec)
    }
}

/**
 * Bind a background BuildService to this task.
 * The service starts lazily before task execution and terminates at the task end.
 */
fun Task.withBackgroundTask(
    configure: BackgroundTaskConfig.() -> Unit = { },
) {
    val svc = project.registerBackgroundTaskService(namePrefix = "background-$name", configure)
    // Inform Gradle about the service usage for proper scheduling
    usesService(svc)

    doFirst { svc.get().startIfNeeded() }
    doLast  { svc.get().stop() }
}
