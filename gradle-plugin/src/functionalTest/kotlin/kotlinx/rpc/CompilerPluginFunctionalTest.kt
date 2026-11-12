/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import com.autonomousapps.kit.GradleBuilder.build
import com.autonomousapps.kit.GradleBuilder.buildAndFail
import com.autonomousapps.kit.Source
import com.autonomousapps.kit.gradle.Plugin
import com.autonomousapps.kit.gradle.Repositories
import com.autonomousapps.kit.gradle.Repository
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.compiler.plugin.devkit.test.AbstractDevKitGradleProject
import org.jetbrains.kotlin.compiler.plugin.devkit.test.BUILD_COMPILER_VERSION
import org.jetbrains.kotlin.compiler.plugin.devkit.test.TEST_COMPILER_VERSION
import org.jetbrains.kotlin.compiler.plugin.devkit.test.assertOutputContains
import org.jetbrains.kotlin.compiler.plugin.devkit.test.pluginUnderTestVersion
import org.junit.Assume.assumeTrue
import org.junit.Test

/**
 * Checks the compiler plugin from the Gradle plugin with the Kotlin compiler of each supported version.
 *
 * Each test project depends on the `kotlinx-rpc-core` library of this build.
 */
public class CompilerPluginFunctionalTest {
    @Test
    public fun generatedCodeRuns() {
        // The libraries depend on the Kotlin standard library of this build.
        // A Kotlin compiler can't read a standard library that is more than one minor version newer.
        assumeTrue(
            "Kotlin $TEST_COMPILER_VERSION can't read the standard library of Kotlin $BUILD_COMPILER_VERSION",
            TEST_COMPILER_VERSION.major == BUILD_COMPILER_VERSION.major &&
                TEST_COMPILER_VERSION.minor + 1 >= BUILD_COMPILER_VERSION.minor,
        )

        val project = RpcProject("Greeter" to service, "Main" to main)

        val result = build(project.projectDir, "run")

        result.assertOutputContains("client: Hello, stub")
        result.assertOutputContains("client stream: [0, 1, 2]")
        result.assertOutputContains("descriptor: test.Greeter [countTo, greet]")
        result.assertOutputContains("server: Hello, invokator")
        result.assertOutputContains("server stream: [0, 1]")
    }

    @Test
    public fun compilerPluginReportsDiagnostic() {
        val project = RpcProject("NotAnInterface" to notAnInterface)

        val result = buildAndFail(project.projectDir, "compileKotlin")

        result.assertOutputContains("annotation is only applicable to interfaces and annotation classes")
    }
}

@Language("kotlin")
private val service = """
    import kotlinx.coroutines.flow.Flow
    import kotlinx.rpc.annotations.Rpc

    @Rpc
    interface Greeter {
        suspend fun greet(name: String): String

        fun countTo(limit: Int): Flow<Int>
    }
""".trimIndent()

@Language("kotlin")
private val main = """
    import kotlinx.coroutines.flow.Flow
    import kotlinx.coroutines.flow.flow
    import kotlinx.coroutines.flow.toList
    import kotlinx.coroutines.runBlocking
    import kotlinx.rpc.RpcCall
    import kotlinx.rpc.RpcClient
    import kotlinx.rpc.descriptor.flowInvokator
    import kotlinx.rpc.descriptor.serviceDescriptorOf
    import kotlinx.rpc.descriptor.unaryInvokator
    import kotlinx.rpc.internal.utils.ExperimentalRpcApi
    import kotlinx.rpc.withService

    class GreeterImpl : Greeter {
        override suspend fun greet(name: String): String = "Hello, ${'$'}name"

        override fun countTo(limit: Int): Flow<Int> = flow { repeat(limit) { emit(it) } }
    }

    // Answers each call with the service implementation, so the output shows the arguments of the generated stub.
    class LocalClient(private val service: Greeter) : RpcClient {
        @Suppress("UNCHECKED_CAST")
        override suspend fun <T> call(call: RpcCall): T {
            check(call.callableName == "greet")
            return service.greet(call.arguments[0] as String) as T
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T> callServerStreaming(call: RpcCall): Flow<T> {
            check(call.callableName == "countTo")
            return service.countTo(call.arguments[0] as Int) as Flow<T>
        }
    }

    @OptIn(ExperimentalRpcApi::class)
    fun main(): Unit = runBlocking {
        val implementation = GreeterImpl()

        val stub = LocalClient(implementation).withService<Greeter>()
        println("client: " + stub.greet("stub"))
        println("client stream: " + stub.countTo(3).toList())

        val descriptor = serviceDescriptorOf<Greeter>()
        println("descriptor: " + descriptor.fqName + " " + descriptor.callables.keys.sorted())

        val greet = descriptor.getCallable("greet")!!.unaryInvokator
        println("server: " + greet.call(implementation, arrayOf("invokator")))

        val countTo = descriptor.getCallable("countTo")!!.flowInvokator
        println("server stream: " + countTo.call(implementation, arrayOf(2)).toList())
    }
""".trimIndent()

@Language("kotlin")
private val notAnInterface = """
    import kotlinx.rpc.annotations.Rpc

    @Rpc
    class NotAnInterface
""".trimIndent()

/**
 * A JVM project with the Gradle plugin and the `kotlinx-rpc-core` library.
 *
 * @param files The name and the content of each source file. The `run` task runs the `main` function of `Main`.
 */
private class RpcProject(private vararg val files: Pair<String, String>) : AbstractDevKitGradleProject() {
    val projectDir get() = gradleProject.rootDir

    override val pluginUnderTest: Plugin
        get() = Plugin("org.jetbrains.kotlinx.rpc.plugin", pluginUnderTestVersion)

    override fun repositories(defaults: List<Repository>): Repositories {
        return super.repositories(
            defaults +
                // The build repository with the libraries of this build.
                Repository.ofMaven(System.getProperty("kotlinx.rpc.functionalTest.repository")) +
                // The libraries and the Gradle plugin depend on the API watchdog annotations. Only kt-lib/eap has them.
                Repository.ofMaven("https://packages.jetbrains.team/maven/p/kt-lib/eap")
        )
    }

    override fun sources(): List<Source> = files.map { (name, content) ->
        source(source = content, fileNameWithoutExtension = name)
    }

    override fun StringBuilder.onBuildScript() {
        appendLine(
            """
                dependencies {
                    implementation("org.jetbrains.kotlinx:kotlinx-rpc-core:$pluginUnderTestVersion")
                }

                tasks.register<JavaExec>("run") {
                    classpath = sourceSets["main"].runtimeClasspath
                    mainClass.set("test.MainKt")
                }
            """.trimIndent()
        )
    }
}
