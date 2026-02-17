/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EnumEntryName", "detekt.EnumNaming")

package kotlinx.rpc.base

import kotlinx.rpc.base.GrpcBaseTest.CompileTaskMode
import kotlinx.rpc.base.GrpcBaseTest.SSets
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestInstance
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.io.path.*
import kotlin.io.path.name
import kotlin.test.assertEquals
import kotlin.test.fail

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
abstract class GrpcBaseTest : BaseTest() {
    enum class Type {
        Jvm, Kmp, Android;
    }

    abstract val type: Type

    protected fun GrpcTestEnv.runNonExistentTasksForSourceSet(set: SSets) {
        runNonExistentTask(bufGenerate(set))
        runNonExistentTask(processProtoFiles(set))
        runNonExistentTask(processProtoFilesImports(set))
        runNonExistentTask(generateBufYaml(set))
        runNonExistentTask(generateBufGenYaml(set))
    }

    protected fun runGrpcTest(
        versionsPredicate: VersionsPredicate = { true },
        test: GrpcTestEnv.() -> Unit,
    ): Stream<DynamicTest> = runWithGradleVersions(versionsPredicate) {
        runTest(GrpcTestEnv(it), test)
    }

    inner class GrpcTestEnv(versions: VersionsEnv) : TestEnv(versions) {
        fun BuildResult.assertOutcomes(
            sourceSet: SSets,
            generate: TaskOutcome? = null,
            bufYaml: TaskOutcome? = null,
            bufGenYaml: TaskOutcome? = null,
            protoFiles: TaskOutcome? = null,
            protoFilesImports: TaskOutcome? = null,
        ) {
            assertOutcome(generate, bufGenerate(sourceSet))
            assertOutcome(bufYaml, generateBufYaml(sourceSet))
            assertOutcome(bufGenYaml, generateBufGenYaml(sourceSet))
            assertOutcome(protoFiles, processProtoFiles(sourceSet))
            assertOutcome(protoFilesImports, processProtoFilesImports(sourceSet))
        }

        fun BuildResult.assertOutcome(expected: TaskOutcome?, task: String) {
            assertEquals(expected, protoTaskOutcomeOrNull(task), "Outcome for task $task")
        }

        fun BuildResult.protoTaskOutcome(name: String): TaskOutcome {
            return tasks.find { it.path == ":$name" }?.outcome
                ?: fail("Task ':$name' was not present in the build result")
        }

        fun BuildResult.protoTaskOutcomeOrNull(name: String): TaskOutcome? {
            return tasks.find { it.path == ":$name" }?.outcome
        }

        fun BuildResult.assertProtoTaskNotExecuted(name: String) {
            val task = tasks.find { it.path == ":$name" }
            if (task != null) {
                fail("Task ':$name' was present in the build result, but it shouldn't have been executed")
            }
        }

        fun assertSourceCodeGenerated(sourceSet: SSets, vararg files: Path) {
            val dir = protoBuildDirGenerated.resolve(sourceSet.name).resolve(KOTLIN_MULTIPLATFORM_DIR)
            if (files.isNotEmpty()) {
                assert(dir.exists()) {
                    "Directory '$dir' with generated sources does not exist"
                }
            }

            files.forEach { file ->
                assert(dir.resolve(file).exists()) {
                    "File '${file}' in '$dir' does not exist"
                }
            }
        }

        fun assertSourceCodeNotGenerated(sourceSet: SSets, vararg files: Path) {
            val dir = protoBuildDirGenerated.resolve(sourceSet.name).resolve(KOTLIN_MULTIPLATFORM_DIR)

            files.forEach { file ->
                assert(!dir.resolve(file).exists()) {
                    "File '${file}' in '$dir' should not exist"
                }
            }
        }

        fun assertSourceCodeNotGeneratedExcept(sourceSet: SSets, vararg files: Path) {
            val dir = protoBuildDirGenerated.resolve(sourceSet.name).resolve(KOTLIN_MULTIPLATFORM_DIR)

            fun Path.doAssert() {
                if (!exists()) {
                    if (files.isEmpty()) {
                        return
                    }

                    fail("Directory '${this.relativeTo(dir)}' does not exist, but expected files: ${files.toList()}")
                }

                listDirectoryEntries().forEach { entry ->
                    when {
                        entry.isDirectory() -> {
                            entry.doAssert()
                        }

                        entry.isRegularFile() && entry.relativeTo(dir) in files -> {
                            // fine
                        }

                        entry.name == ".keep" -> {
                            // fine
                        }

                        else -> {
                            fail("File '${entry.relativeTo(dir)}' in '${this.relativeTo(dir)}' should not exist")
                        }
                    }
                }
            }

            dir.doAssert()
        }

        @OptIn(ExperimentalPathApi::class)
        private fun assertWorkspaceProtoFilesCopiedInternal(vararg files: Path, sourceSet: SSets, dir: String) {
            val protoSources = protoBuildDirSourceSets.resolve(sourceSet.name).resolve(dir)
            val included = files.map { file ->
                val resolved = protoSources.resolve(file)
                assert(resolved.exists()) {
                    "File '${file}' in '$protoSources' does not exist"
                }
                resolved.relativeTo(protoSources).pathString
            }.toSet()

            protoSources.walk().forEach {
                val pathString = it.relativeTo(protoSources).pathString
                if (it.isRegularFile() && it.extension == "proto" && pathString !in included) {
                    fail("File '${it.relativeTo(protoSources)}' in '$protoSources' is not expected")
                }
            }
        }

        fun assertWorkspaceProtoFilesCopied(sourceSet: SSets, vararg files: Path) {
            assertWorkspaceProtoFilesCopiedInternal(*files, sourceSet = sourceSet, dir = "proto")
        }

        fun assertWorkspaceImportProtoFilesCopied(sourceSet: SSets, vararg files: Path) {
            assertWorkspaceProtoFilesCopiedInternal(*files, sourceSet = sourceSet, dir = "import")
        }

        @OptIn(ExperimentalPathApi::class)
        fun cleanProtoBuildDir() {
            protoBuildDir.deleteRecursively()
        }

        fun assertBufGenYaml(sourceSet: SSets, @Language("Yaml") content: String) {
            val file = protoBuildDirSourceSets
                .resolve(sourceSet.name)
                .resolve("buf.gen.yaml")

            assert(file.exists()) {
                "File '${file.absolutePathString()}' does not exist"
            }

            val fileContent = file.readLines().joinToString("\n") {
                when {
                    it.contains("protoc-gen-kotlin-multiplatform") -> {
                        it.replace(localPluginExecRegex, "[protoc-gen-kotlin-multiplatform]")
                    }

                    it.contains("protoc-gen-grpc-kotlin-multiplatform") -> {
                        it.replace(localPluginExecRegex, "[protoc-gen-grpc-kotlin-multiplatform]")
                    }

                    else -> {
                        it
                    }
                }
            }

            assertEquals(content.trimIndent(), fileContent.trimIndent())
        }

        private val localPluginExecRegex = "\\[.*?]".toRegex()

        fun BuildResult.assertMainTaskExecuted(
            protoFiles: List<Path>,
            generatedFiles: List<Path>,
        ) {
            assertOutcome(TaskOutcome.SUCCESS, bufGenerateCommonMain)
            assertOutcome(TaskOutcome.SUCCESS, processCommonMainProtoFiles)
            assertOutcome(TaskOutcome.SUCCESS, generateBufYamlCommonMain)
            assertOutcome(TaskOutcome.SUCCESS, generateBufGenYamlCommonMain)

            assertProtoTaskNotExecuted(bufGenerateCommonTest)
            assertProtoTaskNotExecuted(processCommonTestProtoFiles)
            assertProtoTaskNotExecuted(processCommonTestProtoFilesImports)
            assertProtoTaskNotExecuted(generateBufYamlCommonTest)
            assertProtoTaskNotExecuted(generateBufGenYamlCommonTest)

            assertSourceCodeGenerated(mainSourceSet, *generatedFiles.toTypedArray())
            assertSourceCodeNotGenerated(testSourceSet, *generatedFiles.toTypedArray())

            assertWorkspaceProtoFilesCopied(mainSourceSet, *protoFiles.toTypedArray())
            assertWorkspaceImportProtoFilesCopied(mainSourceSet)

            assertWorkspaceProtoFilesCopied(testSourceSet)
            assertWorkspaceImportProtoFilesCopied(testSourceSet)
        }

        fun BuildResult.assertTestTaskExecuted(
            protoFiles: List<Path>,
            importProtoFiles: List<Path>,
            generatedFiles: List<Path>,
            importGeneratedFiles: List<Path>,
        ) {
            assertOutcome(TaskOutcome.SUCCESS, bufGenerateCommonTest)
            val processProtoOutcome = if (protoFiles.isEmpty()) {
                TaskOutcome.NO_SOURCE
            } else {
                TaskOutcome.SUCCESS
            }
            assertOutcome(processProtoOutcome, processCommonTestProtoFiles)
            val processProtoImportsOutcome = if (importProtoFiles.isEmpty()) {
                TaskOutcome.NO_SOURCE
            } else {
                TaskOutcome.SUCCESS
            }
            assertOutcome(processProtoImportsOutcome, processCommonTestProtoFilesImports)
            assertOutcome(TaskOutcome.SUCCESS, generateBufYamlCommonTest)
            assertOutcome(TaskOutcome.SUCCESS, generateBufGenYamlCommonTest)

            val mainGenerateOutcome = if (importProtoFiles.isEmpty()) {
                TaskOutcome.SKIPPED
            } else {
                TaskOutcome.SUCCESS
            }

            assertOutcome(mainGenerateOutcome, bufGenerateCommonMain)
            val mainProcessOutcome = if (importProtoFiles.isEmpty()) {
                TaskOutcome.NO_SOURCE
            } else {
                TaskOutcome.SUCCESS
            }
            assertOutcome(mainProcessOutcome, processCommonMainProtoFiles)
            assertOutcome(TaskOutcome.SUCCESS, generateBufYamlCommonMain)
            assertOutcome(TaskOutcome.SUCCESS, generateBufGenYamlCommonMain)

            assertSourceCodeGenerated(testSourceSet, *generatedFiles.toTypedArray())
            assertSourceCodeNotGenerated(mainSourceSet, *generatedFiles.toTypedArray())

            assertSourceCodeGenerated(mainSourceSet, *importGeneratedFiles.toTypedArray())
            assertSourceCodeNotGenerated(testSourceSet, *importGeneratedFiles.toTypedArray())

            assertWorkspaceProtoFilesCopied(testSourceSet, *protoFiles.toTypedArray())
            assertWorkspaceImportProtoFilesCopied(testSourceSet, *importProtoFiles.toTypedArray())

            assertWorkspaceProtoFilesCopied(mainSourceSet, *importProtoFiles.toTypedArray())
            assertWorkspaceImportProtoFilesCopied(mainSourceSet)
        }

        fun BuildResult.assertTaskExecuted(
            sourceSet: SSets,
            protoFiles: List<Path>,
            importProtoFiles: List<Path>,
            generatedFiles: List<Path>,
            notExecuted: List<SSets>,
        ) {
            val generateOutcome = if (protoFiles.isEmpty()) {
                TaskOutcome.SKIPPED
            } else {
                TaskOutcome.SUCCESS
            }
            assertOutcome(generateOutcome, bufGenerate(sourceSet))

            val processProtoOutcome = if (protoFiles.isEmpty()) {
                TaskOutcome.NO_SOURCE
            } else {
                TaskOutcome.SUCCESS
            }
            assertOutcome(processProtoOutcome, processProtoFiles(sourceSet))

            val processImportProtoOutcome = if (importProtoFiles.isEmpty()) {
                TaskOutcome.NO_SOURCE
            } else {
                TaskOutcome.SUCCESS
            }
            assertOutcome(processImportProtoOutcome, processProtoFilesImports(sourceSet))

            assertOutcome(TaskOutcome.SUCCESS, generateBufYaml(sourceSet))
            assertOutcome(TaskOutcome.SUCCESS, generateBufGenYaml(sourceSet))

            assertSourceCodeGenerated(sourceSet, *generatedFiles.toTypedArray())
            assertSourceCodeNotGeneratedExcept(sourceSet, *generatedFiles.toTypedArray())
            assertWorkspaceProtoFilesCopied(sourceSet, *protoFiles.toTypedArray())
            assertWorkspaceImportProtoFilesCopied(sourceSet, *importProtoFiles.toTypedArray())

            notExecuted.forEach {
                assertProtoTaskNotExecuted(bufGenerate(it))
                assertProtoTaskNotExecuted(processProtoFiles(it))
                assertProtoTaskNotExecuted(processProtoFilesImports(it))
                assertProtoTaskNotExecuted(generateBufYaml(it))
                assertProtoTaskNotExecuted(generateBufGenYaml(it))
            }
        }

        fun GrpcTestEnv.runAndCheckFiles(
            sourceSet: SSets,
            vararg imports: SSets,
            extended: List<SSets> = emptyList(),
        ) {
            cleanProtoBuildDir()

            runForSet(sourceSet).assertSourceSet(sourceSet, *imports, extendedProto = extended)

            dryRunCompilation(sourceSet)
        }

        fun dryRunCompilation(sourceSet: SSets) {
            val compileTask = sourceSet.compileTask() ?: return

            val result = runGradle(compileTask, "--dry-run", "--no-configuration-cache")

            assert(result.output.contains(":${bufGenerate(sourceSet)} SKIPPED")) {
                "${bufGenerate(sourceSet)} task should be present in dry-run mode for $compileTask execution"
            }
        }

        fun GrpcTestEnv.runForSet(sourceSet: SSets): BuildResult {
            return runGradle(bufGenerate(sourceSet))
        }

        fun BuildResult.assertSourceSet(
            sourceSet: SSets,
            vararg imports: SSets,
            extendedProto: List<SSets>,
        ) {
            if (!sourceSet.applicable()) {
                println(
                    "Skipping ${sourceSet.capital} source set " +
                            "because it's not applicable for the current Kotlin version"
                )
                return
            }

            val importsSet = imports
                .onEach {
                    if (!it.applicable()) {
                        println(
                            "Skipping ${it.capital} import source set " +
                                    "because it's not applicable for the current Kotlin version"
                        )
                    }
                }
                .filter { it.applicable() }
                .toSet()

            val generateFor = extendedProto + sourceSet

            assertTaskExecuted(
                sourceSet = sourceSet,
                protoFiles = generateFor.map { Path("${it.name}.proto") },
                importProtoFiles = importsSet.map {
                    Path("${it.name}.proto")
                },
                generatedFiles = generateFor.flatMap {
                    val ktFile = "${it.capital}.kt"

                    listOf(
                        Path(ktFile),
                        Path(RPC_INTERNAL, ktFile),
                    )
                },
                notExecuted = sourceSet.all().filter { it != sourceSet && it !in importsSet },
            )
        }

        fun runPlatformOptionTest(sourceSet: SSets, platformOption: String) {
            runGradle(generateBufGenYaml(sourceSet))

            assertBufGenYaml(
                sourceSet = sourceSet,
                content = """
version: v2
clean: true
plugins:
  - local: [protoc-gen-kotlin-multiplatform]
    out: kotlin-multiplatform
    opt:
      - debugOutput=protoc-gen-kotlin-multiplatform.log
      - generateComments=true
      - generateFileLevelComments=true
      - indentSize=4
      - explicitApiModeEnabled=false
      - platform=${platformOption}
  - local: [protoc-gen-grpc-kotlin-multiplatform]
    out: grpc-kotlin-multiplatform
    opt:
      - debugOutput=protoc-gen-grpc-kotlin-multiplatform.log
      - generateComments=true
      - generateFileLevelComments=true
      - indentSize=4
      - explicitApiModeEnabled=false
      - platform=${platformOption}
inputs:
  - directory: proto
            """.trimIndent()
            )
        }

        fun bufGenerate(sourceSet: SSets) = "bufGenerate${sourceSet.capital}"
        fun processProtoFiles(sourceSet: SSets) = "process${sourceSet.capital}ProtoFiles"
        fun processProtoFilesImports(sourceSet: SSets) = "process${sourceSet.capital}ProtoFilesImports"
        fun generateBufYaml(sourceSet: SSets) = "generateBufYaml${sourceSet.capital}"
        fun generateBufGenYaml(sourceSet: SSets) = "generateBufGenYaml${sourceSet.capital}"

        val mainSourceSet: SSets = when (type) {
            Type.Kmp -> SSetsKmp.Default.commonMain
            Type.Jvm -> SSetsJvm.main
            Type.Android -> SSetsAndroid.Default.main
        }

        val testSourceSet: SSets = when (type) {
            Type.Kmp -> SSetsKmp.Default.commonTest
            Type.Jvm -> SSetsJvm.test
            Type.Android -> SSetsAndroid.Default.test
        }

        val bufGenerateCommonMain = bufGenerate(mainSourceSet)
        val bufGenerateCommonTest = bufGenerate(testSourceSet)
        val processCommonMainProtoFiles = processProtoFiles(mainSourceSet)
        val processCommonTestProtoFiles = processProtoFiles(testSourceSet)
        val processCommonTestProtoFilesImports = processProtoFilesImports(testSourceSet)
        val generateBufYamlCommonMain = generateBufYaml(mainSourceSet)
        val generateBufYamlCommonTest = generateBufYaml(testSourceSet)
        val generateBufGenYamlCommonMain = generateBufGenYaml(mainSourceSet)
        val generateBufGenYamlCommonTest = generateBufGenYaml(testSourceSet)

        val protoBuildDir: Path by lazy {
            projectDir
                .resolve("build")
                .resolve("protoBuild")
        }

        val TestEnv.protoBuildDirSourceSets: Path by lazy {
            protoBuildDir
                .resolve("sourceSets")
        }

        val TestEnv.protoBuildDirGenerated: Path by lazy {
            protoBuildDir
                .resolve("generated")
        }

        val mainProtoFileSources: Path by lazy {
            projectDir
                .resolve("src")
                .resolve(mainSourceSet.name)
                .resolve("proto")
        }

        val testProtoFileSources: Path by lazy {
            projectDir
                .resolve("src")
                .resolve(testSourceSet.name)
                .resolve("proto")
        }

        fun SSets.sourceDir(): Path {
            return projectDir
                .resolve("src")
                .resolve(name)
                .resolve("proto")
        }

        fun SSets.applicable(): Boolean {
            return versions.kotlinSemver >= minKotlin
        }
    }

