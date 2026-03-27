/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

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
class GrpcShimFixtureTest {
    private val resourcesDir = kotlin.io.path.Path("src", "test", "resources", "grpc")
    private val verificationRepositoryDir = kotlin.io.path.Path(System.getProperty("grpcShimVerificationRepoDir"))
    private val grpcShimVersion = System.getProperty("grpcShimVersion")
    private val kotlinVersion = System.getProperty("grpcShimKotlinVersion")
    private val hostTargetDeclaration = System.getProperty("grpcShimHostTargetDeclaration")
    private val hostCompileTask = System.getProperty("grpcShimHostCompileTask")
    private val hostPublicationSuffix = System.getProperty("grpcShimHostPublicationSuffix")

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
        assertContains(
            result.output,
            "grpc-shim native interop declarations are internal implementation details",
        )
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
            "org/jetbrains/kotlinx/kotlinx-rpc-grpc-core-shim-$hostPublicationSuffix/" +
                "$grpcShimVersion/" +
                "kotlinx-rpc-grpc-core-shim-$hostPublicationSuffix-$grpcShimVersion-cinterop-grpcCoreInterop.klib",
        )
        assertTrue(cinteropKlib.exists(), "Missing published cinterop KLIB: ${cinteropKlib.absolutePathString()}")

        ZipFile(cinteropKlib.toFile()).use { zip ->
            val manifestEntry = zip.getEntry("default/manifest")
            assertTrue(manifestEntry != null, "Missing manifest entry in ${cinteropKlib.pathString}")
            val manifest = zip.getInputStream(manifestEntry).reader().readText()
            assertContains(manifest, "kotlinx-rpc-native-shims-annotation")

            val knmEntry = zip.entries().asSequence().firstOrNull { entry ->
                !entry.isDirectory &&
                    entry.name.startsWith("default/linkdata/package_kotlinx.rpc.grpc.internal.cinterop/") &&
                    entry.name.endsWith(".knm")
                }
            assertTrue(
                knmEntry != null,
                "Missing package_kotlinx.rpc.grpc.internal.cinterop knm entry in ${cinteropKlib.pathString}",
            )

            val knmText = zip.getInputStream(knmEntry).readBytes().toString(StandardCharsets.ISO_8859_1)
            assertContains(knmText, "InternalNativeRpcApi")
        }
    }

    private fun runScenario(scenarioName: String): BuildResult {
        val scenarioDir = prepareScenarioProject(scenarioName)
        return GradleRunner.create()
            .withProjectDir(scenarioDir.toFile())
            .withArguments("--stacktrace", hostCompileTask)
            .forwardOutput()
            .build()
    }

    private fun runScenarioExpectingFailure(scenarioName: String): BuildResult {
        val scenarioDir = prepareScenarioProject(scenarioName)
        return GradleRunner.create()
            .withProjectDir(scenarioDir.toFile())
            .withArguments("--stacktrace", hostCompileTask)
            .forwardOutput()
            .buildAndFail()
    }

    private fun prepareScenarioProject(scenarioName: String): Path {
        val settingsTemplate = resourcesDir.resolve("template.settings.gradle.kts").readText()
        val propertiesTemplate = resourcesDir.resolve("template.gradle.properties").readText()
        val buildTemplate = resourcesDir.resolve("template.build.gradle.kts").readText()
        val scenarioSourceDir = resourcesDir.resolve("projects").resolve(scenarioName)

        assertTrue(scenarioSourceDir.exists(), "Missing fixture directory: ${scenarioSourceDir.absolutePathString()}")
        scenarioSourceDir.copyToRecursively(projectDir, followLinks = false, overwrite = true)

        projectDir.resolve("settings.gradle.kts").writeText(
            settingsTemplate.replace("<test-name>", "grpc-shim-$scenarioName"),
        )
        projectDir.resolve("gradle.properties").writeText(propertiesTemplate)
        projectDir.resolve("build.gradle.kts").writeText(
            buildTemplate
                .replace("<kotlin-version>", kotlinVersion)
                .replace("<grpc-shim-version>", grpcShimVersion)
                .replace("<verification-repo-dir>", verificationRepositoryDir.absolutePathString())
                .replace("<host-target-declaration>", hostTargetDeclaration)
                .replace("<host-publication-suffix>", hostPublicationSuffix),
        )

        assertFalse(
            projectDir.resolve("build").exists(),
            "Fixture directory should not include prebuilt outputs: ${projectDir.absolutePathString()}",
        )

        return projectDir
    }
}
