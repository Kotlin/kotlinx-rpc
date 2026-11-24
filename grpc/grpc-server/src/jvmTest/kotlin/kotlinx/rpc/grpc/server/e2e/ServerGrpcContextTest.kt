/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.e2e

import io.grpc.Context
import io.grpc.testing.integration.SimpleRequest
import io.grpc.testing.integration.SimpleResponse
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.get
import kotlinx.rpc.grpc.server.testing.AWAIT_TIMEOUT
import kotlinx.rpc.grpc.server.testing.assertGrpcStatus
import kotlinx.rpc.grpc.server.testing.clientInterceptor
import kotlinx.rpc.grpc.server.testing.grpcServerTest
import kotlinx.rpc.grpc.server.testing.serverInterceptor
import kotlinx.rpc.grpc.server.testing.zeros
import kotlinx.rpc.test.EventLog

class ServerGrpcContextTest {
    @Test
    fun concurrentCallsKeepSeparateContextsWhenAnotherCallIsCancelledOrFails() = grpcServerTest {
        val nextId = AtomicInteger()
        val gates = (1..3).map { registerNewGate("call $it") }
        val cancelled = CompletableDeferred<Unit>()
        val interceptorContexts = EventLog<Pair<String, Context>>()
        val serviceContexts = EventLog<Pair<String, Context>>()
        start(
            serverConfig = {
                intercept(serverInterceptor {
                    val id = checkNotNull(requestHeaders["x-call-id"])
                    interceptorContexts.add(id to context)
                })
            },
            clientConfig = {
                intercept(clientInterceptor {
                    val id = nextId.incrementAndGet().toString()
                    requestHeaders.append("x-call-id", id)
                })
            },
        ) {
            unaryCall = { request ->
                val serviceContext = Context.current()
                withContext(Dispatchers.IO) {
                    val id = request.responseSize
                    assertSame(serviceContext, Context.current())
                    serviceContexts.add(id.toString() to Context.current())
                    try {
                        gates[id - 1].pass()
                    } catch (error: CancellationException) {
                        assertEquals(1, id)
                        assertSame(serviceContext, Context.current())
                        cancelled.complete(Unit)
                        throw error
                    }
                    assertSame(serviceContext, Context.current())
                    if (id == 2) {
                        throw GrpcStatusCode.ABORTED.asException("Call failed")
                    }
                    SimpleResponse { payload = zeros(id) }
                }
            }
        }

        coroutineScope {
            val first = async { testService.unaryCall(SimpleRequest { responseSize = 1 }) }
            gates[0].awaitReached(AWAIT_TIMEOUT)
            val second = async {
                assertGrpcStatus(GrpcStatusCode.ABORTED) {
                    testService.unaryCall(SimpleRequest { responseSize = 2 })
                }
            }
            gates[1].awaitReached(AWAIT_TIMEOUT)
            val third = async { testService.unaryCall(SimpleRequest { responseSize = 3 }) }
            gates[2].awaitReached(AWAIT_TIMEOUT)

            first.cancelAndJoin()
            withTimeout(AWAIT_TIMEOUT) { cancelled.await() }
            gates[0].open()
            assertFalse(second.isCompleted)
            assertFalse(third.isCompleted)

            gates[1].open()
            withTimeout(AWAIT_TIMEOUT) { second.await() }
            assertFalse(third.isCompleted)

            gates[2].open()
            assertEquals(zeros(3), withTimeout(AWAIT_TIMEOUT) { third.await() }.payload)
        }

        val contexts = serviceContexts.events.toMap()
        assertEquals(setOf("1", "2", "3"), contexts.keys)
        interceptorContexts.events.forEach { (id, context) -> assertSame(context, contexts.getValue(id)) }
        assertNotSame(contexts.getValue("1"), contexts.getValue("2"))
        assertNotSame(contexts.getValue("1"), contexts.getValue("3"))
        assertNotSame(contexts.getValue("2"), contexts.getValue("3"))
    }

    @Test
    fun serviceContextMatchesTheCallScopeAcrossSuspension() = grpcServerTest {
        val scopeContext = CompletableDeferred<Context>()
        val serviceContext = CompletableDeferred<Context>()
        start(serverConfig = { intercept(serverInterceptor { scopeContext.complete(context) }) }) {
            unaryCall = {
                withContext(Dispatchers.IO) { serviceContext.complete(Context.current()) }
                SimpleResponse {}
            }
        }

        testService.unaryCall(SimpleRequest {})

        withTimeout(AWAIT_TIMEOUT) { assertSame(scopeContext.await(), serviceContext.await()) }
    }

    @Test
    fun clientDeadlineIsVisibleInTheServiceContext() = grpcServerTest {
        val context = CompletableDeferred<Context>()
        start(clientConfig = { intercept(clientInterceptor { callOptions.timeout = 5.seconds }) }) {
            unaryCall = {
                context.complete(Context.current())
                SimpleResponse {}
            }
        }

        testService.unaryCall(SimpleRequest {})

        assertNotNull(withTimeout(AWAIT_TIMEOUT) { context.await() }.deadline)
    }
}
