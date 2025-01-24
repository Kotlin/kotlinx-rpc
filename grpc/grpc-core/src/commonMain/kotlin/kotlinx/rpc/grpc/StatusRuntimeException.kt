/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

/**
 * [Status] in RuntimeException form, for propagating [Status] information via exceptions.
 */
public interface StatusRuntimeException {
    /**
     * The status code as a [Status] object.
     */
    public val status: Status
}

/**
 * Constructor function for the [StatusRuntimeException] class.
 */
public expect fun StatusRuntimeException(status: Status) : StatusRuntimeException
