/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.rpc.base.GrpcBaseTest
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.TestInstance
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class GrpcJvmProjectTest : GrpcBaseTest() {
    override val isKmp: Boolean = false

    @Test
    fun `Minimal gRPC Configuration`() {
        val result = runGradle(bufGenerateMain)

        assertEquals(TaskOutcome.SUCCESS, result.protoTaskOutcome(bufGenerateMain))
        assertEquals(TaskOutcome.SUCCESS, result.protoTaskOutcome(processMainProtoFiles))
        assertEquals(TaskOutcome.SUCCESS, result.protoTaskOutcome(generateBufYamlMain))
        assertEquals(TaskOutcome.SUCCESS, result.protoTaskOutcome(generateBufGenYamlMain))

        result.assertProtoTaskNotExecuted(bufGenerateTest)
        result.assertProtoTaskNotExecuted(processTestProtoFiles)
        result.assertProtoTaskNotExecuted(processTestImportProtoFiles)
        result.assertProtoTaskNotExecuted(generateBufYamlTest)
        result.assertProtoTaskNotExecuted(generateBufGenYamlTest)

        assertSourceCodeGenerated(
            mainSourceSet,
            Path("Some.kt"),
            Path(RPC_INTERNAL, "Some.kt")
        )

        assertWorkspaceProtoFilesCopied(mainSourceSet, Path("some.proto"))
        assertWorkspaceImportProtoFilesCopied(mainSourceSet)
    }

    @Test
    fun `No gRPC`() {
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
}
