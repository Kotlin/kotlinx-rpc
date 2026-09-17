/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.targets

import org.gradle.api.Project
import org.gradle.api.provider.ListProperty

/**
 * Configures Kotlin Gradle plugin SwiftPM import tasks to use the requested Xcode build configuration.
 *
 * Remove this workaround when KT-83900 is fixed.
 */
fun Project.configureSwiftPMXcodeBuildConfiguration(configuration: String) {
    tasks.matching { task ->
        task.name.startsWith("fingerprintXcodebuild") ||
            task.name.startsWith("dumpXcodebuildArgs")
    }.configureEach {
        @Suppress("UNCHECKED_CAST")
        val xcodebuildArguments = property("additionalXcodeArgs") as ListProperty<String>
        xcodebuildArguments.addAll("-configuration", configuration)
    }
}