    interface SSets {
        val minKotlin: KotlinVersion
        val mode: CompileTaskMode?
        val name: String
        fun all(): List<SSets>
        fun compileTask(): String? {
            return mode?.let { compileTaskName(it) }
        }
    }

    enum class SSetsJvm(override val minKotlin: KotlinVersion = KtVersion.v2_0_0) : SSets {
        main, test,
        ;

        override val mode: CompileTaskMode = ctm.j

        override fun all(): List<SSets> {
            return entries
        }
    }

    sealed interface SSetsKmp : SSets {
        enum class Default(
            override val mode: CompileTaskMode? = null,
            override val minKotlin: KotlinVersion = KtVersion.v2_0_0,
        ) : SSetsKmp {
            commonMain, commonTest,
            jvmMain(ctm.k), jvmTest(ctm.k),
            webMain(minKotlin = KtVersion.v2_2_20), webTest(minKotlin = KtVersion.v2_2_20),
            jsMain(ctm.k), jsTest(ctm.k),
            wasmJsMain(ctm.k), wasmJsTest(ctm.k),
            wasmWasiMain(ctm.k), wasmWasiTest(ctm.k),
            nativeMain, nativeTest,
            appleMain, appleTest,
            macosMain, macosTest,
            macosArm64Main(ctm.k), macosArm64Test(ctm.k),
            ;

            override fun all(): List<SSets> {
                return entries
            }
        }

