/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.buf

import kotlinx.rpc.BUF_TOOL_VERSION
import kotlinx.rpc.buf.tasks.BufExecTask
import kotlinx.rpc.util.ProcessRunner
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

// See: https://github.com/bufbuild/buf-gradle-plugin/blob/1bc48078880887797db3aa412d6a3fea60461276/src/main/kotlin/build/buf/gradle/BufSupport.kt#L28
internal fun Project.configureBufExecutable() {
    configurations.create(BUF_EXECUTABLE_CONFIGURATION)

    val os = System.getProperty("os.name").lowercase()
    val osPart =
        when {
            os.startsWith("windows") -> "windows"
            os.startsWith("linux") -> "linux"
            os.startsWith("mac") -> "osx"
            else -> error("unsupported os: $os")
        }

    val archPart =
        when (val arch = System.getProperty("os.arch").lowercase()) {
            in setOf("x86_64", "amd64") -> "x86_64"
            in setOf("arm64", "aarch64") -> "aarch_64"
            else -> error("unsupported arch: $arch")
        }

    dependencies {
        add(
            BUF_EXECUTABLE_CONFIGURATION,
            mapOf(
                "group" to "build.buf",
                "name" to "buf",
                "version" to BUF_TOOL_VERSION,
                "classifier" to "$osPart-$archPart",
                "ext" to "exe",
            ),
        )
    }
}

internal fun BufExecTask.execBuf(args: Iterable<Any>) {
    val executable = bufExecutable.get()

    if (!executable.canExecute()) {
        executable.setExecutable(true)
    }

    val baseArgs = buildList {
        val configValue = configFile.orNull
        if (configValue != null) {
            add("--config")
            add(configValue.path)
        }

        if (debug.get()) {
            add("--debug")
        }

        val logFormatValue = logFormat.get()
        if (logFormatValue != BufExtension.LogFormat.Default) {
            add("--log-format")
            add(logFormatValue.name.lowercase())
        }

        val timeoutValue = bufTimeoutInWholeSeconds.get()
        if (timeoutValue != 0L) {
            add("--timeout")
            add("${timeoutValue}s")
        }
    }

    val processArgs = listOf(executable.absolutePath) + args + baseArgs

    val workingDirValue = workingDir.get()

    logger.debug("Running buf from {}: `buf {}`", workingDirValue, processArgs.joinToString(" "))

    val result = ProcessRunner().use { it.shell("buf", workingDirValue, processArgs) }

    if (result.exitCode != 0) {
        throw GradleException(result.formattedOutput())
    } else {
        logger.debug(result.formattedOutput())
    }
}
