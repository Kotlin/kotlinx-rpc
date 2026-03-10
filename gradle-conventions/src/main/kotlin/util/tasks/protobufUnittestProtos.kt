/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.tasks

import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.register
import util.other.libs

private const val PROTOBUF_SOURCE_ARCHIVE_CONFIGURATION = "protobufSourceArchive"
private const val EXTRACT_UNITTEST_PROTOS_TASK = "extractUnittestProtos"
private const val EXTRACT_UNITTEST_PROTO_IMPORTS_TASK = "extractUnittestProtoImports"

fun Project.setupProtobufUnittestProtos() {
    val protobufVersion = libs.versions.protobuf.get().substringAfter(".")

    // Ivy repository for GitHub releases (source archive has no platform classifier)
    repositories.ivy {
        name = "github-source-archive"
        url = uri("https://github.com")

        patternLayout {
            // https://github.com/protocolbuffers/protobuf/releases/download/v31.1/protobuf-31.1.zip
            artifact("[organisation]/[module]/releases/download/v[revision]/[artifact]-[revision].[ext]")
        }

        metadataSources { artifact() }
    }

    configurations.create(PROTOBUF_SOURCE_ARCHIVE_CONFIGURATION)

    dependencies {
        add(
            PROTOBUF_SOURCE_ARCHIVE_CONFIGURATION,
            dependencies.create("protocolbuffers:protobuf:$protobufVersion").apply {
                this as ExternalModuleDependency
                artifact {
                    name = "protobuf"
                    type = "zip"
                    extension = "zip"
                }
            },
        )
    }

    val archivePrefix = "protobuf-$protobufVersion/src/"

    val protosDir = project.layout.buildDirectory.dir("protobuf-unittest-protos")

    // Extract only unittest-related proto files (not well-known types which buf handles natively)
    val extractUnittestProtos = tasks.register<Copy>(EXTRACT_UNITTEST_PROTOS_TASK) {
        val archiveFiles = configurations.getByName(PROTOBUF_SOURCE_ARCHIVE_CONFIGURATION)

        from(archiveFiles.map { zipTree(it) })

        include(
            "${archivePrefix}google/protobuf/unittest*.proto",
            "${archivePrefix}google/protobuf/map_*unittest*.proto",
            "${archivePrefix}google/protobuf/edition_unittest*.proto",
        )

        eachFile {
            path = path.removePrefix(archivePrefix)
        }

        includeEmptyDirs = false
        into(protosDir)
    }

    val importsDir = project.layout.buildDirectory.dir("protobuf-unittest-imports")

    // Extract non-WKT import dependencies (e.g., cpp_features.proto) as import-only files
    val extractUnittestProtoImports = tasks.register<Copy>(EXTRACT_UNITTEST_PROTO_IMPORTS_TASK) {
        val archiveFiles = configurations.getByName(PROTOBUF_SOURCE_ARCHIVE_CONFIGURATION)

        from(archiveFiles.map { zipTree(it) })

        include("${archivePrefix}google/protobuf/cpp_features.proto")

        eachFile {
            path = path.removePrefix(archivePrefix)
        }

        includeEmptyDirs = false
        into(importsDir)
    }

    tasks.matching { it.name == "processMainProtoFiles" }.all {
        dependsOn(extractUnittestProtos)
    }

    tasks.matching { it.name == "processMainProtoFilesImports" }.all {
        dependsOn(extractUnittestProtoImports)
    }
}
