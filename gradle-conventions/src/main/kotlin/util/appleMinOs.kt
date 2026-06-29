/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import util.other.libs
import java.io.File

// Apple minimum-OS pinning for the native-deps Bazel builds (KRPC-609).
//
// Without an explicit `--<platform>_minimum_os`, Bazel stamps Apple object files with a minimum
// deployment target equal to the build machine's SDK version. That leaked the publisher's SDK (e.g.
// iOS 26.2) into the shipped gRPC/shim artifacts and forced downstream apps to raise their deployment
// target. We pin each Apple target to the SAME minimum that Kotlin/Native itself uses for that target
// (so the native artifacts never become the limiting factor), sourced from the Kotlin/Native
// distribution's konan.properties so the values track whatever Kotlin version the project currently uses.

/**
 * Apple platform "family" (Bazel config-name prefix) -> the Bazel flag that pins its minimum OS.
 */
private val appleMinOsFlagByFamily: Map<String, String> = linkedMapOf(
    "macos" to "macos_minimum_os",
    "ios" to "ios_minimum_os",
    "tvos" to "tvos_minimum_os",
    "watchos" to "watchos_minimum_os",
)

/**
 * Bazel config names (identical to Kotlin/Native target names) for every Apple target the native-deps
 * Bazel builds cross-compile. Kept explicit — rather than parsed out of each `.bazelrc` — so the
 * generated dump is stable and reviewable. This must stay in sync with the Apple `build:<config>` lines
 * shared by every native-deps `.bazelrc`.
 */
internal val appleBazelConfigs: List<String> = listOf(
    "macos_arm64",
    "ios_arm64", "ios_simulator_arm64", "ios_x64",
    "tvos_arm64", "tvos_simulator_arm64",
    "watchos_arm32", "watchos_arm64", "watchos_device_arm64", "watchos_simulator_arm64",
)

/** Whether this native dependency target is an Apple target (and therefore carries a minimum OS). */
fun NativeDependencyTarget.isApple(): Boolean =
    appleMinOsFlagByFamily.keys.any { bazelName == it || bazelName.startsWith("${it}_") }

private fun appleFamilyOf(config: String): String =
    appleMinOsFlagByFamily.keys.firstOrNull { config == it || config.startsWith("${it}_") }
        ?: error("Cannot determine Apple platform family for Bazel config '$config'")

/**
 * Reads `osVersionMin.<target>` from the Kotlin/Native distribution's konan.properties, resolving the
 * `$minVersion.<family>` indirections, and returns `target -> minimum OS version`.
 */
internal fun readKonanOsVersionMin(konanHome: File): Map<String, String> {
    val propertiesFile = konanHome.resolve("konan").resolve("konan.properties")
    check(propertiesFile.isFile) { "konan.properties not found at ${propertiesFile.absolutePath}" }

    val raw = mutableMapOf<String, String>()
    propertiesFile.forEachLine { line ->
        val trimmed = line.trim()
        if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("!") || '=' !in trimmed) {
            return@forEachLine
        }
        raw[trimmed.substringBefore('=').trim()] = trimmed.substringAfter('=').trim()
    }

    fun resolve(value: String): String =
        if (value.startsWith("$")) {
            val reference = value.removePrefix("$")
            resolve(raw[reference] ?: error("Unresolved konan.properties reference '$value'"))
        } else {
            value
        }

    return raw.asSequence()
        .filter { it.key.startsWith("osVersionMin.") }
        .associate { it.key.removePrefix("osVersionMin.") to resolve(it.value) }
}

/**
 * Renders the checked-in dump. The dump is valid Bazel rc syntax (it is `import`ed by each `.bazelrc`
 * to actually pin the flags) and is also parsed by [parseAppleMinOsDump] for verification.
 */
internal fun renderAppleMinOsDump(kotlinNativeVersion: String, osVersionMin: Map<String, String>): String =
    buildString {
        appendLine("# Apple minimum OS (deployment target) pinning for the native-deps Bazel builds (KRPC-609).")
        appendLine("#")
        appendLine("# GENERATED from the Kotlin/Native $kotlinNativeVersion konan.properties (osVersionMin.*).")
        appendLine("# Do NOT edit by hand: run the 'generateAppleMinOsDump' Gradle task and commit the result.")
        appendLine("# This file is imported by .bazelrc (to pin --<platform>_minimum_os) and read by the")
        appendLine("# 'appleMinOsTest' verification task. It exists so the artifacts inherit Kotlin/Native's own")
        appendLine("# per-target deployment target instead of the build machine's SDK version.")
        appendLine()
        appleBazelConfigs.forEach { config ->
            val flag = appleMinOsFlagByFamily.getValue(appleFamilyOf(config))
            val version = osVersionMin[config]
                ?: error("konan.properties has no osVersionMin.$config entry for Kotlin/Native $kotlinNativeVersion")
            appendLine("build:$config --$flag=$version")
        }
    }

