/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.rpc.grpc.configurePluginProtections
import kotlinx.rpc.proto.createProtoExtensions
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

@Suppress("unused")
public class RpcGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        println("Start parameters: ${target.gradle.startParameter.systemPropertiesArgs.values}")
        target.extensions.create<RpcExtension>("rpc")

        try {
            target.extensions.findByType<KotlinJvmProjectExtension>()
            applyCompilerPlugin(target)
        } catch (e: Throwable) {
            e.printStackTrace()
            throw e
        }

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
