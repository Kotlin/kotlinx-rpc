/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.api.tasks.Exec
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.api.tasks.bundling.Zip
import org.gradle.kotlin.dsl.register
import org.gradle.api.publish.maven.MavenPublication
import util.konanHomeProvider
import util.nativeDependencyTargets
import util.registerCheckBazelTask
import util.registerCheckKonanHomeTask
import util.registerPrepareKonanHomeTask
import util.requireGradleProperty
import util.toTaskSuffix

plugins {
    base
    id("conventions-publishing")
}

group = "org.jetbrains.kotlinx"
val grpcVersion = requireGradleProperty("grpcVersion")
version = grpcVersion

// Bazel target names and published artifact suffixes intentionally differ:
// Bazel uses Konan-style names, while Maven coordinates follow the repo's lowercase target naming.
val grpcTargets = nativeDependencyTargets

val konanHome = konanHomeProvider()
val headersDir = layout.buildDirectory.dir("grpc/headers")
val moduleFile = layout.projectDirectory.file("MODULE.bazel").asFile
val checkBazel = registerCheckBazelTask()
val prepareKonanHome = registerPrepareKonanHomeTask(
    downloadTaskPath = ":grpc:grpc-core:downloadKotlinNativeDistribution",
)
val checkKonanHome = registerCheckKonanHomeTask(
    prepareKonanHome = prepareKonanHome,
    konanHome = konanHome,
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

val buildGrpcHeaders = tasks.register<Exec>("buildGrpcHeaders") {
    dependsOn(syncGrpcVersionToBazelModule, checkBazel, checkKonanHome)
    group = "build"
    workingDir = layout.projectDirectory.asFile
    val outputDir = headersDir.get().asFile
    outputs.dir(outputDir)
    commandLine(
        "./extract_headers_dir.sh",
        ":grpc_headers_dir",
        outputDir.absolutePath,
        konanHome.get(),
    )
}

val buildAllGrpcBundles = tasks.register("buildAllGrpcBundles") {
    group = "build"
}
// gRPC native bundles must only go to the dedicated grpc package repository. Keeping the
// allowed set explicit here prevents accidental publication to the generic EAP / for-IDE repos
// that conventions-publishing would otherwise add for normal modules.
val allowedPublishRepositories = setOf("grpc", "buildRepo")

publishing {
    // conventions-publishing adds the shared repository set for normal modules. This standalone
    // build is intentionally stricter, so trim it down to the grpc repo and the local build repo.
    repositories.toList()
        .filter { it.name !in allowedPublishRepositories }
        .forEach { repositories.remove(it) }

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

fun String.replaceGrpcVersion(grpcVersion: String): String {
    val regex = Regex("""(?m)^GRPC_VERSION\s*=\s*"[^"]*"$""")
    check(regex.containsMatchIn(this)) {
        "Failed to find GRPC_VERSION assignment in ${moduleFile.absolutePath}"
    }
    return replace(regex, """GRPC_VERSION = "$grpcVersion"""")
}
