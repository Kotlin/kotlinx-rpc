/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class, ExperimentalStdlibApi::class)

package kotlinx.rpc.grpc.internal

import cnames.structs.grpc_call
import kotlinx.atomicfu.atomic
import kotlinx.cinterop.*
import kotlinx.coroutines.suspendCancellableCoroutine
import libgrpcpp_c.*
import platform.posix.memset
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner

/**
 * A coroutine wrapper around the grpc completion_queue, which manages message operations.
 * It is based on the "new" callback API; therefore, there are no kotlin-side threads required to poll
 * the queue.
 */
internal class CompletionQueue {

    private enum class State { OPEN, SHUTTING_DOWN, CLOSED }

    private val state = atomic(State.OPEN)

    // internal as it must be accessible from the SHUTDOWN_CB,
    // but it shouldn't be used from outside this file.
    @Suppress("PropertyName")
    internal val _shutdownDone = kotlinx.coroutines.CompletableDeferred<Unit>()

    // used for spinning lock. false means not used (available)
    private val batchStartGuard = atomic(false)

    private val thisStableRef = StableRef.create(this)

    private val shutdownFunctor = nativeHeap.alloc<grpc_cb_tag> {
        functor.functor_run = SHUTDOWN_CB
        user_data = thisStableRef.asCPointer()
    }.reinterpret<grpc_completion_queue_functor>()


    val raw = grpc_completion_queue_create_for_callback(shutdownFunctor.ptr, null)

    private val thisStableRefCleaner = createCleaner(thisStableRef) { it.dispose() }
    private val shutdownFunctorCleaner = createCleaner(shutdownFunctor) { nativeHeap.free(it) }

    init {
        // Assert grpc_iomgr_run_in_background() to guarantee that the event manager provides
        // IO threads and supports the callback API.
        require(kgrpc_iomgr_run_in_background()) { "The gRPC iomgr is not running background threads, required for callback based APIs." }
    }

    suspend fun runBatch(call: CPointer<grpc_call>, ops: CPointer<grpc_op>, nOps: ULong) =
        suspendCancellableCoroutine<grpc_call_error> { cont ->
            val tag = newCbTag(cont, OPS_COMPLETE_CB)

            var err = grpc_call_error.GRPC_CALL_ERROR
            // synchronizes access to grpc_call_start_batch
            withBatchStartLock {
                if (state.value != State.OPEN) {
                    deleteCbTag(tag)
                    cont.resume(grpc_call_error.GRPC_CALL_ERROR_COMPLETION_QUEUE_SHUTDOWN)
                    return@suspendCancellableCoroutine
                }

                err = grpc_call_start_batch(call, ops, nOps, tag, null)
            }

            if (err != grpc_call_error.GRPC_CALL_OK) {
                // if the call was not successful, the callback will not be invoked.
                deleteCbTag(tag)
                cont.resume(err)
                return@suspendCancellableCoroutine
            }


            cont.invokeOnCancellation {
                @Suppress("UnusedExpression")
                // keep reference, otherwise the cleaners might get invoked before the batch finishes
                this
                // cancel the call if one of its batches is canceled.
                // grpc_call_cancel is thread-safe and can be called several times.
                // the callback is invoked anyway, so the tag doesn't get deleted here.
                grpc_call_cancel(call, null)
            }
        }

    suspend fun shutdown() {
        if (!state.compareAndSet(State.OPEN, State.SHUTTING_DOWN)) {
            // the first call to shutdown() makes transition and to SHUTTING_DOWN and
            // initiates shut down. all other invocations await the shutdown.
            _shutdownDone.await()
            return
        }

        // wait until all batch operations since the state transitions were started.
        // this is required to prevent batches from starting after shutdown was initialized
        withBatchStartLock { }

        grpc_completion_queue_shutdown(raw)
        _shutdownDone.await()
        state.value = State.CLOSED
    }

    private inline fun withBatchStartLock(block: () -> Unit) {
        try {
            // spin until this thread occupies the guard
            @Suppress("ControlFlowWithEmptyBody")
            while (!batchStartGuard.compareAndSet(expect = false, update = true)) {
            }
            block()
        } finally {
            // set guard to "not occupied"
            batchStartGuard.value = false
        }
    }
}

// kq stands for kompletion_queue lol
@CName("kq_ops_complete_cb")
private fun opsCompleteCb(functor: CPointer<grpc_completion_queue_functor>?, ok: Int) {
    val tag = functor!!.reinterpret<grpc_cb_tag>()
    val cont = tag.pointed.user_data!!.asStableRef<Continuation<grpc_call_error>>().get()
    deleteCbTag(tag)
    if (ok != 0) cont.resume(grpc_call_error.GRPC_CALL_OK)
    else cont.resumeWithException(IllegalStateException("batch failed"))
}

@CName("kq_shutdown_cb")
private fun shutdownCb(functor: CPointer<grpc_completion_queue_functor>?, ok: Int) {
    val tag = functor!!.reinterpret<grpc_cb_tag>()
    val cq = tag.pointed.user_data!!.asStableRef<CompletionQueue>().get()
    cq._shutdownDone.complete(Unit)
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