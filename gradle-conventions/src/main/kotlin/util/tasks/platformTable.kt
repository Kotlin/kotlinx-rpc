/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.targets.js.KotlinWasmTargetType
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinWasmJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinWasmWasiTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import util.KOTLIN_JVM_PLUGIN_ID
import util.KOTLIN_MULTIPLATFORM_PLUGIN_ID
import util.other.isPublicModule
import util.other.optionalPropertyValue
import java.io.File
import java.nio.file.Files

private val PLATFORM_TOPIC_PATH = File("docs/pages/kotlinx-rpc/topics/platforms.topic")

private const val TABLE_START_TAG = "PLATFORMS_TABLE_START"
private const val TABLE_END_TAG = "PLATFORMS_TABLE_END"

enum class TableColumn {
    Jvm, Js, Wasm, Native;
}

sealed interface PlatformTarget {
    val name: String
    val subtargets: List<PlatformTarget>
    val stub: Boolean get() = false

    class Group(
        override val name: String,
        override val subtargets: List<PlatformTarget>,
        override val stub: Boolean,
    ) : PlatformTarget

    class LeafTarget(override val name: String, override val stub: Boolean) : PlatformTarget {
        override val subtargets: List<PlatformTarget> = emptyList()
    }
}

sealed interface PlatformsDescription {
    object None : PlatformsDescription

    object JvmOnly : PlatformsDescription

    class Kmp(val platforms: Map<KotlinPlatformType, PlatformTarget>) : PlatformsDescription
}

abstract class DumpPlatformsTask : DefaultTask() {
    @get:InputFile
    abstract val input: Property<File>

    @get:OutputFile
    abstract val output: Property<File>

    @TaskAction
    fun dump() {
        val input = input.get()
        if (!input.exists()) {
            throw GradleException("File ${input.absolutePath} doesn't exist")
        }

        val output = output.get()
        if (!output.exists()) {
            Files.createFile(output.toPath())
        }

        val modules = collectPlatforms()

        updateTable(input, output, modules)
    }

    private fun collectPlatforms(): Map<String, PlatformsDescription> {
        return project.rootProject.allprojects.mapNotNull { subproject ->
            if (!subproject.isPublicModule) {
                return@mapNotNull null
            }

            val platforms: PlatformsDescription = when {
                subproject.plugins.hasPlugin(KOTLIN_JVM_PLUGIN_ID) -> {
                    PlatformsDescription.JvmOnly
                }

                subproject.plugins.hasPlugin(KOTLIN_MULTIPLATFORM_PLUGIN_ID) -> {
                    platformsDescriptionKmp(subproject)
                }

                else -> PlatformsDescription.None
            }

            subproject.name to platforms
        }.toMap()
    }

    private fun platformsDescriptionKmp(subproject: Project): PlatformsDescription.Kmp {
        val mapped = subproject
            .the<KotlinMultiplatformExtension>()
            .targets
            .groupBy { target ->
                target.platformType
            }
            .mapValues { (platform, targets) ->
                when (platform) {
                    KotlinPlatformType.jvm -> {
                        val isStub = subproject.optionalPropertyValue(targets[0].name, "stub")
                        PlatformTarget.LeafTarget(TableColumn.Jvm.name.lowercase(), isStub)
                    }

                    KotlinPlatformType.js -> {
                        val (target, subtargets) = targets.jsOrWasmSubTargets("js", subproject) {
                            it is KotlinJsIrTarget
                        } ?: return@mapValues null

                        val isStub = subproject.optionalPropertyValue(target.name, "stub")
                        PlatformTarget.Group(TableColumn.Js.name, subtargets, isStub)
                    }

                    KotlinPlatformType.wasm -> {
                        val (jsTarget, jsSubtargets) = targets.jsOrWasmSubTargets("wasmJs", subproject) {
                            it is KotlinWasmJsTargetDsl && it.wasmTargetType == KotlinWasmTargetType.JS
                        } ?: (null to emptyList())

                        val (wasiTarget, wasiSubtargets) = targets.jsOrWasmSubTargets("wasmWasi", subproject) {
                            it is KotlinWasmWasiTargetDsl && it.wasmTargetType == KotlinWasmTargetType.WASI
                        } ?: (null to emptyList())

                        val jsIsStub = jsTarget?.name
                            ?.let { subproject.optionalPropertyValue(it, "stub") } ?: false

                        val wasiIsStub = wasiTarget?.name
                            ?.let { subproject.optionalPropertyValue(it, "stub") } ?: false

                        val wasmSubtargets = listOfNotNull(
                            PlatformTarget.Group("wasmJs", jsSubtargets, jsIsStub).takeIf { jsSubtargets.isNotEmpty() },
                            PlatformTarget.Group("wasmWasi", wasiSubtargets, wasiIsStub).takeIf { wasiSubtargets.isNotEmpty() },
                        )

                        PlatformTarget.Group(
                            name = TableColumn.Wasm.name,
                            subtargets = wasmSubtargets,
                            stub = subproject.optionalPropertyValue(TableColumn.Wasm.name, "stub"),
                        )
                    }

                    KotlinPlatformType.native -> {
                        PlatformTarget.Group(
                            name = TableColumn.Native.name,
                            subtargets = listOf(
                                PlatformTarget.Group(
                                    name = "apple",
                                    subtargets = listOf(
                                        targets.nativeGroup(subproject, "ios"),
                                        targets.nativeGroup(subproject, "macos"),
                                        targets.nativeGroup(subproject, "watchos"),
                                        targets.nativeGroup(subproject, "tvos"),
                                    ),
                                    stub = subproject.optionalPropertyValue("apple", "stub"),
                                ),
                                targets.nativeGroup(subproject, "linux"),
                                targets.nativeGroup(subproject, "windows", "mingw"),
                            ),
                            stub = subproject.optionalPropertyValue(TableColumn.Native.name, "stub"),
                        )
                    }

                    KotlinPlatformType.common, KotlinPlatformType.androidJvm -> null
                }
            }
            .mapNotNull { (platform, target) -> target?.let { platform to it } }
            .toMap()

        return PlatformsDescription.Kmp(mapped)
    }

