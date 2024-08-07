/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.test.services

import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoot
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.EnvironmentConfigurator
import org.jetbrains.kotlin.test.services.RuntimeClasspathProvider
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.kotlin.test.services.assertions
import java.io.File
import java.io.FilenameFilter

private val globalRootDir: String = System.getProperty("kotlinx.rpc.globalRootDir")
    ?: error("Global root dir is not specified")

object RpcClasspathProvider {
    private val CORE_JVM_JAR_DIR = "$globalRootDir/core/build/libs/"
    private val CORE_JVM_JAR_FILTER = FilenameFilter { _, name ->
        name.startsWith("core-jvm") && name.endsWith(".jar")
    }

    private const val RUNTIME_DEPENDENCIES_PROPERTY = "kotlinx.rpc.test.data.classpath.dependencies"
    private val runtimeDependenciesPaths = System.getProperty(RUNTIME_DEPENDENCIES_PROPERTY)
        ?.split(File.pathSeparator)
        ?.map { File(it) }
        ?: error("Runtime dependencies are not specified")

    fun provideClasspath(testServices: TestServices): List<File> {
        val libDir = File(CORE_JVM_JAR_DIR)
        testServices.assertions.assertTrue(libDir.exists() && libDir.isDirectory, failMessage)
        val coreJar = libDir.listFiles(CORE_JVM_JAR_FILTER)?.firstOrNull() ?: testServices.assertions.fail(failMessage)

        return runtimeDependenciesPaths + coreJar
    }

    private val failMessage = { "Jar with runtime API does not exist. Please run :core:jvmJar" }
}

class RpcCompileClasspathProvider(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    override fun configureCompilerConfiguration(configuration: CompilerConfiguration, module: TestModule) {
        RpcClasspathProvider.provideClasspath(testServices).forEach {
            configuration.addJvmClasspathRoot(it)
        }
    }
}

class RpcRuntimeClasspathProvider(testServices: TestServices) : RuntimeClasspathProvider(testServices) {
    override fun runtimeClassPaths(module: TestModule): List<File> {
        return RpcClasspathProvider.provideClasspath(testServices)
    }
}
