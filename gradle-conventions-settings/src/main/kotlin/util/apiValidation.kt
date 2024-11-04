/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import kotlinx.validation.ApiValidationExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the

fun Project.configureApiValidation() {
    plugins.apply(libs.plugins.binary.compatibility.validator.get().pluginId)

    the<ApiValidationExtension>().apply {
        ignoredPackages.add("kotlinx.rpc.internal")
        ignoredPackages.add("kotlinx.rpc.krpc.internal")

        ignoredProjects.addAll(
            listOf(
                "compiler-plugin-tests",
                "krpc-test",
                "utils",
            )
        )

        nonPublicMarkers.add("kotlinx.rpc.internal.utils.InternalRPCApi")
    }
}
