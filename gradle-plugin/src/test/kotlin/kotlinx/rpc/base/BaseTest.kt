/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.base

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.api.TestInstance
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.absolutePathString
import kotlin.io.path.copyTo
import kotlin.io.path.copyToRecursively
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlinx.rpc.KOTLIN_VERSION
import kotlinx.rpc.BUILD_REPO
import kotlin.io.path.absolute
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteRecursively

@OptIn(ExperimentalPathApi::class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
abstract class BaseTest {
    private lateinit var projectDir: Path

    @BeforeEach
    protected fun setup(testInfo: TestInfo) {
        TEST_KIT_PATH.createDirectories()

        val testClassName = testInfo.testClass.get().simpleName
        val testMethodName = testInfo.testMethod.get().name
            .replace(nameRegex, "_")
            .lowercase()

        val baseDir = TEST_PROJECTS_PATH
            .resolve(testClassName)
            .resolve(testMethodName)

        baseDir.deleteRecursively()
        baseDir.createDirectories()

        projectDir = baseDir.resolve(PROJECT_DIR)
        val buildCacheDir = baseDir.resolve(BUILD_CACHE_DIR)

        projectDir.createDirectories()
        buildCacheDir.createDirectories()

        val settingsTemplate = RESOURCES_PATH.resolve(SETTINGS_TEMPLATE)
            ?: error("template.settings.gradle.kts not found")
        val propertiesTemplate = RESOURCES_PATH.resolve(PROPERTIES_TEMPLATE)
            ?: error("template.gradle.properties not found")

        val settingsFile = projectDir.resolve("settings.gradle.kts")
        val propertiesFile = projectDir.resolve("gradle.properties")

        settingsTemplate.copyTo(settingsFile)
        propertiesTemplate.copyTo(propertiesFile)

        val projectName = "$testClassName-$testMethodName"
        settingsFile.replace("<test-name>", projectName)
        settingsFile.replace("<build-cache-dir>", buildCacheDir.absolutePathString())
        settingsFile.replace("<build-repo>", BUILD_REPO)

        val testTemplateDirectory = RESOURCES_PATH.resolve(PROJECTS_DIR)
            .resolve(testClassName)
            .resolve(testMethodName)

        testTemplateDirectory.copyToRecursively(projectDir, followLinks = false, overwrite = true)

        val buildScriptFile = projectDir.resolve("build.gradle.kts")
        buildScriptFile.replace("<kotlin-version>", KOTLIN_VERSION)

        println("""
            Setup project '$projectName'
              - in directory: ${projectDir.absolutePathString()}
              - from directory: ${testTemplateDirectory.absolutePathString()}
        """.trimIndent())
    }

    private fun runGradleInternal(
        task: String,
        vararg args: String,
        body: GradleRunner.() -> BuildResult,
    ): BuildResult {
        val gradleRunner = GradleRunner.create()
            .withProjectDir(projectDir.absolute().toFile())
            .withTestKitDir(TEST_KIT_PATH.absolute().toFile())
            .withPluginClasspath()
            .withArguments(
                listOfNotNull(
                    task,
                    "--stacktrace",
                    "--info",
                    "-Dorg.gradle.kotlin.dsl.scriptCompilationAvoidance=false",
                    *args,
                )
            ).apply {
                if (forwardOutput) {
                    forwardOutput()
                }
            }

        println("Running Gradle task '$task' with arguments: [${args.joinToString()}]")
        return gradleRunner.body()
    }

    protected fun runBaseTest(body: TestEnv.() -> Unit) {
        runTest(TestEnv(), body)
    }

    protected fun <T : TestEnv> runTest(testEnv: T, body: T.() -> Unit) {
        try {
            testEnv.body()
        } catch (e: Throwable) {
            val output = testEnv.latestBuild?.output
            if (output != null) {
                println("Latest gradle build output:")
                println(output)
            } else {
                println("No gradle build output available")
            }
            throw e
        }
    }

    open inner class TestEnv {
        val projectDir: Path get() = this@BaseTest.projectDir
        var latestBuild: BuildResult? = null
            private set

        fun runGradle(task: String, vararg args: String): BuildResult {
            return runGradleInternal(task, *args) {
                build().also { latestBuild = it }
            }
        }

        fun runGradleToFail(task: String, vararg args: String): BuildResult {
            return runGradleInternal(task, *args) {
                buildAndFail().also { latestBuild = it }
            }
        }

        fun runNonExistentTask(task: String): BuildResult {
            return runGradleToFail(task).apply {
                assertNoTask(task)
            }
        }

        fun BuildResult.assertNoTask(name: String) {
            assert(output.contains("Task '$name' not found")) {
                "Task '$name' should not be present in the project"
            }
        }
    }

    protected fun Path.replace(oldValue: String, newValue: String) {
        writeText(readText().replace(oldValue, newValue))
    }

    companion object {
        private val forwardOutput = System.getProperty("gradle.test.forward.output")
            ?.toBooleanStrictOrNull() ?: false

        private val nameRegex = Regex("[ .,-]")

        private val TEST_PROJECTS_PATH = Path.of("build", "gradle-test")
        private val TEST_KIT_PATH = Path.of("build", "test-kit")
        private const val BUILD_CACHE_DIR = "build-cache"
        private const val PROJECT_DIR = "project"

        private val RESOURCES_PATH = Path.of("src", "test", "resources")
        private const val SETTINGS_TEMPLATE = "template.settings.gradle.kts"
        private const val PROPERTIES_TEMPLATE = "template.gradle.properties"
        private const val PROJECTS_DIR = "projects"
    }
}
