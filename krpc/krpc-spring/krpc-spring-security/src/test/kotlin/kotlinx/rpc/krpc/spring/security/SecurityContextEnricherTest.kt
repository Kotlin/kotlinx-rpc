/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.security

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame

class SecurityContextEnricherTest {
    @Test
    fun securityContextIsRestoredAfterDispatcherSwitch() = runTest {
        val strategy = SecurityContextHolder.getContextHolderStrategy()
        val originalContext = strategy.context
        val authentication = TestingAuthenticationToken("user", "password", "ROLE_USER")

        try {
            withContext(SecurityContextElement(authentication, strategy)) {
                assertSame(authentication, strategy.context.authentication)

                withContext(Dispatchers.Default) {
                    assertSame(authentication, strategy.context.authentication)
                }

                assertSame(authentication, strategy.context.authentication)
            }

            assertSame(originalContext, strategy.context)
        } finally {
            strategy.context = originalContext
        }
    }

    @Test
    fun nullAuthenticationCreatesAnEmptySecurityContext() = runTest {
        val strategy = SecurityContextHolder.getContextHolderStrategy()

        withContext(SecurityContextElement(null, strategy)) {
            assertNull(strategy.context.authentication)
        }
    }

    @Test
    fun concurrentCoroutinesDoNotShareSecurityContextMutations() = runTest {
        val strategy = SecurityContextHolder.getContextHolderStrategy()
        val authentication = TestingAuthenticationToken("user", "password", "ROLE_USER")
        val changedAuthentication = TestingAuthenticationToken("other", "password", "ROLE_ADMIN")

        withContext(SecurityContextElement(authentication, strategy)) {
            coroutineScope {
                val changed = CompletableDeferred<Unit>()
                val mutatingCoroutine = async(Dispatchers.Default) {
                    strategy.context.authentication = changedAuthentication
                    changed.complete(Unit)
                    yield()
                    assertSame(authentication, strategy.context.authentication)
                }
                val observingCoroutine = async(Dispatchers.Default) {
                    changed.await()
                    assertSame(authentication, strategy.context.authentication)
                }

                mutatingCoroutine.await()
                observingCoroutine.await()
            }
        }
    }
}