/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.grpc

import kotlinx.rpc.protoc.gen.core.runGenerator

fun main() {
    runGenerator(GrpcProtocGenPlugin)
}
