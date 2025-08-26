/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.File
import javax.inject.Inject
import kotlin.io.path.Path

abstract class BazelTask @Inject constructor(
    private val execOps: ExecOperations,
    private val fsOps: FileSystemOperations,
) : DefaultTask() {

    @get:Input
    abstract val bazelPath: Property<String>           // "bazel"

    @get:Input
    abstract val command: Property<String>             // usually "build"

    @get:Input
    abstract val args: ListProperty<String>            // e.g. ["--color=yes"]

    @get:Input
    abstract val targets: ListProperty<String>         // e.g. ["//:kgrpc"]

    @get:Input
    abstract val outputLabels: ListProperty<String>    // labels to collect files from (e.g. ["//:kgrpc"])

    @get:Input
    abstract val workingDir: Property<File>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    init {
        group = "bazel"
        description = "Runs Bazel and collects built files into a Gradle output dir."
        bazelPath.convention(project.provider {
            findExecutable("bazel") ?: error("Could not find bazel executable")
        })
        command.convention("build")
        args.convention(emptyList())
        targets.convention(emptyList())
        outputLabels.convention(emptyList())
        workingDir.convention(project.rootDir)
        outputDir.convention(project.layout.buildDirectory.dir("bazel-out/${name}"))
    }

    @TaskAction
    fun run() {
        val bazelPath = bazelPath.get()
        val command = command.get()
        val args = args.get()
        val targets = targets.get()
        val outputLabels = outputLabels.get()
        val outputDir = outputDir.get()

        // exec actual command
        exec(listOf(bazelPath, command) + args + targets)

        val execroot = execCapture(listOf(bazelPath, "info", "execution_root")).trim()
        if (execroot.isEmpty()) error("Could not determine Bazel execution_root")

        // query files for the given labels
        val files = mutableSetOf<File>()
        for (label in outputLabels) {
            val out = execCapture(listOf(bazelPath, "cquery", "--output=files", label))
            out.lineSequence()
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .map { File(execroot, it) }
                .forEach { files += it }
        }
        if (files.isEmpty()) {
            logger.warn("No files returned by cquery for labels: $outputLabels")
            return
        }

        // copy into Gradle outputDir
        val dest = outputDir.asFile.also { it.mkdirs() }
        val execRootDir = Path(execroot)

        files.forEach { file ->
            val relativePath = execRootDir.relativize(file.toPath())
            fsOps.copy {
                from(relativePath)
                into(dest)
            }
        }

    }

    private fun exec(cmd: List<String>) {
        val result = execOps.exec {
            workingDir(this@BazelTask.workingDir.get())
            commandLine(cmd)
            isIgnoreExitValue = false
        }
        result.assertNormalExitValue()
    }

    private fun execCapture(cmd: List<String>): String {
        val stdout = java.io.ByteArrayOutputStream()
        val stderr = java.io.ByteArrayOutputStream()
        val result = execOps.exec {
            workingDir(this@BazelTask.workingDir.get())
            commandLine(cmd)
            standardOutput = stdout
            errorOutput = stderr
            isIgnoreExitValue = true
        }
        if (result.exitValue != 0) {
            throw GradleException("Command failed (${result.exitValue}): ${cmd.joinToString(" ")}\n${stderr}")
        }
        return stdout.toString(Charsets.UTF_8)
    }

    private fun findExecutable(name: String): String? {
        val stdout = java.io.ByteArrayOutputStream()
        val result = execOps.exec {
            commandLine("which", name)
            standardOutput = stdout
            isIgnoreExitValue = true
        }
        return if (result.exitValue == 0) stdout.toString(Charsets.UTF_8).trim().takeIf { it.isNotEmpty() } else null
    }
}
