/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.library.watchdog.ExemptionReason
import kotlinx.library.watchdog.IntentionallyOpen
import kotlinx.rpc.internal.utils.InternalRpcApi

@IntentionallyOpen(reason = ExemptionReason.INTEROP, description = "grpc-java type")
public actual typealias GrpcStatusException = io.grpc.StatusException

@InternalRpcApi
public actual typealias StatusRuntimeException = io.grpc.StatusRuntimeException
