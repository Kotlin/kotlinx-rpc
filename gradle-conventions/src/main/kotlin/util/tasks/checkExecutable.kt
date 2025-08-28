/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * A task that verifies the existence of an executable in the system's PATH.
 *
 * This task is typically used to ensure that a specific executable is available before executing related tasks.
 * If the executable is not found, a GradleException is thrown with an appropriate error message.
 *
 * The task generates an output file containing the path to the executable if it is found.
 * The executable path can be accessed via the [execPath] property.
 */
abstract class CheckExecutableTask() : DefaultTask() {

    @get:Input
    abstract val exec: Property<String>

    @get:Input
    @get:Optional
    abstract val helpMessage: Property<String>

    @get:OutputFile
    abstract val output: RegularFileProperty

    @get:Inject
    abstract val execOps: ExecOperations

    @get:Internal
    val execPath: Provider<String> = project.providers.fileContents(output).asText.map { it.trim() }
    private val checkExecDir = project.layout.buildDirectory.dir("check-executable")

    init {
        group = "verification"
        description = "Checks that the executable file exists"
        output.convention(project.provider {
            checkExecDir.get().file(exec.get())
        })
    }


    @TaskAction
    fun check() {
        val exec = exec.get()
        val helpMessage = helpMessage.orElse("").get()
        val result = execOps.exec {
            commandLine("which", exec)
            isIgnoreExitValue = true
            standardOutput = output.get().asFile.outputStream()
        }
        if (result.exitValue != 0) {
            if (result.exitValue != 0) {
                throw GradleException("'$exec' not found on PATH. $helpMessage")
            }
        }
    }


}