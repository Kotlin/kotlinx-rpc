/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

/**
 * The exception that a `GrpcClient` or `GrpcServer` factory throws when the transport rejects
 * a setting of the configuration block. The message names the setting.
 *
 * A transport can also ignore a setting that it doesn't support. The factory then doesn't throw.
 */
public class GrpcUnsupportedSettingException(
    message: String,
    cause: Throwable? = null,
) : UnsupportedOperationException(message, cause)
