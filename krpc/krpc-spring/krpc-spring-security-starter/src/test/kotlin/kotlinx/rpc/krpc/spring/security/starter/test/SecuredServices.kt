/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.security.starter.test

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.rpc.annotations.Rpc
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Rpc
interface SecuredService {
    suspend fun currentUser(): String

    suspend fun currentUserAfterDispatcherSwitch(): String

    suspend fun adminOnly(): String
}

@Service
open class SecuredServiceImplementation : SecuredService {
    override suspend fun currentUser(): String = currentAuthenticationName()

    override suspend fun currentUserAfterDispatcherSwitch(): String = withContext(Dispatchers.Default) {
        currentAuthenticationName()
    }

    @PreAuthorize("hasRole('ADMIN')")
    override suspend fun adminOnly(): String = currentAuthenticationName()

    private fun currentAuthenticationName(): String =
        SecurityContextHolder.getContext().authentication?.name ?: "anonymous"
}
