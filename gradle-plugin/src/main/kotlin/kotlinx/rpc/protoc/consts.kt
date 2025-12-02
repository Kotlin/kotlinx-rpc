/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc

import org.gradle.api.artifacts.Configuration

/**
 * `proto` group for related gradle tasks.
 */
public const val PROTO_GROUP: String = "proto"

/**
 * Container for proto source sets.
 */
public const val PROTO_SOURCE_SETS: String = "protoSourceSets"

/**
 * Name of the extension that is created for [org.gradle.api.tasks.SourceSet] instances.
 */
public const val PROTO_SOURCE_SET_EXTENSION_NAME: String = "proto"

/**
 * Name of the default source directory set for proto files in [PROTO_SOURCE_SETS].
 */
public const val PROTO_SOURCE_DIRECTORY_NAME: String = "proto"

/**
 * Directory for proto build artifacts.
 */
public const val PROTO_BUILD_DIR: String = "protoBuild"

/**
 * Directory for proto build generated files.
 */
public const val PROTO_BUILD_GENERATED: String = "generated"

/**
 * Directory for proto build temporary files.
 * Files there are constructed to form a valid Buf workspace.
 */
public const val PROTO_BUILD_SOURCE_SETS: String = "sourceSets"

/**
 * Source directory for proto files in [PROTO_BUILD_SOURCE_SETS].
 *
 * For these files the `buf generate` task will be called.
 */
public const val PROTO_FILES_DIR: String = "proto"

/**
 * Source directory for proto imported files in [PROTO_BUILD_SOURCE_SETS].
 *
 * These files are included as imports in the `buf generate` task, but don't get processed by it.
 */
public const val PROTO_FILES_IMPORT_DIR: String = "import"

/**
 * [Configuration] name for the `protoc-gen-kotlin-multiplatform` protoc plugin artifact.
 *
 * MUST be a single file.
 */
public const val PROTOC_GEN_KOTLIN_MULTIPLATFORM_JAR_CONFIGURATION: String = "protocGenKotlinMultiplatformJar"

/**
 * [Configuration] name for the `protoc-gen-grpc-kotlin-multiplatform` protoc plugin artifact.
 *
 * MUST be a single file.
 */
public const val PROTOC_GEN_GRPC_KOTLIN_MULTIPLATFORM_JAR_CONFIGURATION: String = "protocGenGrpcKotlinMultiplatformJar"
