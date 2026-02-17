/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.base

import kotlinx.rpc.ANDROID_HOME_DIR
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
import kotlinx.rpc.RPC_VERSION
import org.junit.jupiter.api.DynamicTest
import java.util.stream.Stream
import kotlin.io.path.absolute
import kotlin.io.path.appendText
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteRecursively
import kotlin.io.path.readLines

class VersionsEnv(
    val gradle: String,
    val kotlin: String,
    val android: String,
) {
    val kotlinSemver = run {
        val (major, minor, patch) = kotlin.split(".").map { it.toInt() }
        KotlinVersion(major, minor, patch)
    }

    val androidSemver = run {
        val (major, minor, patch) = android.split(".").map { it.toInt() }
        KotlinVersion(major, minor, patch)
    }
}

internal object KtVersion {
    val v2_2_20 = KotlinVersion(2, 2, 20)
    val v2_0_0 = KotlinVersion(2, 0, 0)
}

typealias VersionsPredicate = VersionsEnv.() -> Boolean

internal fun VersionsEnv.versionsWhereAndroidKmpLibExist(): Boolean {
    return androidSemver.isAtLeast(8, 10, 0)
}

private val GradleVersions = listOf(
    VersionsEnv("9.2.1", "2.2.21", "8.13.1"),
    VersionsEnv("8.14.1", "2.2.0", "8.10.0"),
    VersionsEnv("8.8", "2.0.0", "8.4.0"),
)

internal fun BaseTest.runWithGradleVersions(
    predicate: VersionsPredicate = { true },
    body: (VersionsEnv) -> Unit,
): Stream<DynamicTest> {
    return GradleVersions.stream()
        .filter { predicate(it) }
        .map { versions ->
            setupTest(versions)

            DynamicTest.dynamicTest("Gradle ${versions.gradle}, KGP ${versions.kotlin}, AGP ${versions.android}") {
                body(versions)
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
    private lateinit var testKitDir: Path

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
        val gradleDir = versions.gradle.replace(".", "_")
        testKitDir = TEST_KIT_PATH.resolve(gradleDir)

        val versioned = baseDir.resolve(gradleDir)

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
            .replace("<rpc-version>", RPC_VERSION)
            .replace("<android-version>", versions.android)

        buildScriptFile.readLines().filter {
            it.startsWith("// include:")
        }.map {
            it.removePrefix("// include:").trim()
        }.forEach { project ->
            settingsFile.appendText("\ninclude(\":$project\")")
        }

        println(
            """
            Setup project '$projectName'
              - in directory: ${projectDir.absolutePathString()}
              - from directory: ${testTemplateDirectory.absolutePathString()}
        """.trimIndent()
        )
    }

    private fun runGradleInternal(
        task: String,
        versions: VersionsEnv,
        vararg args: String,
        body: GradleRunner.() -> BuildResult,
    ): BuildResult {
        val gradleRunner = GradleRunner.create()
            .withProjectDir(projectDir.absolute().toFile())
            .withTestKitDir(testKitDir.absolute().toFile())
            .withGradleVersion(versions.gradle)
            .withEnvironment(mapOf("ANDROID_HOME" to ANDROID_HOME_DIR))
            .withArguments(
                listOfNotNull(
                    task,
                    "--stacktrace",
                    "-Dorg.gradle.kotlin.dsl.scriptCompilationAvoidance=false",
                    *args,
                )
            )

        println("Running Gradle task '$task' with arguments: [${args.joinToString()}]")
        return gradleRunner.body()
    }

    protected fun <T : TestEnv> runTest(testEnv: T, body: T.() -> Unit) {
        try {
            testEnv.body()
        } catch (e: Throwable) {
            testEnv.lastResult?.output?.let { println(it) }
            throw e
        }
    }

    open inner class TestEnv(
        val versions: VersionsEnv,
    ) {
        var lastResult: BuildResult? = null
        val projectDir: Path get() = this@BaseTest.projectDir

        fun runGradle(
            task: String,
            vararg args: String,
        ): BuildResult {
            return runGradleInternal(task, versions, *args) {
                build()
            }.also { lastResult = it }
        }

        fun runGradleToFail(
            task: String,
            vararg args: String,
        ): BuildResult {
            return runGradleInternal(task, versions, *args) {
                buildAndFail()
            }.also { lastResult = it }
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

    protected fun Path.replace(oldValue: String, newValue: String): Path {
        writeText(readText().replace(oldValue, newValue))
        return this
    }

    companion object {
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
