/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.targets

import org.gradle.api.Project
import org.gradle.api.provider.ListProperty

private const val XCODE_BUILD_CONFIGURATION_PROPERTY = "kotlinx.rpc.grpc.swift.xcodeBuildConfiguration"
private const val XCODE_BUILD_CONFIGURATION_DEFAULT = "Release"
private val supportedXcodeBuildConfigurations = setOf("Debug", XCODE_BUILD_CONFIGURATION_DEFAULT)

/**
 * Sets the Xcode build configuration for the Kotlin Gradle plugin SwiftPM import.
 *
 * The configuration comes from the `kotlinx.rpc.grpc.swift.xcodeBuildConfiguration` Gradle property.
 * The default is `Release`. Set `Debug` when build time matters more than runtime performance,
 * for example on CI.
 *
 * Remove this workaround when KT-83900 is fixed.
 */
fun Project.configureSwiftPMXcodeBuildConfiguration() {
    val configuration = providers.gradleProperty(XCODE_BUILD_CONFIGURATION_PROPERTY)
        .getOrElse(XCODE_BUILD_CONFIGURATION_DEFAULT)

    if (configuration !in supportedXcodeBuildConfigurations) {
        error(
            "Invalid value '$configuration' for the '$XCODE_BUILD_CONFIGURATION_PROPERTY' Gradle property. " +
                "Use 'Debug' or 'Release'."
        )
    }

    tasks.matching { task ->
        task.name.startsWith("fingerprintXcodebuild") ||
            task.name.startsWith("dumpXcodebuildArgs")
    }.configureEach {
        @Suppress("UNCHECKED_CAST")
        val xcodebuildArguments = property("additionalXcodeArgs") as ListProperty<String>
        xcodebuildArguments.addAll("-configuration", configuration)
    }
}
