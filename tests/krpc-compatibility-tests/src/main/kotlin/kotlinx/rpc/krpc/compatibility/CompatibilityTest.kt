/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.compatibility

import kotlinx.rpc.RpcClient
import kotlinx.rpc.krpc.KrpcProtocol

interface CompatibilityTest {
    fun getAllTests(): Map<String, suspend (RpcClient<KrpcProtocol>) -> Unit>
}
