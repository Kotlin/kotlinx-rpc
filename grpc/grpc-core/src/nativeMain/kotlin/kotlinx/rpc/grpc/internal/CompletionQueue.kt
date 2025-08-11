/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class, ExperimentalStdlibApi::class)

package kotlinx.rpc.grpc.internal

import cnames.structs.grpc_call
import kotlinx.atomicfu.atomic
import kotlinx.cinterop.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import libgrpcpp_c.*
import platform.posix.memset
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner

internal class CompletionQueue {

    // internal as it must be accessible from the SHUTDOWN_CB
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

    suspend fun runBatch(call: CPointer<grpc_call>, ops: CPointer<grpc_op>, nOps: ULong) = coroutineScope {
        suspendCancellableCoroutine<Unit> { cont ->
            val tag = newCbTag(cont, OPS_COMPLETE_CB)

            // synchronizes access to grpc_call_start_batch
            while (!batchStartGuard.compareAndSet(expect = false, update = true)) {
                // could not be set to true (currently hold by different thread)
            }

            var err: UInt
            try {
                err = grpc_call_start_batch(call, ops, nOps, tag, null)
            } finally {
                batchStartGuard.value = false
            }

            if (err != 0u) {
                deleteCbTag(tag)
                cont.resumeWithException(IllegalStateException("start_batch err=$err"))
                return@suspendCancellableCoroutine
            }


            cont.invokeOnCancellation {
                this // keep reference, otherwise the cleaners might get cleaned before batch finishes
                TODO("Implement call operation cancellation")
            }
        }
    }

    suspend fun shutdown() {
        if (_shutdownDone.isCompleted) return
        grpc_completion_queue_shutdown(raw)
        _shutdownDone.await()
    }
}

@CName("kq_ops_complete_cb")
private fun opsCompleteCb(functor: CPointer<grpc_completion_queue_functor>?, ok: Int) {
    val tag = functor!!.reinterpret<grpc_cb_tag>()
    val cont = tag.pointed.user_data!!.asStableRef<Continuation<Unit>>().get()
    deleteCbTag(tag)
    if (ok != 0) cont.resume(Unit) else cont.resumeWithException(IllegalStateException("batch failed"))
}

@CName("kq_shutdown_cb")
private fun shutdownCb(functor: CPointer<grpc_completion_queue_functor>?, ok: Int) {
    val tag = functor!!.reinterpret<grpc_cb_tag>()
    val cq = tag.pointed.user_data!!.asStableRef<CompletionQueue>().get()
    check(ok != 0) { "CQ shutdown failed" }
    grpc_completion_queue_destroy(cq.raw)
    cq._shutdownDone.complete(Unit)
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