/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationVariantSpec
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

private val excludedProjects = setOf(
    "krpc-test",
    "krpc-compatibility-tests",
    "compiler-plugin-tests",
    "protobuf-plugin",
)

val Project.enableAbiValidation get() = name !in excludedProjects

@OptIn(ExperimentalAbiValidation::class)
fun AbiValidationVariantSpec.configureAbiFilters() {
    filters {
        excluded {
            annotatedWith.add("kotlinx.rpc.internal.utils.InternalRpcApi")
            byNames.add("kotlinx.rpc.internal.**")
            byNames.add("kotlinx.rpc.krpc.internal.**")
            byNames.add("kotlinx.rpc.grpc.internal.**")
        }
    }
}
