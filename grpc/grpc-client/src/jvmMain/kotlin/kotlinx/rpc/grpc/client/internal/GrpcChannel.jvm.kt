/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import io.grpc.Channel
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public actual typealias GrpcChannel = Channel
