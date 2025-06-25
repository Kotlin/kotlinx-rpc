/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationExtension
import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationVariantSpec
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

@OptIn(ExperimentalAbiValidation::class)
fun Project.configureApiValidation() {
    when (project.name) {
        "krpc-test",
        "krpc-compatibility-tests",
        "compiler-plugin-tests",
            -> return
    }

    withKotlinJvmExtension {
        extensions.configure<AbiValidationExtension> {
            enabled.set(true)

            configureFilters()
        }
    }

    withKotlinKmpExtension {
        extensions.configure<AbiValidationMultiplatformExtension> {
            enabled.set(true)

            klib {
                enabled.set(true)
            }

            configureFilters()
        }
    }
}

@OptIn(ExperimentalAbiValidation::class)
private fun AbiValidationVariantSpec.configureFilters() {
    filters {
        excluded {
            annotatedWith.add("kotlinx.rpc.internal.utils.InternalRpcApi")
            byNames.add("kotlinx.rpc.internal.**")
            byNames.add("kotlinx.rpc.krpc.internal.**")
        }
    }
}
