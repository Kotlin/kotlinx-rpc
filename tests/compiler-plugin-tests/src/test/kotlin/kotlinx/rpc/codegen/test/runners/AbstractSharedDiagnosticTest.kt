/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.test.runners

import org.jetbrains.kotlin.test.Constructor
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.directives.DiagnosticsDirectives
import org.jetbrains.kotlin.test.directives.TestDumpDirectives
import org.jetbrains.kotlin.test.services.SourceFilePreprocessor

/**
 * Runs protocol-agnostic test data from `src/testData/diagnosticsShared` for a specific protocol.
 *
 * FIR dumps differ between protocols (generated supertypes), so each protocol has its own dump file:
 * `rpcService.kt` -> `rpcService.krpc.fir.txt` / `rpcService.grpc.fir.txt`.
 */
abstract class AbstractSharedDiagnosticTest(
    private val protocolImportPreprocessor: Constructor<SourceFilePreprocessor>,
    private val protocol: String,
) : AbstractDiagnosticTest() {
    override fun configure(builder: TestConfigurationBuilder) {
        super.configure(builder)

        with(builder) {
            useSourcePreprocessor(protocolImportPreprocessor)

            defaultDirectives {
                // inserted before the dump extension: <name>.<protocol>.fir.txt
                TestDumpDirectives.DUMP_CLASSIFIER with protocol
            }
        }
    }
}

open class AbstractKrpcSharedDiagnosticTest : AbstractSharedDiagnosticTest(
    protocolImportPreprocessor = ::KrpcProtocolImportPreprocessor,
    protocol = "krpc",
)

open class AbstractGrpcSharedDiagnosticTest : AbstractSharedDiagnosticTest(
    protocolImportPreprocessor = ::GrpcProtocolImportPreprocessor,
    protocol = "grpc",
) {
    override fun configure(builder: TestConfigurationBuilder) {
        super.configure(builder)

        builder.defaultDirectives {
            // gRPC-only rules, covered by diagnostics/grpc.kt
            DiagnosticsDirectives.DIAGNOSTICS with listOf(
                "-MULTIPLE_PARAMETERS_IN_GRPC_SERVICE",
                "-NULLABLE_PARAMETER_IN_GRPC_SERVICE",
                "-NULLABLE_RETURN_TYPE_IN_GRPC_SERVICE",
                "-NON_TOP_LEVEL_CLIENT_STREAMING_IN_RPC_SERVICE",
            )
        }
    }
}
