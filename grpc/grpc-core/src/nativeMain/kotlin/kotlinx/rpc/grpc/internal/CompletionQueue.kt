/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class, ExperimentalStdlibApi::class)

package kotlinx.rpc.grpc.internal

import cnames.structs.grpc_call
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.cinterop.*
import kotlinx.coroutines.CompletableDeferred
import libgrpcpp_c.*
import platform.posix.memset
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner

internal sealed interface BatchResult {
    object Success : BatchResult
    object ResultError : BatchResult
    object CQShutdown : BatchResult
    data class CallError(val error: grpc_call_error) : BatchResult
}

/**
 * A coroutine wrapper around the grpc completion_queue, which manages message operations.
 * It is based on the "new" callback API; therefore, there are no kotlin-side threads required to poll
 * the queue.
 */
internal class CompletionQueue {

    internal enum class State { OPEN, SHUTTING_DOWN, CLOSED }

    // if the queue was called with forceShutdown = true,
    // it will reject all new batches and wait for all current ones to finish.
    private var forceShutdown = false

    // internal as it must be accessible from the SHUTDOWN_CB,
    // but it shouldn't be used from outside this file.
    @Suppress("PropertyName")
    internal val _state = atomic(State.OPEN)

    // internal as it must be accessible from the SHUTDOWN_CB,
    // but it shouldn't be used from outside this file.
    @Suppress("PropertyName")
    internal val _shutdownDone = CompletableDeferred<Unit>()

    // used for spinning lock. false means not used (available)
    private val batchStartGuard = SynchronizedObject()

    private val thisStableRef = StableRef.create(this)

    private val shutdownFunctor = nativeHeap.alloc<grpc_cb_tag> {
        functor.functor_run = SHUTDOWN_CB
        user_data = thisStableRef.asCPointer()
    }.reinterpret<grpc_completion_queue_functor>()


    val raw = grpc_completion_queue_create_for_callback(shutdownFunctor.ptr, null)

    @Suppress("unused")
    private val thisStableRefCleaner = createCleaner(thisStableRef) { it.dispose() }

    @Suppress("unused")
    private val shutdownFunctorCleaner = createCleaner(shutdownFunctor) { nativeHeap.free(it) }

    init {
        // Assert grpc_iomgr_run_in_background() to guarantee that the event manager provides
        // IO threads and supports the callback API.
        require(kgrpc_iomgr_run_in_background()) { "The gRPC iomgr is not running background threads, required for callback based APIs." }
    }

    // TODO: Remove this method
    fun runBatch(call: NativeClientCall<*, *>, ops: CPointer<grpc_op>, nOps: ULong) =
        runBatch(call.raw, ops, nOps)

    fun runBatch(call: CPointer<grpc_call>, ops: CPointer<grpc_op>, nOps: ULong): CompletableDeferred<BatchResult> {
        val completion = CompletableDeferred<BatchResult>()
        val tag = newCbTag(completion, OPS_COMPLETE_CB)

        var err = grpc_call_error.GRPC_CALL_ERROR
        synchronized(batchStartGuard) {
            // synchronizes access to grpc_call_start_batch
            if (forceShutdown || _state.value == State.CLOSED) {
                // if the queue is either closed or in the process of a FORCE shutdown,
                // new batches will instantly fail.
                deleteCbTag(tag)
                completion.complete(BatchResult.CQShutdown)
                return completion
            }

            err = grpc_call_start_batch(call, ops, nOps, tag, null)
        }

        if (err != grpc_call_error.GRPC_CALL_OK) {
            // if the call was not successful, the callback will not be invoked.
            deleteCbTag(tag)
            completion.complete(BatchResult.CallError(err))
            return completion
        }

        return completion
    }

    // must not be canceled as it cleans resources and sets the state to CLOSED
    fun shutdown(force: Boolean = false): CompletableDeferred<Unit> {
        if (force) {
            forceShutdown = true
        }
        if (!_state.compareAndSet(State.OPEN, State.SHUTTING_DOWN)) {
            // the first call to shutdown() makes transition and to SHUTTING_DOWN and
            // initiates shut down. all other invocations await the shutdown.
            return _shutdownDone
        }

        // wait until all batch operations since the state transitions were started.
        // this is required to prevent batches from starting after shutdown was initialized.
        // however, this lock will be available very fast, so it shouldn't be a problem.'
        synchronized(batchStartGuard) {
            grpc_completion_queue_shutdown(raw)
        }

        return _shutdownDone
    }
}

// kq stands for kompletion_queue lol
@CName("kq_ops_complete_cb")
private fun opsCompleteCb(functor: CPointer<grpc_completion_queue_functor>?, ok: Int) {
    val tag = functor!!.reinterpret<grpc_cb_tag>()
    val cont = tag.pointed.user_data!!.asStableRef<CompletableDeferred<BatchResult>>().get()
    deleteCbTag(tag)
    if (ok != 0) cont.complete(BatchResult.Success)
    else cont.complete(BatchResult.ResultError)
}

@CName("kq_shutdown_cb")
private fun shutdownCb(functor: CPointer<grpc_completion_queue_functor>?, ok: Int) {
    val tag = functor!!.reinterpret<grpc_cb_tag>()
    val cq = tag.pointed.user_data!!.asStableRef<CompletionQueue>().get()
    cq._shutdownDone.complete(Unit)
    cq._state.value = CompletionQueue.State.CLOSED
    grpc_completion_queue_destroy(cq.raw)
}

private val OPS_COMPLETE_CB = staticCFunction(::opsCompleteCb)
private val SHUTDOWN_CB = staticCFunction(::shutdownCb)

private fun newCbTag(
    userData: Any,
    cb: CPointer<CFunction<(CPointer<grpc_completion_queue_functor>?, Int) -> Unit>>,
): CPointer<grpc_cb_tag> {
    val tag = nativeHeap.alloc<grpc_cb_tag>()
    memset(tag.ptr, 0, sizeOf<grpc_cb_tag>().convert())
    tag.functor.functor_run = cb
    tag.user_data = StableRef.create(userData).asCPointer()
    return tag.ptr
}

@CName("grpc_cb_tag_destroy")
private fun deleteCbTag(tag: CPointer<grpc_cb_tag>) {
    tag.pointed.user_data!!.asStableRef<Any>().dispose()
    nativeHeap.free(tag)
}