/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest
import java.io.File

/**
 * In-process crash backtrace shim for Kotlin/Native test binaries (KRPC-597).
 *
 * The shim source lives at `scripts/crashbt.c`. On SIGABRT/SIGSEGV/SIGBUS it
 * prints a per-thread backtrace + memory map to stderr, then re-raises under
 * the default disposition so the process still exits with the original signal.
 *
 * Wiring is two-part:
 *  - [registerBuildCrashbtShimTask] on the root project compiles the shim once
 *    per build into `build/crashbt/libcrashbt.{so,dylib}`.
 *  - [applyCrashbtShimToNativeTests] on every KMP subproject sets
 *    `LD_PRELOAD` (Linux) / `DYLD_INSERT_LIBRARIES` (macOS) on each
 *    [KotlinNativeTest] task so any native test crash surfaces with a
 *    backtrace in the build log + per-test stderr captured by Gradle.
 *
 * On unsupported hosts (Windows, anything that isn't Linux/macOS) both calls
 * are no-ops -- the shim only covers signal-driven crashes which are not the
 * relevant failure mode on Windows; a future Windows-side implementation would
 * use SetUnhandledExceptionFilter + MiniDumpWriteDump.
 *
 * Why an in-process shim and not gdb-batch: Kotlin/Native installs its own
 * SIGSEGV-as-safepoint and SIGBUS handlers during runtime init. ptrace via gdb
 * intercepts those signals first and breaks the runtime's invariants -- every
 * iteration fails fast before doing anything (TC build #54004 confirmed this).
 * The LD_PRELOAD'd sigaction handler does not perturb execution.
 */

private val osName = System.getProperty("os.name").orEmpty()
private val isLinuxHost = osName.startsWith("Linux", ignoreCase = true)
private val isMacosHost = osName.startsWith("Mac", ignoreCase = true)

internal const val BUILD_CRASHBT_SHIM_TASK_NAME = "buildCrashbtShim"

private fun crashbtShimFile(rootDir: File): File? = when {
    isLinuxHost -> rootDir.resolve("build/crashbt/libcrashbt.so")
    isMacosHost -> rootDir.resolve("build/crashbt/libcrashbt.dylib")
    else -> null
}

/**
 * Registers `:buildCrashbtShim` on the root project. The task is a no-op on
 * unsupported hosts. Output is `build/crashbt/libcrashbt.{so,dylib}`.
 */
fun Project.registerBuildCrashbtShimTask() {
    check(this == rootProject) { "registerBuildCrashbtShimTask must be called on the root project" }

    val src = rootDir.resolve("scripts/crashbt.c")
    val out = crashbtShimFile(rootDir)

    tasks.register(BUILD_CRASHBT_SHIM_TASK_NAME, Exec::class.java) {
        group = "verification"
        description = "Compiles the LD_PRELOAD/DYLD_INSERT_LIBRARIES crash backtrace shim used by native test tasks (KRPC-597)."

        if (out == null || !src.exists()) {
            // Unsupported host or source missing -- no-op task so dependents can stay declarative.
            enabled = false
            return@register
        }

        inputs.file(src)
        outputs.file(out)

        doFirst {
            out.parentFile.mkdirs()
        }

        when {
            isLinuxHost -> commandLine(
                "gcc", "-O0", "-g", "-fPIC", "-shared",
                "-o", out.absolutePath, src.absolutePath, "-ldl",
            )
            isMacosHost -> commandLine(
                "clang", "-O0", "-g", "-dynamiclib",
                "-o", out.absolutePath, src.absolutePath,
            )
        }
    }
}

/**
 * Wires the compiled shim into every [KotlinNativeTest] task in this project.
 * Sets `LD_PRELOAD` (Linux) / `DYLD_INSERT_LIBRARIES` + `DYLD_FORCE_FLAT_NAMESPACE`
 * (macOS) on the test process and adds a `dependsOn` on the root-level shim
 * build task. On unsupported hosts this is a no-op.
 */
fun Project.applyCrashbtShimToNativeTests() {
    val shim = crashbtShimFile(rootDir) ?: return

    val shimTask = rootProject.tasks.named(BUILD_CRASHBT_SHIM_TASK_NAME)

    tasks.withType(KotlinNativeTest::class.java).configureEach {
        dependsOn(shimTask)

        when {
            isLinuxHost -> environment("LD_PRELOAD", shim.absolutePath)
            isMacosHost -> {
                // DYLD_INSERT_LIBRARIES is the macOS analogue of LD_PRELOAD.
                // DYLD_FORCE_FLAT_NAMESPACE=1 is required for the inserted library's
                // sigaction symbols to bind ahead of the kexe's own resolution path
                // when the test binary uses two-level namespace (the K/N default).
                environment("DYLD_INSERT_LIBRARIES", shim.absolutePath)
                environment("DYLD_FORCE_FLAT_NAMESPACE", "1")
            }
        }
    }
}