        enum class AndroidKmpLib(
            override val mode: CompileTaskMode? = null,
            override val minKotlin: KotlinVersion = KtVersion.v2_0_0,
        ) : SSetsKmp {
            commonMain, commonTest,
            jvmMain(ctm.k), jvmTest(ctm.k),
            androidMain(ctm.ak), androidHostTest(ctm.ak), androidDeviceTest(ctm.ak),
            ;

            override fun all(): List<SSets> {
                return entries
            }
        }

        enum class LegacyAndroid(
            override val mode: CompileTaskMode? = null,
            override val minKotlin: KotlinVersion = KtVersion.v2_0_0,
        ) : SSetsKmp {
            commonMain, commonTest,
            jvmMain(ctm.k), jvmTest(ctm.k),

            // kmp, non-executable
            androidMain,
            androidUnitTest, androidInstrumentedTest,

            // kmp, executable
            androidDebug(ctm.al), androidRelease(ctm.al),
            androidUnitTestDebug(ctm.al), androidUnitTestRelease(ctm.al),
            androidInstrumentedTestDebug(ctm.al),

            // legacy, non-executable
            main, test,
            testFixtures, testFixturesDebug, testFixturesRelease,
            androidTest,

            debug, release,
            testDebug, testRelease,
            androidTestDebug,
            ;

            override fun all(): List<SSets> {
                return entries
            }
        }
    }

