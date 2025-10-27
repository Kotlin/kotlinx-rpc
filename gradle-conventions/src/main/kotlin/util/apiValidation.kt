/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

// marker-imports
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationVariantSpec
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
// /marker-imports

private val excludedProjects = setOf(
    "krpc-test",
    "krpc-compatibility-tests",
    "krpc-protocol-compatibility-tests",
    "compiler-plugin-tests",
    "protobuf-conformance",
    "protobuf-plugin",
)

val Project.enableAbiValidation get() = name !in excludedProjects

// marker-configureAbiFilters
@OptIn(ExperimentalAbiValidation::class)
fun AbiValidationVariantSpec.configureAbiFilters() {
    filters {
        @Suppress("DEPRECATION_ERROR") // todo: temp, remove after update to 2.3.20
        excluded {
            annotatedWith.add("kotlinx.rpc.internal.utils.InternalRpcApi")
            byNames.add("kotlinx.rpc.internal.**")
            byNames.add("kotlinx.rpc.krpc.internal.**")
            byNames.add("kotlinx.rpc.grpc.internal.**")
        }
    }
}
// /marker-configureAbiFilters