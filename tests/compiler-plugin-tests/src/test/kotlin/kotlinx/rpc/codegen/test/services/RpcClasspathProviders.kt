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

private class RuntimeDependency(
    val dir: String,
    val name: String,
) {
    val filter = FilenameFilter { _, filename ->
        filename.startsWith(name) && filename.endsWith(".jar")
    }
}

private object RpcClasspathProvider {
    private val TEST_RUNTIME = RuntimeDependency("build/libs/", "compiler-plugin-test")
    private val KRPC_CORE_JVM = RuntimeDependency("$globalRootDir/krpc/krpc-core/build/libs/", "krpc-core-jvm")
    private val CORE_JVM = RuntimeDependency("$globalRootDir/core/build/libs/", "core-jvm")
    private val UTILS_JVM = RuntimeDependency("$globalRootDir/utils/build/libs/", "utils-jvm")

    private const val RUNTIME_DEPENDENCIES_PROPERTY = "kotlinx.rpc.test.data.classpath.dependencies"
    private val runtimeDependenciesPaths = System.getProperty(RUNTIME_DEPENDENCIES_PROPERTY)
        ?.split(File.pathSeparator)
        ?.map { File(it) }
        ?: error("Runtime dependencies are not specified")

    fun provideClasspath(testServices: TestServices): List<File> {
        val additionalDependencies = listOf(
            TEST_RUNTIME,
            CORE_JVM,
            KRPC_CORE_JVM,
            UTILS_JVM,
        ).map { it.getFile(testServices) }

        return runtimeDependenciesPaths + additionalDependencies
    }

    private fun RuntimeDependency.getFile(testServices: TestServices): File {
        fun failMessage(): String {
            return "Jar file with '$name' runtime API does not exist. " +
                    "Please run corresponding gradle :jar (or :jvmJar) task"
        }

        val libDir = File(dir)
        testServices.assertions.assertTrue(libDir.exists() && libDir.isDirectory, ::failMessage)
        val jar = libDir.listFiles(filter)?.firstOrNull()
            ?: testServices.assertions.fail(::failMessage)

        return jar
    }
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
