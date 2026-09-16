/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// All real logic lives in subprojects:
// - :kotlinx-rpc-grpc-core-shim publishes the gRPC native shim
// - :kotlinx-rpc-protobuf-shim publishes the protobuf native shim
// - :kotlinx-rpc-native-shims-annotation publishes the shared opt-in markers used by both shims
plugins {
    base
    alias(libs.plugins.kotlin.multiplatform) apply false
}

group = "org.jetbrains.kotlinx"

allprojects {
    group = rootProject.group
}
