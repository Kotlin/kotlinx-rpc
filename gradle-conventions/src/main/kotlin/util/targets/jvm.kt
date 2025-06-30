/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.targets

import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.attributes
import org.gradle.kotlin.dsl.getByName
import util.other.files
import util.other.libs
import util.other.optionalProperty

fun Project.configureJvm(isKmp: Boolean) {
    val excludeJvm by optionalProperty("exclude")

    if (isKmp && excludeJvm) {
        return
    }

    tasks.getByName<Jar>(if (isKmp) "jvmJar" else "jar").apply {
        manifest {
            attributes(
                "Implementation-Title" to name,
                "Implementation-Version" to libs.versions.kotlinx.rpc.get()
            )
            val name = project.javaModuleName()
            attributes("Automatic-Module-Name" to name)
        }
    }
}

fun Project.javaModuleName(): String {
    return "kotlinx.rpc.${project.name}".replace('-', '.')
}

val Project.hasJavaModule: Boolean
    get() = plugins.hasPlugin("maven-publish") && name != "bom" && name != "jpms-check" && hasJvm

val Project.hasJvm: Boolean
    get() = files.any { it.name == "commonMain" }
            || files.any { it.name == "jvmMain" }
            || files.any { it.name == "main" }
