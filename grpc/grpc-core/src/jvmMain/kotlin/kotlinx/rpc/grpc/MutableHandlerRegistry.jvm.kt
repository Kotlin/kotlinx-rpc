/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package kotlinx.rpc.grpc

internal actual typealias HandlerRegistry = io.grpc.HandlerRegistry

internal actual typealias MutableHandlerRegistry = io.grpc.util.MutableHandlerRegistry
