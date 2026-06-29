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
// target. We pin each Apple target to its real achievable minimum and verify every produced object
// carries exactly that value.
//
// The pinned value is max(Kotlin/Native osVersionMin, the architecture's real floor):
//  - Kotlin/Native's osVersionMin (from konan.properties) tracks whatever Kotlin version the project uses;
//  - the architecture's real floor is what clang will actually emit — most arches accept the K/N value,
//    but true-arm64 watchOS device only exists from watchOS 26 (Apple Watch S9/S10/Ultra2 moved off
//    arm64_32), so its real minimum is 26.x even though K/N nominally lists 9.0.
// Pinning the real minimum means clang has nothing to clamp, so the build deterministically emits exactly
// the pinned value and the verification can be a strict equality check.

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

/** The konan.properties file inside a Kotlin/Native distribution. */
internal fun konanPropertiesPath(konanHome: File): File = konanHome.resolve("konan").resolve("konan.properties")

/** Parses konan.properties into a raw `key -> value` map (no `$`-indirection resolution). */
private fun parseKonanProperties(propertiesFile: File): Map<String, String> {
    check(propertiesFile.isFile) { "konan.properties not found at ${propertiesFile.absolutePath}" }
    val raw = mutableMapOf<String, String>()
    propertiesFile.forEachLine { line ->
        val trimmed = line.trim()
        if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("!") || '=' !in trimmed) {
            return@forEachLine
        }
        raw[trimmed.substringBefore('=').trim()] = trimmed.substringAfter('=').trim()
    }
    return raw
}

/**
 * Reads `osVersionMin.<target>` from the Kotlin/Native distribution's konan.properties, resolving the
 * `$minVersion.<family>` indirections, and returns `target -> minimum OS version`.
 */
internal fun readKonanOsVersionMin(konanHome: File): Map<String, String> {
    val raw = parseKonanProperties(konanPropertiesPath(konanHome))
    fun resolve(value: String): String =
        if (value.startsWith("$")) {
            resolve(raw[value.removePrefix("$")] ?: error("Unresolved konan.properties reference '$value'"))
        } else {
            value
        }
    return raw.asSequence()
        .filter { it.key.startsWith("osVersionMin.") }
        .associate { it.key.removePrefix("osVersionMin.") to resolve(it.value) }
}

/**
 * The clang target triple Kotlin/Native uses for [config] (e.g. `arm64-apple-watchos`,
 * `arm64-apple-ios-simulator`), read from konan.properties. The triple encodes the arch + OS that
 * determine the deployment-target floor, so it is exactly what the floor probe needs.
 */
internal fun readKonanTargetTriple(konanPropertiesFile: File, config: String): String =
    parseKonanProperties(konanPropertiesFile)["targetTriple.$config"]
        ?: error("konan.properties has no targetTriple.$config entry")

/** Runs [command], merging stderr into stdout; returns (exitCode, output). */
internal fun runProcess(command: List<String>): Pair<Int, String> {
    val process = ProcessBuilder(command).redirectErrorStream(true).start()
    // Drain stdout (with stderr merged) fully before waiting, so a large output can't deadlock the pipe.
    val output = process.inputStream.bufferedReader().use { it.readText() }
    return process.waitFor() to output
}

/**
 * The minimum OS clang actually stamps for [triple] at [requestedVersion]. clang clamps the requested
 * version UP to the architecture's real floor when the platform has no OS that low (e.g. true-arm64
 * watchOS, which only exists from watchOS 26), so this returns `max(requestedVersion, archFloor)`.
 * Requires the Apple toolchain (clang/otool) — macOS only.
 */
internal fun probeAppleMinOs(triple: String, requestedVersion: String, workDir: File): String {
    val versionedTriple = if (triple.endsWith("-simulator")) {
        triple.removeSuffix("-simulator") + requestedVersion + "-simulator"
    } else {
        triple + requestedVersion
    }
    workDir.mkdirs()
    val stub = workDir.resolve("apple-min-os-probe.c")
    stub.writeText("int kotlinx_rpc_apple_min_os_probe(void) { return 0; }\n")
    val probeObject = workDir.resolve("apple-min-os-probe.o")
    val (clangExit, clangOutput) = runProcess(
        listOf("xcrun", "clang", "-target", versionedTriple, "-c", stub.absolutePath, "-o", probeObject.absolutePath),
    )
    check(clangExit == 0) { "`xcrun clang -target $versionedTriple` failed (exit $clangExit):\n$clangOutput" }
    val (otoolExit, otoolOutput) = runProcess(listOf("otool", "-l", probeObject.absolutePath))
    check(otoolExit == 0) { "`otool -l` on the probe object failed (exit $otoolExit):\n$otoolOutput" }
    return parseOtoolMinOs(otoolOutput, probeObject.name).values.firstOrNull()
        ?: error("Probe object (triple $versionedTriple) carries no minimum-OS load command.")
}

/**
 * Computes the pinned minimum OS for every Apple Bazel config: `max(Kotlin/Native osVersionMin, the
 * architecture's real floor)`. The floor is obtained by probing clang with the target's triple, so it
 * reflects what the build will actually emit. Requires the Apple toolchain — macOS only.
 */
internal fun computeApplePinnedMinOs(konanHome: File, workDir: File): Map<String, String> {
    val osVersionMin = readKonanOsVersionMin(konanHome)
    val konanProperties = konanPropertiesPath(konanHome)
    return appleBazelConfigs.associateWith { config ->
        val konanMin = osVersionMin[config]
            ?: error("konan.properties has no osVersionMin.$config entry")
        probeAppleMinOs(readKonanTargetTriple(konanProperties, config), konanMin, workDir)
    }
}

