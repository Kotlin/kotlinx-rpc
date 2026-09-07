/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

// marker-imports
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationExtension
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
// /marker-imports

private val excludedProjects = setOf(
    "krpc-test",
    "krpc-compatibility-tests",
    "krpc-protocol-compatibility-tests",
    "test-api",
    "compiler-plugin-tests",
    "compiler-plugin-protos",
    "protobuf-conformance",
    "protobuf-unittest",
    "protobuf-playground",
    "grpc-test-server",
    "test-protos",
)

val Project.enableAbiValidation get() = name !in excludedProjects

// marker-configureAbiFilters
@OptIn(ExperimentalAbiValidation::class)
fun AbiValidationExtension.configureAbiFilters() {
    filters {
        exclude {
            annotatedWith.add("kotlinx.rpc.internal.utils.InternalRpcApi")
            byNames.add("kotlinx.rpc.internal.**")
            byNames.add("kotlinx.rpc.krpc.internal.**")
            byNames.add("kotlinx.rpc.grpc.internal.**")
            byNames.add("kotlinx.rpc.protobuf.internal.**")
        }
    }
}
// /marker-configureAbiFilters