private val dumpLineRegex = Regex("""^build:(\S+)\s+--\w+_minimum_os=(\S+)$""")

/** Parses a generated dump back into `bazelConfig -> minimum OS version`. */
internal fun parseAppleMinOsDump(dumpFile: File): Map<String, String> =
    dumpFile.readLines().mapNotNull { line ->
        dumpLineRegex.matchEntire(line.trim())?.let { it.groupValues[1] to it.groupValues[2] }
    }.toMap()

/**
 * Registers `generateAppleMinOsDump` (writes [dumpFile] from the current Kotlin/Native konan.properties)
 * and `checkAppleMinOsDump` (fails if the checked-in dump is stale). The check is cheap (no native
 * build) and is meant to be wired into `check` so a Kotlin/Native bump that changes deployment targets
 * is surfaced as an explicit, reviewable update.
 */
fun Project.registerAppleMinOsDumpTasks(
    konanHome: Provider<String>,
    dumpFile: File,
    dependsOn: List<Any> = emptyList(),
): Pair<TaskProvider<Task>, TaskProvider<Task>> {
    val kotlinNativeVersion = libs.versions.kotlin.lang.get()
    // The konan.properties of the resolved Kotlin/Native distribution is the source of the pinned
    // versions, so it (plus the version and the config list) fully determines the generated dump.
    val konanProperties = konanHome.map { File(it).resolve("konan").resolve("konan.properties") }
    val checkMarker = layout.buildDirectory.file("apple-min-os/dump-check-ok.txt")

    val generate = tasks.register("generateAppleMinOsDump") {
        group = "build"
        description = "Regenerates ${dumpFile.name} from the current Kotlin/Native konan.properties (osVersionMin.*)."
        dependsOn(dependsOn)
        inputs.file(konanProperties).withPropertyName("konanProperties").withPathSensitivity(PathSensitivity.NONE)
        inputs.property("kotlinNativeVersion", kotlinNativeVersion)
        inputs.property("appleBazelConfigs", appleBazelConfigs)
        outputs.file(dumpFile).withPropertyName("dump")
        doLast {
            val rendered = renderAppleMinOsDump(kotlinNativeVersion, readKonanOsVersionMin(File(konanHome.get())))
            dumpFile.writeText(rendered)
            logger.lifecycle("Wrote Apple minimum OS dump: ${dumpFile.absolutePath}")
        }
    }

    val checkTask = tasks.register("checkAppleMinOsDump") {
        group = "verification"
        description = "Fails if ${dumpFile.name} is stale vs the current Kotlin/Native konan.properties."
        dependsOn(dependsOn)
        // generate and check share the dump file (one writes it, the other reads it). They are not
        // meant to run together, but order them so Gradle's graph stays valid if they ever do.
        mustRunAfter(generate)
        inputs.file(konanProperties).withPropertyName("konanProperties").withPathSensitivity(PathSensitivity.NONE)
        inputs.file(dumpFile).withPropertyName("dump").withPathSensitivity(PathSensitivity.NONE).optional(true)
        inputs.property("kotlinNativeVersion", kotlinNativeVersion)
        inputs.property("appleBazelConfigs", appleBazelConfigs)
        // A stamp output so the (input-only) verification is properly up-to-date when nothing changed.
        outputs.file(checkMarker).withPropertyName("marker")
        doLast {
            val expected = renderAppleMinOsDump(kotlinNativeVersion, readKonanOsVersionMin(File(konanHome.get())))
            val actual = dumpFile.takeIf { it.isFile }?.readText()
            check(actual == expected) {
                "${dumpFile.absolutePath} is out of date for Kotlin/Native $kotlinNativeVersion.\n" +
                    "Run the 'generateAppleMinOsDump' task and commit the result."
            }
            checkMarker.get().asFile.apply {
                parentFile.mkdirs()
                writeText("apple-min-os dump verified for Kotlin/Native $kotlinNativeVersion\n")
            }
        }
    }

    return generate to checkTask
}

/**
 * Verifies that every Mach-O object in the produced [archives] is stamped with the minimum OS recorded
 * for [bazelConfig] in the dump. Fails loudly on any mismatch — catching both an SDK version leaking in
 * (KRPC-609) and silent drift from the pinned values.
 */