    sealed interface SSetsAndroid : SSets {
        enum class Default(
            override val mode: CompileTaskMode? = null,
            override val minKotlin: KotlinVersion = KtVersion.v2_0_0,
        ) : SSetsAndroid {
            // non-executable
            main, test,
            testFixtures, testFixturesDebug, testFixturesRelease,
            androidTest,

            // executable
            debug(ctm.a), release(ctm.a),
            testDebug(ctm.a), testRelease(ctm.a),
            androidTestDebug(ctm.a),
            ;

            override fun all(): List<SSets> {
                return entries
            }
        }

        enum class Test(
            override val mode: CompileTaskMode? = null,
            override val minKotlin: KotlinVersion = KtVersion.v2_0_0,
        ) : SSetsAndroid {
            main, debug(ctm.a),
            ;

            override fun all(): List<SSets> {
                return entries
            }
        }
    }

    enum class CompileTaskMode {
        Jvm, Kmp, Android, LegacyAndroidKmp, AndroidKmpLib;

        companion object {
            val j = Jvm
            val k = Kmp
            val a = Android
            val al = LegacyAndroidKmp
            val ak = AndroidKmpLib
        }
    }

    companion object {
        private const val KOTLIN_MULTIPLATFORM_DIR = "kotlin-multiplatform"
        const val RPC_INTERNAL = "_rpc_internal"
    }
}

