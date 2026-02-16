/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.krpc_compat

import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.buildtools.api.ExperimentalBuildToolsApi
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.PLUGIN_CLASSPATH_CONFIGURATION_NAME
import util.withKotlinJvmExtension

private fun kcp(suffix: String, version: String): String {
    return "org.jetbrains.kotlinx:kotlinx-rpc-compiler-plugin-$suffix:$version"
}

fun Project.setupCompat(rpcVersion: String, kotlinVersion: String) {
    plugins.apply("org.jetbrains.kotlin.jvm")

    dependencies.apply {
        add("compileOnly", project(":tests:krpc-protocol-compatibility-tests:test-api"))

        add("implementation", "org.jetbrains.kotlinx:kotlinx-rpc-krpc-client:$rpcVersion")
        add("implementation", "org.jetbrains.kotlinx:kotlinx-rpc-krpc-server:$rpcVersion")
        add("implementation", "org.jetbrains.kotlinx:kotlinx-rpc-krpc-serialization-json:$rpcVersion")

        val kcpVersion = "$kotlinVersion-$rpcVersion"
        add(PLUGIN_CLASSPATH_CONFIGURATION_NAME, kcp("common", kcpVersion))
        add(PLUGIN_CLASSPATH_CONFIGURATION_NAME, kcp("cli", kcpVersion))
        add(PLUGIN_CLASSPATH_CONFIGURATION_NAME, kcp("backend", kcpVersion))
        add(PLUGIN_CLASSPATH_CONFIGURATION_NAME, kcp("k2", kcpVersion))
    }

    withKotlinJvmExtension {
        @OptIn(ExperimentalBuildToolsApi::class, ExperimentalKotlinGradlePluginApi::class)
        compilerVersion.set(kotlinVersion)
        coreLibrariesVersion = kotlinVersion

        sourceSets.named("main") {
            kotlin.srcDirs(layout.buildDirectory.dir("generated-sources").map { it.asFile.resolve("csm") })
        }
    }

    tasks.named<Jar>("jar") {
        archiveVersion.set(rpcVersion)
    }

    tasks.named("compileKotlin").configure {
        dependsOn(":tests:krpc-protocol-compatibility-tests:process_template_${project.name}")
    }
}