    private fun List<KotlinTarget>.jsOrWasmSubTargets(
        name: String,
        project: Project,
        condition: (KotlinTarget) -> Boolean,
    ): Pair<KotlinTarget, List<PlatformTarget.LeafTarget>>? {
        val foundTargets = filter(condition)
            .filterIsInstance<KotlinJsIrTarget>()

        if (foundTargets.isEmpty()) {
            return null
        }

        if (foundTargets.size > 1) {
            error("Multiple $name targets are not supported (project ${project.name})")
        }

        val target = foundTargets.single()
        val jsSubtargets = target.subTargets.map { subTargetWithBinary ->
            val isStub = project.optionalPropertyValue(subTargetWithBinary.name, "stub", target.name)
            PlatformTarget.LeafTarget(subTargetWithBinary.name, isStub)
        }

        return target to jsSubtargets
    }

    private fun List<KotlinTarget>.nativeGroup(subproject: Project, name: String, prefix: String = name): PlatformTarget.Group {
        return PlatformTarget.Group(
            name = name,
            subtargets = filter { it.name.startsWith(prefix) }.map {
                PlatformTarget.LeafTarget(it.name, subproject.optionalPropertyValue(it.name, "stub"))
            },
            stub = subproject.optionalPropertyValue(name, "stub")
        )
    }

    private fun updateTable(input: File, output: File, modules: Map<String, PlatformsDescription>) {
        val original = input.readLines(Charsets.UTF_8)
        val tableStart = original.indexOfFirst { it.contains(TABLE_START_TAG) }
        val tableEnd = original.indexOfFirst { it.contains(TABLE_END_TAG) }

        if (tableStart == -1 || tableEnd == -1) {
            throw GradleException("Table start and end tags are not found in the file ${input.absolutePath}")
        }

        val newLines = modules.mapNotNull { (moduleName, description) ->
            if (description == PlatformsDescription.None) {
                return@mapNotNull null
            }

            buildString {
                fun cell(text: String) {
                    appendLine("<td>$text</td>")
                }

                fun writeRecursive(target: PlatformTarget?, topLevel: Boolean): String = buildString {
                    when (target) {
                        null -> append("-")

                        is PlatformTarget.LeafTarget -> {
                            append(target.name)
                            if (target.stub) {
                                append(" <b>[stub]</b>")
                            }
                        }

                        is PlatformTarget.Group -> {
                            when {
                                !topLevel -> {
                                    append(target.name)
                                    if (target.stub) {
                                        append(" <b>[stub]</b>")
                                    }
                                }

                                target.stub -> {
                                    append("<b>[stubs]</b>")
                                }
                            }

                            append("<list>")
                            target.subtargets.forEach { subtarget ->
                                append("<li>")
                                append(writeRecursive(subtarget, topLevel = false))
                                append("</li>")
                            }
                            append("</list>")
                        }
                    }
                }

                appendLine("<tr>")

                cell(moduleName)

                when (description) {
                    is PlatformsDescription.JvmOnly -> {
                        cell("Jvm Only")
                        cell("-")
                        cell("-")
                        cell("-")
                    }

                    is PlatformsDescription.Kmp -> {
                        cell(writeRecursive(description.platforms[KotlinPlatformType.jvm], topLevel = true))
                        cell(writeRecursive(description.platforms[KotlinPlatformType.js], topLevel = true))
                        cell(writeRecursive(description.platforms[KotlinPlatformType.wasm], topLevel = true))
                        cell(writeRecursive(description.platforms[KotlinPlatformType.native], topLevel = true))
                    }

                    is PlatformsDescription.None -> {
                        error("Unexpected None platforms description for module '$moduleName'")
                    }
                }

                appendLine("</tr>")
            }
        }

        output.bufferedWriter(Charsets.UTF_8).use { writer ->
            original.subList(0, tableStart + 1).forEach { line ->
                writer.appendLine(line)
            }
            newLines.forEach { line ->
                writer.appendLine(line)
            }
            original.subList(tableEnd, original.size).forEach { line ->
                writer.appendLine(line)
            }
        }
    }
}

fun Project.registerDumpPlatformTableTask() {
    tasks.register<DumpPlatformsTask>("dumpPlatformTable") {
        input.set(PLATFORM_TOPIC_PATH)
        output.set(PLATFORM_TOPIC_PATH)
    }
}

fun Project.registerVerifyPlatformTableTask() {
    tasks.register<DumpPlatformsTask>("verifyPlatformTable") {
        val tempFile = Files.createTempFile("platform-table-temp", ".topic").toFile()

        input.set(PLATFORM_TOPIC_PATH)
        output.set(tempFile)

        doLast {
            val input = input.get()
            val output = output.get()

            if (input.readText() != output.readText()) {
                throw GradleException(
                    "Platform table is not up-to-date. " +
                            "Run `./gradlew dumpPlatformTable --no-configuration-cache` to update it."
                )
            }
        }
    }
}
