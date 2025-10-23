/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("PropertyName")

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import util.csm.ProcessCsmTemplate
import util.krpc_compat.krpcCompatVersions
import util.other.generateSource
import util.other.libs

plugins {
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.serialization)
    alias(libs.plugins.kotlinx.rpc)
    alias(libs.plugins.atomicfu)
}

dependencies {
    testImplementation(projects.tests.krpcProtocolCompatibilityTests.testApi)

    testImplementation(projects.krpc.krpcCore)
    testImplementation(projects.krpc.krpcServer)
    testImplementation(projects.krpc.krpcClient)
    testImplementation(projects.krpc.krpcSerialization.krpcSerializationJson)
    testImplementation(projects.tests.testUtils)

    testImplementation(libs.coroutines.test)
    testImplementation(libs.kotlin.test.junit5)

    testImplementation(libs.slf4j.api)
    testImplementation(libs.logback.classic)
    testImplementation(libs.coroutines.debug)
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}

tasks.test {
    useJUnitPlatform()
}

val templates: java.nio.file.Path = project.layout.projectDirectory.dir("templates").asFile.toPath()

krpcCompatVersions.forEach { (dir, version) ->
    val templateTask = tasks.register<ProcessCsmTemplate>(
        "process_template_$dir",
        version,
        mapOf("<rpc-version>" to dir),
        project.provider { templates },
        project.provider {
            val root = project.layout.projectDirectory.let {
                if (dir != "Latest") it.dir(dir) else it
            }

            root.dir("build")
                .dir("generated-sources")
                .dir("csm")
                .asFile.toPath()
        },
    )

    tasks.named("processResources").configure {
        dependsOn(templateTask)
    }
}

kotlin {
    sourceSets.test {
        kotlin.srcDirs(layout.buildDirectory.dir("generated-sources").map { it.asFile.resolve("csm") })
    }
}

generateSource(
    name = "versions",
    text = """
        |package kotlinx.rpc.krpc.test.compat
        |
        |@Suppress("EnumEntryName", "detekt.EnumNaming")
        |enum class Versions {
        |    ${krpcCompatVersions.keys.joinToString("\n|    ") { "$it," }}
        |    ;
        |}
        |
    """.trimMargin(),
    chooseSourceSet = { test },
)

tasks.test {
    krpcCompatVersions.keys.filter { it != "Latest" }.forEach { dir ->
        val jarTask = project(":tests:krpc-protocol-compatibility-tests:$dir").tasks.named("jar")
        dependsOn(jarTask)

        environment["JAR_PATH_$dir"] = jarTask.get().outputs.files.singleFile.absolutePath
    }
}
