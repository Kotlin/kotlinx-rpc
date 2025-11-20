/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.rpc.base.GrpcBaseTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestInstance
import kotlin.io.path.Path

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class GrpcKmpProjectTest : GrpcBaseTest() {
    override val isKmp: Boolean = true

    @TestFactory
    fun `Minimal gRPC Configuration`() = runGrpcTest {
        val result = runGradle(bufGenerateCommonMain)

        result.assertMainTaskExecuted(
            protoFiles = listOf(
                Path("some.proto")
            ),
            generatedFiles = listOf(
                Path("Some.kt"),
                Path(RPC_INTERNAL, "Some.kt"),
            )
        )
    }

    @TestFactory
    fun `No gRPC`() = runGrpcTest {
        runNonExistentTask(bufGenerateCommonMain)
        runNonExistentTask(bufGenerateCommonTest)
        runNonExistentTask(processCommonMainProtoFiles)
        runNonExistentTask(processCommonTestProtoFiles)
        runNonExistentTask(processCommonTestProtoFilesImports)
        runNonExistentTask(generateBufYamlCommonMain)
        runNonExistentTask(generateBufYamlCommonTest)
        runNonExistentTask(generateBufGenYamlCommonMain)
        runNonExistentTask(generateBufGenYamlCommonTest)
    }

    @TestFactory
    fun `Test-Only Sources`() = runGrpcTest {
        val result = runGradle(bufGenerateCommonTest)

        result.assertTestTaskExecuted(
            protoFiles = listOf(
                Path("some.proto"),
            ),
            importProtoFiles = emptyList(),
            generatedFiles = listOf(
                Path("Some.kt"),
                Path(RPC_INTERNAL, "Some.kt"),
            ),
            importGeneratedFiles = emptyList()
        )
    }

    @TestFactory
    fun `Main and Test Mixed Sources`() = runGrpcTest {
        val mainRun = runGradle(bufGenerateCommonMain)

        mainRun.assertMainTaskExecuted(
            protoFiles = listOf(
                Path("some.proto")
            ),
            generatedFiles = listOf(
                Path("Some.kt"),
                Path(RPC_INTERNAL, "Some.kt"),
            )
        )

        cleanProtoBuildDir()

        val testRun = runGradle(bufGenerateCommonTest)

        testRun.assertTestTaskExecuted(
            protoFiles = listOf(
                Path("other.proto")
            ),
            importProtoFiles = listOf(
                Path("some.proto")
            ),
            generatedFiles = listOf(
                Path("Other.kt"),
                Path(RPC_INTERNAL, "Other.kt"),
            ),
            importGeneratedFiles = listOf(
                Path("Some.kt"),
                Path(RPC_INTERNAL, "Some.kt"),
            )
        )
    }

    @TestFactory
    fun `No JVM Targets`() = runGrpcTest {
        runNonExistentTask(bufGenerateCommonMain)
        runNonExistentTask(bufGenerateCommonTest)
        runNonExistentTask(processCommonMainProtoFiles)
        runNonExistentTask(processCommonTestProtoFiles)
        runNonExistentTask(processCommonTestProtoFilesImports)
        runNonExistentTask(generateBufYamlCommonMain)
        runNonExistentTask(generateBufYamlCommonTest)
        runNonExistentTask(generateBufGenYamlCommonMain)
        runNonExistentTask(generateBufGenYamlCommonTest)
    }

    @TestFactory
    fun `With Other Targets`() = runGrpcTest {
        val result = runGradle(bufGenerateCommonMain)

        result.assertMainTaskExecuted(
            protoFiles = listOf(
                Path("some.proto")
            ),
            generatedFiles = listOf(
                Path("Some.kt"),
                Path(RPC_INTERNAL, "Some.kt"),
            )
        )
    }

    @TestFactory
    fun `KMP Hierarchy`() = runGrpcTest {
        runKmp(
            SSets.commonMain,
        )

        runKmp(
            SSets.commonTest,
            SSets.commonMain,
        )

        runKmp(
            SSets.nativeMain,
            SSets.commonMain,
        )

        runKmp(
            SSets.nativeTest,
            SSets.commonMain, SSets.nativeMain,
            SSets.commonTest,
        )

        runKmp(
            SSets.jvmMain,
            SSets.commonMain,
        )

        runKmp(
            SSets.jvmTest,
            SSets.commonMain, SSets.jvmMain,
            SSets.commonTest,
        )

        runKmp(
            SSets.jsMain,
            SSets.commonMain,
        )

        runKmp(
            SSets.jsTest,
            SSets.commonMain, SSets.jsMain,
            SSets.commonTest,
        )

        runKmp(
            SSets.appleMain,
            SSets.commonMain, SSets.nativeMain,
        )

        runKmp(
            SSets.appleTest,
            SSets.commonMain, SSets.nativeMain, SSets.appleMain,
            SSets.commonTest, SSets.nativeTest
        )

        runKmp(
            SSets.macosMain,
            SSets.commonMain, SSets.nativeMain, SSets.appleMain,
        )

        runKmp(
            SSets.macosTest,
            SSets.commonMain, SSets.nativeMain, SSets.appleMain, SSets.macosMain,
            SSets.commonTest, SSets.nativeTest, SSets.appleTest
        )

        runKmp(
            SSets.macosArm64Main,
            SSets.commonMain, SSets.nativeMain, SSets.appleMain, SSets.macosMain,
        )

        runKmp(
            SSets.macosArm64Test,
            SSets.commonMain, SSets.nativeMain, SSets.appleMain, SSets.macosMain, SSets.macosArm64Main,
            SSets.commonTest, SSets.nativeTest, SSets.appleTest, SSets.macosTest,
            clean = false,
        )
    }

    private fun GrpcTestEnv.runKmp(
        sourceSet: SSets,
        vararg imports: SSets,
        clean: Boolean = true,
    ) {
        runGradle(bufGenerate(sourceSet))
            .assertKmpSourceSet(sourceSet, *imports)

        if (clean) {
            cleanProtoBuildDir()
        }
    }
}
