/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import kotlin.time.Duration

/**
 * The collection of runtime options for a new gRPC call.
 *
 * This class allows configuring per-call behavior such as timeouts.
 */
public class GrpcCallOptions {
    /**
     * The maximum duration to wait for the RPC to complete.
     *
     * If set, the RPC will be canceled (with `DEADLINE_EXCEEDED`)
     * if it does not complete within the specified duration.
     * The timeout is measured from the moment the call is initiated.
     * If `null`, no timeout is applied, and the call may run indefinitely.
     *
     * @see Duration
     */
    public var timeout: Duration? = null
}
