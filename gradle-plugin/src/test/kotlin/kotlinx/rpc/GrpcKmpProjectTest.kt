/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.rpc.base.GrpcBaseTest
import org.junit.jupiter.api.TestInstance
import kotlin.io.path.Path
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class GrpcKmpProjectTest : GrpcBaseTest() {
    override val isKmp: Boolean = true

    @Test
    fun `Minimal gRPC Configuration`() = runGrpcTest {
        val result = runGradle(bufGenerateMain)

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

    @Test
    fun `No gRPC`() = runGrpcTest {
        runNonExistentTask(bufGenerateMain)
        runNonExistentTask(bufGenerateTest)
        runNonExistentTask(processMainProtoFiles)
        runNonExistentTask(processTestProtoFiles)
        runNonExistentTask(processTestImportProtoFiles)
        runNonExistentTask(generateBufYamlMain)
        runNonExistentTask(generateBufYamlTest)
        runNonExistentTask(generateBufGenYamlMain)
        runNonExistentTask(generateBufGenYamlTest)
    }

    @Test
    fun `Test-Only Sources`() = runGrpcTest {
        val result = runGradle(bufGenerateTest)

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

    @Test
    fun `Main and Test Mixed Sources`() = runGrpcTest {
        val mainRun = runGradle(bufGenerateMain)

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

        val testRun = runGradle(bufGenerateTest)

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

    @Test
    fun `No JVM Targets`() = runGrpcTest {
        runNonExistentTask(bufGenerateMain)
        runNonExistentTask(bufGenerateTest)
        runNonExistentTask(processMainProtoFiles)
        runNonExistentTask(processTestProtoFiles)
        runNonExistentTask(processTestImportProtoFiles)
        runNonExistentTask(generateBufYamlMain)
        runNonExistentTask(generateBufYamlTest)
        runNonExistentTask(generateBufGenYamlMain)
        runNonExistentTask(generateBufGenYamlTest)
    }

    @Test
    fun `With Other Targets`() = runGrpcTest {
        val result = runGradle(bufGenerateMain)

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
}
