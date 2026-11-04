/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server.starter.test.explicit

import kotlinx.rpc.annotations.Rpc
import org.springframework.stereotype.Service

@Rpc
interface ExplicitRpc {
    suspend fun ping(): String
}

@Service
open class ExplicitService : ExplicitRpc {
    override suspend fun ping(): String = "pong"
}
