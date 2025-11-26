/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class, ExperimentalStdlibApi::class)

package kotlinx.rpc.grpc.internal

import cnames.structs.grpc_call
import cnames.structs.grpc_completion_queue
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.cinterop.CFunction
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.convert
import kotlinx.cinterop.free
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.staticCFunction
import kotlinx.rpc.internal.utils.InternalRpcApi
import libkgrpc.GRPC_OP_RECV_STATUS_ON_CLIENT
import libkgrpc.grpc_call_error
import libkgrpc.grpc_call_start_batch
import libkgrpc.grpc_completion_queue_create_for_callback
import libkgrpc.grpc_completion_queue_destroy
import libkgrpc.grpc_completion_queue_functor
import libkgrpc.grpc_completion_queue_shutdown
import libkgrpc.grpc_op
import libkgrpc.kgrpc_cb_tag
import libkgrpc.kgrpc_iomgr_run_in_background
import platform.posix.memset
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner

/**
 * The result of a batch operation (see [CompletionQueue.runBatch]).
 */
@InternalRpcApi
public sealed interface BatchResult {
    /**
     * Happens when a batch was submitted and...
     * - the queue is closed
     * - the queue is in the process of a force shutdown
     * - the queue is in the process of a normal shutdown, and the batch is a new `RECV_STATUS_ON_CLIENT` batch.
     */
    @InternalRpcApi
    public object CQShutdown : BatchResult

    /**
     * Happens when the batch couldn't be submitted for some reason.
     */
    @InternalRpcApi
    public data class SubmitError(val error: grpc_call_error) : BatchResult

    /**
     * Happens when the batch was successfully submitted.
     * The [future] will be completed with `true` if the batch was successful, `false` otherwise.
     * In the case of `false`, the status of the `RECV_STATUS_ON_CLIENT` batch will provide the error details.
     */
    @InternalRpcApi
    public data class Submitted(val future: CallbackFuture<Boolean>) : BatchResult
}

/**
 * A thread-safe Kotlin wrapper for the native grpc_completion_queue.
 * It is based on the "new" callback API; therefore, there are no kotlin-side threads required to poll
 * the queue.
 * Users can attach to the returned [CallbackFuture] if the batch was successfully submitted (see [BatchResult]).
 */
@InternalRpcApi
public class CompletionQueue {

    internal enum class State { OPEN, SHUTTING_DOWN, CLOSED }

    // if the shutdown() was called with forceShutdown = true,
    // it will reject all new batches and wait for all current ones to finish.
    private val forceShutdown = atomic(false)

    // internal as it must be accessible from the SHUTDOWN_CB,
    // but it shouldn't be used from outside this file.
    @Suppress("PropertyName")
    internal val _state = atomic(State.OPEN)

    // internal as it must be accessible from the SHUTDOWN_CB,
    // but it shouldn't be used from outside this file.
    @Suppress("PropertyName")
    internal val _shutdownDone = CallbackFuture<Unit>()

    // used to synchronize the start of a new batch operation.
    private val batchStartGuard = SynchronizedObject()

    // a stable reference of this used as user_data in the shutdown callback.
    private val thisStableRef = StableRef.create(this)

    // the shutdown functor/tag called when the queue is shut down.
    private val shutdownFunctor = nativeHeap.alloc<kgrpc_cb_tag> {
        functor.functor_run = SHUTDOWN_CB
        user_data = thisStableRef.asCPointer()
    }.reinterpret<grpc_completion_queue_functor>()


    public val raw: CPointer<grpc_completion_queue>? =
        grpc_completion_queue_create_for_callback(shutdownFunctor.ptr, null)

    @Suppress("unused")
    private val thisStableRefCleaner = createCleaner(thisStableRef) { it.dispose() }

    @Suppress("unused")
    private val shutdownFunctorCleaner = createCleaner(shutdownFunctor) { nativeHeap.free(it) }

    init {
        // Assert grpc_iomgr_run_in_background() to guarantee that the event manager provides
        // IO threads and supports the callback API.
        require(kgrpc_iomgr_run_in_background()) { "The gRPC iomgr is not running background threads, required for callback based APIs." }
    }

