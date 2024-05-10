/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal

import kotlinx.coroutines.*
import kotlinx.rpc.internal.map.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * Stream scope handles all RPC streams that are launched inside it.
 * Streams are alive until stream scope is. Streams can outlive their initial request scope.
 *
 * Streams are grouped by the request that initiated them.
 * Each group can have a completion callback associated with it.
 *
 * Stream scope is a child of the [CoroutineContext] it was created in.
 * Failure of one request will not cancel all streams in the others.
 */
@InternalRPCApi
@OptIn(InternalCoroutinesApi::class)
public class StreamScope(
    parentContext: CoroutineContext,
    internal val role: Role,
) : CoroutineContext.Element, AutoCloseable {
    internal companion object Key : CoroutineContext.Key<StreamScope>

    override val key: CoroutineContext.Key<StreamScope> = Key

    private val scopeJob = SupervisorJob(parentContext.job)

    private val requests = ConcurrentHashMap<String, CoroutineScope>()

    public fun onScopeCompletion(handler: (Throwable?) -> Unit) {
        scopeJob.invokeOnCompletion(handler)
    }

    public fun onScopeCompletion(callId: String, handler: (Throwable?) -> Unit) {
        getRequestScope(callId).coroutineContext.job.invokeOnCompletion(onCancelling = true, handler = handler)
    }

    public fun cancelRequestScopeById(callId: String, message: String, cause: Throwable?) {
        requests.remove(callId)?.cancel(message, cause)
    }

    // Group stream launches by callId. In case one fails, so do others
    public fun launch(callId: String, block: suspend CoroutineScope.() -> Unit): Job {
        return getRequestScope(callId).launch(block = block)
    }

    override fun close() {
        scopeJob.cancel("Stream scope closed")
        requests.clear()
    }

    private fun getRequestScope(callId: String): CoroutineScope {
        return requests.computeIfAbsent(callId) { CoroutineScope(Job(scopeJob.job)) }
    }

    internal class CallScope(val callId: String) : CoroutineContext.Element {
        object Key : CoroutineContext.Key<CallScope>

        override val key: CoroutineContext.Key<*> = Key
    }

    @InternalRPCApi
    public enum class Role {
        Client, Server;
    }
}

@InternalRPCApi
public fun CoroutineContext.withClientStreamScope(): CoroutineContext = withStreamScope(StreamScope.Role.Client)

@InternalRPCApi
public fun CoroutineContext.withServerStreamScope(): CoroutineContext = withStreamScope(StreamScope.Role.Server)

@OptIn(InternalCoroutinesApi::class)
internal fun CoroutineContext.withStreamScope(role: StreamScope.Role): CoroutineContext {
    return this + StreamScope(this, role).apply {
        this@withStreamScope.job.invokeOnCompletion(onCancelling = true) { close() }
    }
}

@InternalRPCApi
public suspend fun streamScopeOrNull(): StreamScope? {
    return currentCoroutineContext()[StreamScope.Key]
}

@InternalRPCApi
public fun streamScopeOrNull(scope: CoroutineScope): StreamScope? {
    return scope.coroutineContext[StreamScope.Key]
}

internal fun noStreamScopeError(): Nothing {
    error(
        "Stream scopes can only be used inside the 'streamScoped' block. \n" +
                "To use stream scope API on a client - wrap your call with 'streamScoped' block.\n" +
                "To use stream scope API on a server - use must use 'streamScoped' block for this call on a client."
    )
}

@InternalRPCApi
public suspend fun <T> callScoped(callId: String, block: suspend CoroutineScope.() -> T): T {
    val context = currentCoroutineContext()

    if (context[StreamScope.CallScope.Key] != null) {
        error("Nested callScoped calls are not allowed")
    }

    val callScope = StreamScope.CallScope(callId)

    return withContext(callScope, block)
}

/**
 * Defines lifetime for all RPC streams that are used inside it.
 * When the [block] ends - all streams that were created inside it are canceled.
 * The same happens when an exception is thrown.
 *
 * All RPC calls that use streams, either sending or receiving them,
 * MUST use this scope to define their lifetime.
 *
 * Lifetimes inside [streamScoped] are hierarchical,
 * meaning that there is parent lifetime for all calls inside this block,
 * and each call has its own lifetime independent of others.
 * This also means that all streams from one call share the same lifetime.
 *
 * Examples:
 * ```kotlin
 * streamScoped {
 *     val flow = flow { /* ... */ }
 *     service.sendStream(flow) // will stop sending updates when 'streamScoped' block is finished
 * }
 * ```
 *
 * ```kotlin
 * streamScoped {
 *     launch {
 *         val flow1 = flow { /* ... */ }
 *         service.sendStream(flow)
 *     }
 *
 *     // if call with 'flow1' is canceled or failed - this flow will continue working
 *     launch {
 *         val flow2 = flow { /* ... */ }
 *         service.sendStream(flow)
 *     }
 * }
 * ```
 */
public suspend fun <T> streamScoped(block: suspend CoroutineScope.() -> T): T {
    val context = currentCoroutineContext()

    if (context[StreamScope.Key] != null) {
        error(
            "One of the following caused a failure: \n" +
                    "- nested 'streamScoped' calls are not allowed.\n" +
                    "- 'streamScoped' calls are not allowed in server RPC services."
        )
    }

    val streamScope = StreamScope(context, StreamScope.Role.Client)

    return withContext(streamScope) {
        streamScope.use {
            block()
        }
    }
}

/**
 * This is a callback that will run when stream scope (created by [streamScoped] function) ends.
 * Typically, this is used to release stream resources that may be occupied by a call:
 * ```kotlin
 * // service on server
 * override suspend fun returnStateFlow(): StateFlow<Int> {
 *     val state = MutableStateFlow(-1)
 *
 *     incomingHotFlowJob = launch {
 *         repeat(Int.MAX_VALUE) { value ->
 *             state.value = value
 *
 *             delay(1000) // intense work
 *         }
 *     }
 *
 *     // release resources allocated for state flow, when it is closed on the client
 *     invokeOnStreamScopeCompletion {
 *         incomingHotFlowJob.cancel()
 *     }
 *
 *     return state
 * }
 * ```
 */
@ExperimentalRPCApi
public suspend fun invokeOnStreamScopeCompletion(throwIfNoScope: Boolean = true, block: (Throwable?) -> Unit) {
    val streamScope = streamScopeOrNull() ?: noStreamScopeError()

    if (streamScope.role == StreamScope.Role.Client) {
        streamScope.onScopeCompletion(block)
        return
    }

    val callScope = coroutineContext[StreamScope.CallScope.Key]

    when {
        callScope != null -> streamScope.onScopeCompletion(callScope.callId, block)

        throwIfNoScope -> error(
            "'invokeOnStreamScopeCompletion' can only be called with corresponding 'streamScoped' block on a client"
        )
    }
}
