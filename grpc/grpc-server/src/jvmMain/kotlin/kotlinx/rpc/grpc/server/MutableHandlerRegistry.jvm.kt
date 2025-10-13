/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package kotlinx.rpc.grpc.server

import io.grpc.HandlerRegistry
import io.grpc.util.MutableHandlerRegistry

/**
 * Registry of services and their methods used by servers to dispatching incoming calls.
 */
public actual typealias HandlerRegistry = HandlerRegistry

internal actual typealias MutableHandlerRegistry = MutableHandlerRegistry
