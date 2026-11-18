/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

/**
 * Thrown when the transport of a gRPC client or a gRPC server rejects a setting of the configuration.
 *
 * A transport can also ignore a setting that it doesn't support. Then nothing is thrown.
 */
public class GrpcUnsupportedSettingException(
    message: String,
    cause: Throwable? = null,
) : UnsupportedOperationException(message, cause)