abstract class VerifyAppleMinOsTask : DefaultTask() {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val archives: ConfigurableFileCollection

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val dumpFile: RegularFileProperty

    @get:Input
    abstract val bazelConfig: Property<String>

    @get:Input
    abstract val otoolPath: Property<String>

    @get:OutputFile
    abstract val reportFile: RegularFileProperty

    @TaskAction
    fun verify() {
        val config = bazelConfig.get()
        val expected = parseAppleMinOsDump(dumpFile.get().asFile)[config]
            ?: error("Dump ${dumpFile.get().asFile.name} has no entry for Bazel config '$config'")

        val archiveFiles = archives.files.filter { it.isFile && it.extension == "a" }
        check(archiveFiles.isNotEmpty()) {
            "No .a archives found to verify for Bazel config '$config'."
        }

        val mismatches = mutableListOf<String>()
        var verifiedObjects = 0
        archiveFiles.forEach { archive ->
            readArchiveMinOs(otoolPath.get(), archive).forEach { (objectName, minOs) ->
                verifiedObjects++
                if (minOs != expected) {
                    mismatches += "${archive.name} :: $objectName -> minos=$minOs (expected $expected)"
                }
            }
        }

        check(verifiedObjects > 0) {
            "Found no Mach-O minimum-OS load commands for '$config' in ${archiveFiles.size} archive(s); " +
                "cannot confirm the pinned minimum is honored."
        }
        check(mismatches.isEmpty()) {
            buildString {
                appendLine("Apple minimum OS mismatch for '$config' (expected $expected):")
                mismatches.forEach { appendLine("  $it") }
                append("These objects would force downstream consumers' deployment target (KRPC-609).")
            }
        }
        reportFile.get().asFile.apply {
            parentFile.mkdirs()
            writeText("verified $verifiedObjects object(s) for '$config' at minos=$expected\n")
        }
        logger.lifecycle("appleMinOs[$config]: verified $verifiedObjects object(s) at minos=$expected")
    }

    private fun readArchiveMinOs(otool: String, archive: File): Map<String, String> {
        val process = ProcessBuilder(otool, "-l", archive.absolutePath)
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().readText()
        val exitCode = process.waitFor()
        check(exitCode == 0) { "`$otool -l ${archive.absolutePath}` failed (exit $exitCode):\n$output" }
        return parseOtoolMinOs(output, archive.name)
    }
}

/**
 * Parses `otool -l` output (over a static archive) into `objectName -> minimum OS`. Handles both the
 * modern `LC_BUILD_VERSION` (with `minos`) and the legacy `LC_VERSION_MIN_*` (with `version`) load
 * commands. Objects without any version load command are simply absent from the result.
 */
internal fun parseOtoolMinOs(otoolOutput: String, archiveName: String): Map<String, String> {
    val result = linkedMapOf<String, String>()
    var currentObject = archiveName
    var pending: String? = null
    otoolOutput.lineSequence().forEach { rawLine ->
        val line = rawLine.trim()
        when {
            // `otool -l archive.a` prefixes each member's load commands with `archive.a(member.o):`.
            line.endsWith("):") && '(' in line -> {
                currentObject = line.substringAfterLast('(').removeSuffix("):")
                pending = null
            }
            line.startsWith("cmd LC_BUILD_VERSION") -> pending = "minos"
            line.startsWith("cmd LC_VERSION_MIN_") -> pending = "version"
            line.startsWith("cmd ") -> pending = null
            pending != null && line.startsWith("$pending ") -> {
                result[currentObject] = line.substringAfter(' ').trim()
                pending = null
            }
        }
    }
    return result
}

/**
 * Creates a [VerifyAppleMinOsTask] for one Bazel config. macOS-only (otool + Apple SDKs are required);
 * skipped on other hosts.
 */
fun Project.registerVerifyAppleMinOsTask(
    taskName: String,
    bazelConfig: String,
    dumpFile: File,
    archives: Any,
    dependsOn: List<Any>,
): TaskProvider<VerifyAppleMinOsTask> = tasks.register<VerifyAppleMinOsTask>(taskName) {
    group = "verification"
    description = "Verifies produced '$bazelConfig' objects carry the minimum OS pinned in ${dumpFile.name}."
    onlyIf { System.getProperty("os.name").lowercase().contains("mac") }
    this.dependsOn(dependsOn)
    this.bazelConfig.set(bazelConfig)
    this.dumpFile.set(dumpFile)
    this.archives.from(archives)
    this.otoolPath.convention("otool")
    this.reportFile.convention(layout.buildDirectory.file("apple-min-os/$bazelConfig.verified.txt"))
}
