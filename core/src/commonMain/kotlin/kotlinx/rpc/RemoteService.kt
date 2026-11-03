/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

/**
 * Marker interface for an RPC service.
 *
 * @see Rpc
 */
@Deprecated("Use of RemoteService is deprecated. Use @Grpc or @Krpc.", level = DeprecationLevel.ERROR)
public interface RemoteService