    /**
     * Submits a batch operation to the queue.
     * See [BatchResult] for possible outcomes.
     */
    @OptIn(UnsafeNumber::class)
    public fun runBatch(call: CPointer<grpc_call>, ops: CPointer<grpc_op>, nOps: ULong): BatchResult {
        if (_shutdownDone.isCompleted) return BatchResult.CQShutdown

        val completion = CallbackFuture<Boolean>()
        val tag = newCbTag(completion, OPS_COMPLETE_CB)

        var err = grpc_call_error.GRPC_CALL_ERROR

        synchronized(batchStartGuard) {
            if (_state.value == State.SHUTTING_DOWN && ops.pointed.op == GRPC_OP_RECV_STATUS_ON_CLIENT) {
                // if the queue is in the process of a SHUTDOWN,
                // new call status receive batches will be rejected.
                deleteCbTag(tag)
                return BatchResult.CQShutdown
            }

            if (forceShutdown.value || _state.value == State.CLOSED) {
                // if the queue is either closed or in the process of a FORCE shutdown,
                // new batches will instantly fail.
                deleteCbTag(tag)
                return BatchResult.CQShutdown
            }

            err = grpc_call_start_batch(call, ops, nOps.convert(), tag, null)
        }

        if (err != grpc_call_error.GRPC_CALL_OK) {
            // if the call was not successful, the callback will not be invoked.
            deleteCbTag(tag)
            return BatchResult.SubmitError(err)
        }

        return BatchResult.Submitted(completion)
    }

    /**
     * Shuts down the queue.
     * The method returns immediately, but the queue will be shut down asynchronously.
     * The returned [CallbackFuture] will be completed with `Unit` when the queue is shut down.
     *
     * @param force if `true`, the queue will reject all new batches with [BatchResult.CQShutdown].
     *        Otherwise, the queue allows submitting new batches and shutdown only when there are no more
     *        ongoing batches.
     */
    public fun shutdown(force: Boolean = false): CallbackFuture<Unit> {
        if (force) {
            forceShutdown.value = true
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
    val tag = functor!!.reinterpret<kgrpc_cb_tag>()
    val cont = tag.pointed.user_data!!.asStableRef<CallbackFuture<Boolean>>().get()
    deleteCbTag(tag)
    cont.complete(ok != 0)
}

@CName("kq_shutdown_cb")
private fun shutdownCb(functor: CPointer<grpc_completion_queue_functor>?, ok: Int) {
    val tag = functor!!.reinterpret<kgrpc_cb_tag>()
    val cq = tag.pointed.user_data!!.asStableRef<CompletionQueue>().get()
    cq._state.value = CompletionQueue.State.CLOSED
    cq._shutdownDone.complete(Unit)
    grpc_completion_queue_destroy(cq.raw)
}

private val OPS_COMPLETE_CB = staticCFunction(::opsCompleteCb)
private val SHUTDOWN_CB = staticCFunction(::shutdownCb)

@OptIn(UnsafeNumber::class)
private fun newCbTag(
    userData: Any,
    cb: CPointer<CFunction<(CPointer<grpc_completion_queue_functor>?, Int) -> Unit>>,
): CPointer<kgrpc_cb_tag> {
    val tag = nativeHeap.alloc<kgrpc_cb_tag>()
    memset(tag.ptr, 0, sizeOf<kgrpc_cb_tag>().convert())
    tag.functor.functor_run = cb
    tag.user_data = StableRef.create(userData).asCPointer()
    return tag.ptr
}

@CName("kgrpc_cb_tag_destroy")
private fun deleteCbTag(tag: CPointer<kgrpc_cb_tag>) {
    tag.pointed.user_data!!.asStableRef<Any>().dispose()
    nativeHeap.free(tag)
}

/**
 * Represents a callback tag for completion queues constructed with
 * [grpc_completion_queue_create_for_callback].
 *
 * The [run] method will be invoked onces the completion queue signals the completion of the operation.
 * It is guaranteed that the [run] method will be only invoked once.
 *
 * `this` object is guaranteed to be not garbage collected until the [run] method was executed.
 */
@InternalRpcApi
public interface CallbackTag {
    public fun run(ok: Boolean)

    /**
     * Creates a pointer to a gRPC callback tag that encapsulates the given `run` function.
     * It can be passed to callback-based grpc_completion_queue.
     */
    public fun toCbTag(): CPointer<kgrpc_cb_tag> {
        return newCbTag(this, staticCFunction { functor, ok ->
            val tag = functor!!.reinterpret<kgrpc_cb_tag>()
            val callbackTag = tag.pointed.user_data!!.asStableRef<CallbackTag>().get()
            // free the tag memory and release the stable reference to `this`
            deleteCbTag(tag)
            callbackTag.run(ok != 0)
        })
    }

    public companion object {
        /**
         * Creates a pointer to a gRPC callback tag that encapsulates the given `run` function.
         *
         * The `run` function will be invoked when the callback is triggered with a boolean
         * value that indicates the success or failure of the operation.
         *
         * @return A pointer to the newly created gRPC callback tag.
         *         It can be passed to callback-based grpc_completion_queue.
         */
        public fun anonymous(run: (ok: Boolean) -> Unit): CPointer<kgrpc_cb_tag> {
            return object : CallbackTag {
                override fun run(ok: Boolean) {
                    run(ok)
                }
            }.toCbTag()
        }
    }
}

