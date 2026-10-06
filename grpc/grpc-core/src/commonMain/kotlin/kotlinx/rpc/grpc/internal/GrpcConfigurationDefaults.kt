/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * Shared configuration defaults applied explicitly by each gRPC implementation.
 * This prevents different defaults on different platforms.
 */
@InternalRpcApi
public object GrpcConfigurationDefaults {
    public const val MAX_INBOUND_MESSAGE_SIZE: Int = 4 * 1024 * 1024
    public const val MAX_INBOUND_METADATA_SIZE: Int = 8 * 1024
    public val CLIENT_IDLE_TIMEOUT: Duration = 30.minutes
}
