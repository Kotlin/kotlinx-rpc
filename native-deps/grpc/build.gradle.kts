/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.bundling.Zip
import org.gradle.kotlin.dsl.register
import org.gradle.api.publish.maven.MavenPublication
import util.findKonanHome
import util.registerCheckBazelTask
import util.registerPrepareKonanHomeTask

plugins {
    base
    id("conventions-publishing")
}

group = "org.jetbrains.kotlinx"
val grpcVersion = providers.gradleProperty("grpcVersion").orNull
    ?: error("Missing grpcVersion in ${layout.projectDirectory.file("gradle.properties").asFile.absolutePath}")
version = grpcVersion

data class GrpcTarget(
    val bazelName: String,
    val publicationSuffix: String,
)

// Bazel target names and published artifact suffixes intentionally differ:
// Bazel uses Konan-style names, while Maven coordinates follow the repo's lowercase target naming.
val grpcTargets = listOf(
    GrpcTarget("ios_arm64", "iosarm64"),
    GrpcTarget("ios_simulator_arm64", "iossimulatorarm64"),
    GrpcTarget("ios_x64", "iosx64"),
    GrpcTarget("macos_arm64", "macosarm64"),
    GrpcTarget("macos_x64", "macosx64"),
    GrpcTarget("tvos_arm64", "tvosarm64"),
    GrpcTarget("tvos_simulator_arm64", "tvossimulatorarm64"),
    GrpcTarget("tvos_x64", "tvosx64"),
    GrpcTarget("watchos_arm32", "watchosarm32"),
    GrpcTarget("watchos_arm64", "watchosarm64"),
    GrpcTarget("watchos_device_arm64", "watchosdevicearm64"),
    GrpcTarget("watchos_simulator_arm64", "watchossimulatorarm64"),
    GrpcTarget("watchos_x64", "watchosx64"),
    GrpcTarget("linux_arm64", "linuxarm64"),
    GrpcTarget("linux_x64", "linuxx64"),
)

val konanHome = providers.provider { findKonanHome() }
val headersDir = layout.buildDirectory.dir("grpc/headers")
val moduleFile = layout.projectDirectory.file("MODULE.bazel").asFile
val checkBazel = registerCheckBazelTask()
val prepareKonanHome = registerPrepareKonanHomeTask(
    downloadTaskPath = ":grpc:grpc-core:downloadKotlinNativeDistribution",
)

val syncGrpcVersionToBazelModule = tasks.register("syncGrpcVersionToBazelModule") {
    doLast {
        val currentText = moduleFile.readText()
        val updatedText = currentText.replaceGrpcVersion(grpcVersion)
        check(updatedText != currentText || currentText.contains("""GRPC_VERSION = "$grpcVersion"""")) {
            "Failed to sync GRPC_VERSION in ${moduleFile.absolutePath}"
        }
        if (updatedText != currentText) {
            moduleFile.writeText(updatedText)
        }
    }
}

val checkKonanHome = tasks.register("checkKonanHome") {
    dependsOn(prepareKonanHome)
    doLast {
        val dir = file(konanHome.get())
        check(dir.isDirectory) {
            "KONAN_HOME does not exist: ${dir.absolutePath}"
        }
    }
}

val buildGrpcHeaders = tasks.register<Exec>("buildGrpcHeaders") {
    dependsOn(syncGrpcVersionToBazelModule, checkBazel, checkKonanHome)
    group = "build"
    workingDir = layout.projectDirectory.asFile
    val outputDir = headersDir.get().asFile
    outputs.dir(outputDir)
    commandLine(
        "./extract_include_dir.sh",
        ":grpc_include_dir",
        outputDir.absolutePath,
        konanHome.get(),
    )
}

val buildAllGrpcBundles = tasks.register("buildAllGrpcBundles") {
    group = "build"
}

publishing {
    publications {
        val headersBundle = tasks.register<Zip>("packageGrpcHeaders") {
            dependsOn(buildGrpcHeaders)
            group = "build"
            archiveBaseName.set("grpc-core-c-headers")
            destinationDirectory.set(layout.buildDirectory.dir("distributions"))

            from(headersDir) {
                into("")
            }
        }

        buildAllGrpcBundles.configure {
            dependsOn(headersBundle)
        }

        create<MavenPublication>("grpcHeaders") {
            artifactId = "kotlinx-rpc-grpc-core-c-headers"
            artifact(headersBundle)
        }

        grpcTargets.forEach { target ->
            val taskSuffix = target.bazelName.toTaskSuffix()
            val archivesDir = layout.buildDirectory.dir("grpc/${target.bazelName}/bundle")

            val buildArchives = tasks.register<Exec>("buildGrpc${taskSuffix}") {
                dependsOn(syncGrpcVersionToBazelModule, checkBazel, checkKonanHome)
                group = "build"
                workingDir = layout.projectDirectory.asFile
                outputs.dir(archivesDir)
                commandLine(
                    "./build_archives.sh",
                    // Build the local wrapper target rather than @grpc//:grpc directly so Bazel materializes every
                    // transitive archive that the bundle publication needs to copy.
                    ":grpc_archive_outputs",
                    archivesDir.get().asFile.absolutePath,
                    target.bazelName,
                    konanHome.get(),
                )
            }

            val bundle = tasks.register<Zip>("packageGrpc${taskSuffix}") {
                dependsOn(buildArchives)
                group = "build"
                archiveBaseName.set("grpc-core-c-deps-${target.publicationSuffix}")
                destinationDirectory.set(layout.buildDirectory.dir("distributions"))

                // Publish one self-contained native archive bundle per target. Headers are shared separately because
                // they are target-independent for a given gRPC version.
                from(archivesDir) {
                    into("")
                }
            }

            buildAllGrpcBundles.configure {
                dependsOn(bundle)
            }

            create<MavenPublication>("grpc${taskSuffix}") {
                artifactId = "kotlinx-rpc-grpc-core-c-deps-${target.publicationSuffix}"
                artifact(bundle)
            }
        }
    }
}

fun String.toTaskSuffix(): String = split('_').joinToString("") { part ->
    part.replaceFirstChar { char -> char.uppercase() }
}

fun String.replaceGrpcVersion(grpcVersion: String): String {
    val regex = Regex("""(?m)^GRPC_VERSION\s*=\s*"[^"]*"$""")
    check(regex.containsMatchIn(this)) {
        "Failed to find GRPC_VERSION assignment in ${moduleFile.absolutePath}"
    }
    return replace(regex, """GRPC_VERSION = "$grpcVersion"""")
}