/**
 * Renders the checked-in dump. The dump is valid Bazel rc syntax (it is `import`ed by each `.bazelrc`
 * to actually pin the flags) and is also parsed by [parseAppleMinOsDump] for verification.
 */
internal fun renderAppleMinOsDump(kotlinNativeVersion: String, pinnedByConfig: Map<String, String>): String =
    buildString {
        appendLine("# Apple minimum OS (deployment target) pinning for the native-deps Bazel builds (KRPC-609).")
        appendLine("#")
        appendLine("# GENERATED — do NOT edit by hand: run the 'generateAppleMinOsDump' Gradle task (on macOS) and commit.")
        appendLine("# Each value is max(Kotlin/Native $kotlinNativeVersion osVersionMin, the architecture's real floor).")
        appendLine("# The arch floor only dominates for true-arm64 watchOS device, which exists solely on watchOS 26+,")
        appendLine("# so its minimum is 26.x even though Kotlin/Native nominally lists 9.0. Imported by .bazelrc to pin")
        appendLine("# --<platform>_minimum_os, and checked object-by-object by the 'appleMinOsTest' verification.")
        appendLine()
        appleBazelConfigs.forEach { config ->
            val flag = appleMinOsFlagByFamily.getValue(appleFamilyOf(config))
            val version = pinnedByConfig[config]
                ?: error("No pinned minimum was computed for Bazel config '$config'")
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
 * Registers `generateAppleMinOsDump` (writes [dumpFile] = max(Kotlin/Native osVersionMin, arch floor) per
 * Apple target) and `checkAppleMinOsDump` (fails if the checked-in dump is stale). Both probe the Apple
 * toolchain to learn the arch floor, so they only run on macOS; the cheap check is wired into `check` so a
 * Kotlin/Native or toolchain change that shifts a minimum is surfaced as an explicit, reviewable update.
 */
fun Project.registerAppleMinOsDumpTasks(
    konanHome: Provider<String>,
    dumpFile: File,
    dependsOn: List<Any> = emptyList(),
): Pair<TaskProvider<Task>, TaskProvider<Task>> {
    val kotlinNativeVersion = libs.versions.kotlin.lang.get()
    val konanProperties = konanHome.map { konanPropertiesPath(File(it)) }
    val probeDir = layout.buildDirectory.dir("apple-min-os/probe").get().asFile
    val checkMarker = layout.buildDirectory.file("apple-min-os/dump-check-ok.txt")

    val generate = tasks.register("generateAppleMinOsDump") {
        group = "build"
        description = "Regenerates ${dumpFile.name}: max(Kotlin/Native osVersionMin, arch floor) per Apple target."
        dependsOn(dependsOn)
        onlyIf { System.getProperty("os.name").lowercase().contains("mac") }
        inputs.file(konanProperties).withPropertyName("konanProperties").withPathSensitivity(PathSensitivity.NONE)
        inputs.property("kotlinNativeVersion", kotlinNativeVersion)
        inputs.property("appleBazelConfigs", appleBazelConfigs)
        outputs.file(dumpFile).withPropertyName("dump")
        doLast {
            val pinned = computeApplePinnedMinOs(File(konanHome.get()), probeDir)
            dumpFile.writeText(renderAppleMinOsDump(kotlinNativeVersion, pinned))
            logger.lifecycle("Wrote Apple minimum OS dump: ${dumpFile.absolutePath}")
        }
    }

    val checkTask = tasks.register("checkAppleMinOsDump") {
        group = "verification"
        description = "Fails if ${dumpFile.name} is stale vs the current Kotlin/Native + Apple toolchain."
        dependsOn(dependsOn)
        onlyIf { System.getProperty("os.name").lowercase().contains("mac") }
        // generate and check share the dump file (one writes it, the other reads it). They are not meant
        // to run together, but order them so Gradle's graph stays valid if they ever do.
        mustRunAfter(generate)
        inputs.file(konanProperties).withPropertyName("konanProperties").withPathSensitivity(PathSensitivity.NONE)
        inputs.file(dumpFile).withPropertyName("dump").withPathSensitivity(PathSensitivity.NONE).optional(true)
        inputs.property("kotlinNativeVersion", kotlinNativeVersion)
        inputs.property("appleBazelConfigs", appleBazelConfigs)
        // A stamp output so the (input-only) verification is properly up-to-date when nothing changed.
        outputs.file(checkMarker).withPropertyName("marker")
        doLast {
            val expected = renderAppleMinOsDump(
                kotlinNativeVersion,
                computeApplePinnedMinOs(File(konanHome.get()), probeDir),
            )
            val actual = dumpFile.takeIf { it.isFile }?.readText()
            check(actual == expected) {
                "${dumpFile.absolutePath} is out of date.\n" +
                    "Run the 'generateAppleMinOsDump' task (on macOS) and commit the result."
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
 * Verifies that every Mach-O object in the produced [archives] carries exactly the minimum OS pinned for
 * [bazelConfig] in the dump. Because the dump holds each target's real achievable minimum, this is a
 * strict equality check — it fails on any object stamped higher (an SDK version leaking in, KRPC-609) or
 * lower than the pin.
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
                appendLine("Apple minimum OS mismatch for '$config' (expected $expected): ${mismatches.size} object(s) differ.")
                mismatches.take(40).forEach { appendLine("  $it") }
                if (mismatches.size > 40) appendLine("  … and ${mismatches.size - 40} more")
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
        val (exitCode, output) = runProcess(listOf(otool, "-l", archive.absolutePath))
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
