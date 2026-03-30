/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.rpc.nativedeps.shims.NativeShimFixtureTestSupport

class GrpcShimFixtureTest : NativeShimFixtureTestSupport() {
    override val systemPropertyPrefix: String = "grpcShim"
    override val resourcesSubdirectory: String = "grpc"
    override val testProjectPrefix: String = "grpc-shim"
    override val dependencyCoordinatePrefix: String = "kotlinx-rpc-grpc-core-shim"
    override val expectedDiagnosticMessage: String =
        "grpc-shim native interop declarations are internal implementation details"
    override val cinteropKlibName: String = "cinterop-grpcCoreInterop.klib"
    override val cinteropPackagePath: String = "kotlinx.rpc.grpc.internal.cinterop"
    override val expectedAnnotationMarker: String = "InternalNativeRpcApi"
}
