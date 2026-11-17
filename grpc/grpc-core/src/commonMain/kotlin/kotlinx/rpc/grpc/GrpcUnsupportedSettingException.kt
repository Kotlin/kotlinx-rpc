/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

/**
 * Thrown when the transport of a gRPC client or a gRPC server doesn't support a setting
 * of the configuration.
 */
public class GrpcUnsupportedSettingException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
