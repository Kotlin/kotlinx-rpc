/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package kotlinx.rpc.grpc

public actual typealias ServerBuilder<T> = io.grpc.ServerBuilder<T>

internal actual fun ServerBuilder(port: Int): ServerBuilder<*> {
    return io.grpc.ServerBuilder.forPort(port)
}
