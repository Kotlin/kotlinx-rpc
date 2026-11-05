/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package kotlinx.rpc.grpc.server

import io.grpc.HandlerRegistry
import io.grpc.util.MutableHandlerRegistry
import org.jetbrains.kotlinx.library.api.watchdog.ExemptionReason
import org.jetbrains.kotlinx.library.api.watchdog.IntentionallyOpen

/**
 * Registry of services and their methods used by servers to dispatching incoming calls.
 */
@IntentionallyOpen(reason = ExemptionReason.API_DESIGN)
public actual typealias GrpcHandlerRegistry = HandlerRegistry

internal actual typealias GrpcMutableHandlerRegistry = MutableHandlerRegistry
