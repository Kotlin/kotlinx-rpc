/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.security

import kotlinx.coroutines.ThreadContextElement
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolderStrategy
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

internal class SecurityContextElement(
    private val authentication: Authentication?,
    private val strategy: SecurityContextHolderStrategy,
) : AbstractCoroutineContextElement(Key), ThreadContextElement<SecurityContext> {
    companion object Key : CoroutineContext.Key<SecurityContextElement>

    override fun updateThreadContext(context: CoroutineContext): SecurityContext {
        val previousContext = strategy.context
        strategy.context = strategy.createEmptyContext().also { it.authentication = authentication }
        return previousContext
    }

    override fun restoreThreadContext(context: CoroutineContext, oldState: SecurityContext) {
        strategy.context = oldState
    }
}
