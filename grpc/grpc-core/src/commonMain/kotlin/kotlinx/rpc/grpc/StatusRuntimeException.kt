/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

public interface StatusRuntimeException {
    public val status: Status
}

public expect fun StatusRuntimeException(status: Status) : StatusRuntimeException
