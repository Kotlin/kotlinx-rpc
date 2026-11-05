/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.rpc.internal.utils.InternalRpcApi
import org.jetbrains.kotlinx.library.api.watchdog.ExemptionReason
import org.jetbrains.kotlinx.library.api.watchdog.IntentionallyOpen

@IntentionallyOpen(reason = ExemptionReason.INTEROP, description = "grpc-java type")
public actual typealias GrpcStatusException = io.grpc.StatusException

@InternalRpcApi
public actual typealias StatusRuntimeException = io.grpc.StatusRuntimeException
