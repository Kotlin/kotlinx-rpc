/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.nativedeps.shims

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.util.zip.ZipFile
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.absolutePathString
import kotlin.io.path.copyToRecursively
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import kotlin.io.path.pathString
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalPathApi::class)
abstract class NativeShimFixtureTestSupport {
    protected abstract val systemPropertyPrefix: String
    protected abstract val resourcesSubdirectory: String
    protected abstract val testProjectPrefix: String
    protected abstract val dependencyCoordinatePrefix: String
    protected abstract val expectedDiagnosticMessage: String
    protected abstract val cinteropKlibName: String
    protected abstract val cinteropPackagePath: String
    protected abstract val expectedAnnotationMarker: String

    private val verificationRepositoryDir by lazy {
        kotlin.io.path.Path(systemProperty("VerificationRepoDir"))
    }
    private val shimVersion by lazy { systemProperty("Version") }
    private val kotlinVersion by lazy { systemProperty("KotlinVersion") }
    private val hostTargetDeclaration by lazy { systemProperty("HostTargetDeclaration") }
    private val hostCompileTask by lazy { systemProperty("HostCompileTask") }
    private val hostPublicationSuffix by lazy { systemProperty("HostPublicationSuffix") }

    private val commonResourcesDir = kotlin.io.path.Path("src", "test", "resources", "common")
    private val scenarioResourcesDir by lazy {
        kotlin.io.path.Path("src", "test", "resources", resourcesSubdirectory)
    }

    private lateinit var projectDir: Path

    @BeforeEach
    fun setUp(testInfo: TestInfo) {
        val testName = testInfo.testMethod.get().name.lowercase()
        projectDir = kotlin.io.path.Path("build", "gradle-test", testName)
        projectDir.deleteRecursively()
        projectDir.createDirectories()
    }

    @Test
    @Tag("negative")
    fun negativeCaseRequiresExplicitOptIn() {
        val result = runScenarioExpectingFailure("negative")
        assertContains(result.output, expectedDiagnosticMessage)
    }

    @Test
    @Tag("positive")
    fun positiveCaseCompilesWithExplicitOptIn() {
        runScenario("positive")
    }

    @Test
    @Tag("scope")
    fun unrelatedCodeIsUnaffected() {
        runScenario("scope")
    }

    @Test
    @Tag("artifact")
    fun publishedCinteropKlibCarriesInternalAnnotationMarker() {
        val cinteropKlib = verificationRepositoryDir.resolve(
            "org/jetbrains/kotlinx/$dependencyCoordinatePrefix-$hostPublicationSuffix/" +
                "$shimVersion/" +
                "$dependencyCoordinatePrefix-$hostPublicationSuffix-$shimVersion-$cinteropKlibName",
        )
        assertTrue(cinteropKlib.exists(), "Missing published cinterop KLIB: ${cinteropKlib.absolutePathString()}")

        ZipFile(cinteropKlib.toFile()).use { zip ->
            val manifestEntry = zip.getEntry("default/manifest")
            assertTrue(manifestEntry != null, "Missing manifest entry in ${cinteropKlib.pathString}")
            val manifest = zip.getInputStream(manifestEntry).reader().readText()
            assertContains(manifest, "kotlinx-rpc-native-shims-annotation")

            val knmEntry = zip.entries().asSequence().firstOrNull { entry ->
                !entry.isDirectory &&
                    entry.name.startsWith("default/linkdata/package_$cinteropPackagePath/") &&
                    entry.name.endsWith(".knm")
            }
            assertTrue(
                knmEntry != null,
                "Missing package_$cinteropPackagePath knm entry in ${cinteropKlib.pathString}",
            )

            val knmText = zip.getInputStream(knmEntry).readBytes().toString(StandardCharsets.ISO_8859_1)
            assertContains(knmText, expectedAnnotationMarker)
        }
    }

    protected fun runScenario(scenarioName: String): BuildResult {
        val scenarioDir = prepareScenarioProject(scenarioName)
        return GradleRunner.create()
            .withProjectDir(scenarioDir.toFile())
            .withArguments("--stacktrace", hostCompileTask)
            .forwardOutput()
            .build()
    }

    protected fun runScenarioExpectingFailure(scenarioName: String): BuildResult {
        val scenarioDir = prepareScenarioProject(scenarioName)
        return GradleRunner.create()
            .withProjectDir(scenarioDir.toFile())
            .withArguments("--stacktrace", hostCompileTask)
            .forwardOutput()
            .buildAndFail()
    }

    private fun prepareScenarioProject(scenarioName: String): Path {
        val settingsTemplate = commonResourcesDir.resolve("template.settings.gradle.kts").readText()
        val propertiesTemplate = commonResourcesDir.resolve("template.gradle.properties").readText()
        val buildTemplate = commonResourcesDir.resolve("template.build.gradle.kts").readText()
        val scenarioSourceDir = scenarioResourcesDir.resolve("projects").resolve(scenarioName)

        assertTrue(scenarioSourceDir.exists(), "Missing fixture directory: ${scenarioSourceDir.absolutePathString()}")
        scenarioSourceDir.copyToRecursively(projectDir, followLinks = false, overwrite = true)

        projectDir.resolve("settings.gradle.kts").writeText(
            settingsTemplate.replace("<test-name>", "$testProjectPrefix-$scenarioName"),
        )
        projectDir.resolve("gradle.properties").writeText(propertiesTemplate)
        projectDir.resolve("build.gradle.kts").writeText(
            buildTemplate
                .replace("<kotlin-version>", kotlinVersion)
                .replace("<verification-version>", shimVersion)
                .replace("<verification-repo-dir>", verificationRepositoryDir.absolutePathString())
                .replace("<host-target-declaration>", hostTargetDeclaration)
                .replace("<host-publication-suffix>", hostPublicationSuffix)
                .replace(
                    "<verification-dependency-coordinate>",
                    "org.jetbrains.kotlinx:$dependencyCoordinatePrefix-<host-publication-suffix>:<verification-version>",
                )
                .replace("<host-publication-suffix>", hostPublicationSuffix)
                .replace("<verification-version>", shimVersion),
        )

        assertFalse(
            projectDir.resolve("build").exists(),
            "Fixture directory should not include prebuilt outputs: ${projectDir.absolutePathString()}",
        )

        return projectDir
    }

    private fun systemProperty(suffix: String): String =
        System.getProperty("$systemPropertyPrefix$suffix")
}
