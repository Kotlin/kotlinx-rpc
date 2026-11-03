/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.test.runners

import org.jetbrains.kotlin.test.model.TestFile
import org.jetbrains.kotlin.test.services.ReversibleSourceFilePreprocessor
import org.jetbrains.kotlin.test.services.TestServices

private const val PROTOCOL_PLACEHOLDER = "// PROTOCOL_IMPORT"

/**
 * Replaces the `// PROTOCOL_IMPORT` placeholder in shared test data
 * with imports of the protocol's declarations under common aliases,
 * so the same test file can be run for every protocol:
 * - `Protocol` - the service annotation
 * - `ProtocolService` - the service supertype
 * - `ProtocolClient` - the client
 * - `ProtocolServer` - the server
 */
abstract class ProtocolImportPreprocessor(
    testServices: TestServices,
    annotation: String,
    service: String,
    client: String,
    server: String,
) : ReversibleSourceFilePreprocessor(testServices) {
    private val imports = listOf(
        "import $annotation as Protocol",
        "import $service as ProtocolService",
        "import $client as ProtocolClient",
        "import $server as ProtocolServer",
    ).joinToString("\n")

    override fun process(file: TestFile, content: String): String {
        return content.replace(PROTOCOL_PLACEHOLDER, imports)
    }

    // markers are rendered into the preprocessed text, revert so it compares against the original file
    override fun revert(file: TestFile, actualContent: String): String {
        return actualContent.replace(imports, PROTOCOL_PLACEHOLDER)
    }
}

class KrpcProtocolImportPreprocessor(testServices: TestServices) : ProtocolImportPreprocessor(
    testServices = testServices,
    annotation = "kotlinx.rpc.krpc.annotations.Krpc",
    service = "kotlinx.rpc.krpc.KrpcService",
    client = "kotlinx.rpc.krpc.client.KrpcClient",
    server = "kotlinx.rpc.krpc.server.KrpcServer",
)

class GrpcProtocolImportPreprocessor(testServices: TestServices) : ProtocolImportPreprocessor(
    testServices = testServices,
    annotation = "kotlinx.rpc.grpc.annotations.Grpc",
    service = "kotlinx.rpc.grpc.GrpcService",
    client = "kotlinx.rpc.grpc.client.GrpcClient",
    server = "kotlinx.rpc.grpc.server.GrpcServer",
)
