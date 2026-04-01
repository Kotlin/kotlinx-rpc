/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.rpc.nativedeps.shims.NativeShimFixtureTestSupport

class ProtobufShimFixtureTest : NativeShimFixtureTestSupport() {
    override val systemPropertyPrefix: String = "protobufShim"
    override val resourcesSubdirectory: String = "protobuf"
    override val testProjectPrefix: String = "protobuf-shim"
    override val dependencyCoordinatePrefix: String = "kotlinx-rpc-protobuf-shim"
    override val expectedDiagnosticMessage: String =
        "protobuf-shim native interop declarations are internal implementation details"
    override val cinteropKlibName: String = "cinterop-libprotowire.klib"
    override val cinteropPackagePath: String = "kotlinx.rpc.protobuf.internal.cinterop"
    override val expectedAnnotationMarker: String = "InternalNativeProtobufApi"
}