typealias ctm = CompileTaskMode

private val SSets.capital get() = name.replaceFirstChar { it.titlecase() }

private fun SSets.compileTaskName(mode: CompileTaskMode): String {
    return when (mode) {
        CompileTaskMode.Jvm -> {
            if (name == "main") "compileKotlin" else "compileTestKotlin"
        }
        CompileTaskMode.Kmp -> {
            val platform = capital.removeSuffix("Main").removeSuffix("Test")
            if (name.endsWith("Test")) {
                "compileTestKotlin$platform"
            } else {
                "compileKotlin$platform"
            }
        }

        CompileTaskMode.Android -> {
            // compileX86FreeappDebugUnitTestKotlin
            // compileArmFreeappDebugAndroidTestKotlin
            // compileArmFreeappDebugKotlin
            when {
                name.startsWith("androidTest") -> {
                    "compile${name.removePrefix("androidTest")}AndroidTestKotlin"
                }

                name.startsWith("test") -> {
                    "compile${name.removePrefix("test")}UnitTestKotlin"
                }

                else -> {
                    "compile${capital}Kotlin"
                }
            }
        }

        CompileTaskMode.LegacyAndroidKmp -> {
            // compileDebugAndroidTestKotlinAndroid
            // compileDebugUnitTestKotlinAndroid
            // compileDebugKotlinAndroid
            val withoutPrefix = name.removePrefix("android")
            when {
                withoutPrefix.startsWith("InstrumentedTest") -> {
                    "compile${withoutPrefix.removePrefix("InstrumentedTest")}AndroidTestKotlinAndroid"
                }

                withoutPrefix.startsWith("UnitTest") -> {
                    "compile${withoutPrefix.removePrefix("UnitTest")}UnitTestKotlinAndroid"
                }

                else -> {
                    "compile${withoutPrefix}KotlinAndroid"
                }
            }
        }

        CompileTaskMode.AndroidKmpLib -> {
            // compileAndroidDeviceTest
            // compileAndroidHostTest
            // compileAndroidMain
            "compile${capital}"
        }
    }
}
