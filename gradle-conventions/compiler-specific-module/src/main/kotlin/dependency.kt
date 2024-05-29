/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

// configurations are available only when the kotlin plugin is applied, so we use lazy resolving
fun Project.lazyDependency(baseConfigurationName: String, notation: Any) {
    val kmpConfigurationName = "commonMain${capitalize(baseConfigurationName)}"
    this.configurations.matching { it.name == kmpConfigurationName }.all {
        this@lazyDependency.dependencies.add(kmpConfigurationName, notation)
    }
    this.configurations.matching { it.name == baseConfigurationName }.all {
        this@lazyDependency.dependencies.add(baseConfigurationName, notation)
    }
}

fun Project.lazyImplementation(notation: Any) {
    lazyDependency("implementation", notation)
}

fun KotlinSourceSet.vsDependencies(
    vsSourceSet: String,
    configure: KotlinDependencyHandler.() -> Unit,
) {
    kotlin.srcDirs.find { it.name == vsSourceSet }?.apply {
        dependencies(configure)
    }
}
