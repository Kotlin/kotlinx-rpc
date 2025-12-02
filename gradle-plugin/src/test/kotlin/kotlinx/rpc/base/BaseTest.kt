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
import kotlinx.rpc.BUILD_REPO
import org.junit.jupiter.api.DynamicTest
import java.util.stream.Stream
import kotlin.io.path.absolute
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteRecursively

class VersionsEnv(
    val gradle: String,
    val kotlin: String,
)

private val GradleVersions = listOf(
    VersionsEnv("9.2.1", "2.2.21"),
    VersionsEnv("8.14.1", "2.2.0"),
    VersionsEnv("8.8", "2.0.0"),
)

internal fun BaseTest.runWithAllGradleVersions(body: (VersionsEnv) -> Unit): Stream<DynamicTest> {
    return GradleVersions.stream().map {
        setupTest(it)

        DynamicTest.dynamicTest("Gradle ${it.gradle}, Kotlin ${it.kotlin}") {
            body(it)
        }
    }
}

@OptIn(ExperimentalPathApi::class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
abstract class BaseTest {
    private lateinit var testClassName: String
    private lateinit var testMethodName: String
    private lateinit var baseDir: Path
    private lateinit var projectDir: Path

    @BeforeEach
    protected fun setup(testInfo: TestInfo) {
        TEST_KIT_PATH.createDirectories()

        testClassName = testInfo.testClass.get().simpleName
        testMethodName = testInfo.testMethod.get().name
            .replace(nameRegex, "_")
            .lowercase()

        baseDir = TEST_PROJECTS_PATH
            .resolve(testClassName)
            .resolve(testMethodName)

        baseDir.deleteRecursively()
        baseDir.createDirectories()
    }

    fun setupTest(versions: VersionsEnv) {
        val versioned = baseDir.resolve(versions.gradle.replace(".", "_"))

        projectDir = versioned.resolve(PROJECT_DIR)
        val buildCacheDir = versioned.resolve(BUILD_CACHE_DIR)

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
        buildScriptFile
            .replace("<kotlin-version>", versions.kotlin)

        println("""
            Setup project '$projectName'
              - in directory: ${projectDir.absolutePathString()}
              - from directory: ${testTemplateDirectory.absolutePathString()}
        """.trimIndent())
    }

    private fun runGradleInternal(
        task: String,
        versions: VersionsEnv,
        vararg args: String,
        body: GradleRunner.() -> BuildResult,
    ): BuildResult {
        val gradleRunner = GradleRunner.create()
            .withProjectDir(projectDir.absolute().toFile())
            .withTestKitDir(TEST_KIT_PATH.absolute().toFile())
            .withGradleVersion(versions.gradle)
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

    open inner class TestEnv(
        val versions: VersionsEnv,
    ) {
        val projectDir: Path get() = this@BaseTest.projectDir
        var latestBuild: BuildResult? = null
            private set

        fun runGradle(
            task: String,
            vararg args: String,
        ): BuildResult {
            return runGradleInternal(task, versions, *args) {
                build().also { latestBuild = it }
            }
        }

        fun runGradleToFail(
            task: String,
            vararg args: String,
        ): BuildResult {
            return runGradleInternal(task, versions, *args) {
                buildAndFail().also { latestBuild = it }
            }
        }

        fun runNonExistentTask(task: String): BuildResult {
            return runGradleToFail(task).apply {
                assertNoTask(task)
            }
        }

        fun BuildResult.assertNoTask(name: String) {
            assert(tasks.none { it.path.endsWith(":$name") }) {
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
