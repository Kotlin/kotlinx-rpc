/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.rpc.protoc.configurePluginProtections
import kotlinx.rpc.protoc.createProtoExtensions
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

@Suppress("unused")
public class RpcGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        checkKGPIsInTheClasspath()

        target.extensions.create<RpcExtension>("rpc")

        applyCompilerPlugin(target)

        target.createProtoExtensions()
        target.configurePluginProtections()
    }

    private fun applyCompilerPlugin(target: Project) {
        target.plugins.apply(CompilerPluginK2::class.java)
        target.plugins.apply(CompilerPluginCommon::class.java)
        target.plugins.apply(CompilerPluginBackend::class.java)
        target.plugins.apply(CompilerPluginCli::class.java)
    }
}

private fun checkKGPIsInTheClasspath() {
    try {
        Class.forName("org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin")
    } catch (_: ClassNotFoundException) {
        throw GradleException(
            """
                Kotlin Gradle plugin is not applied to the project.
                Please ensure that Kotlin Gradle plugin is applied to the project in the plugins block:
                
                  plugins {
                      kotlin("jvm") version "..." // or
                      kotlin("multiplatform") version "..." // or
                      kotlin("android") version "..."
                  }
            """.trimIndent()
        )
    }
}
