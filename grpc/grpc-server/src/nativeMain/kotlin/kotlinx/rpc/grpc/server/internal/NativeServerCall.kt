/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class, InternalRpcApi::class,
    InternalNativeRpcApi::class)

package kotlinx.rpc.grpc.server.internal

import cnames.structs.grpc_call
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.cinterop.Arena
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.get
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.descriptor.GrpcMethodDescriptor
import kotlinx.rpc.grpc.descriptor.GrpcMethodType
import kotlinx.rpc.grpc.internal.BatchResult
import kotlinx.rpc.grpc.internal.CompletionQueue
import kotlinx.rpc.grpc.internal.ResourceGuard
import kotlinx.rpc.grpc.internal.destroyEntries
import kotlinx.rpc.grpc.internal.internalError
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.grpc.internal.toGrpcByteBuffer
import kotlinx.rpc.grpc.internal.toGrpcSlice
import kotlinx.rpc.grpc.internal.toKotlin
import kotlinx.rpc.grpc.internal.toRaw
import kotlinx.rpc.grpc.status
import kotlinx.rpc.grpc.statusCode
import kotlinx.rpc.grpc.internal.cinterop.GRPC_OP_RECV_CLOSE_ON_SERVER
import kotlinx.rpc.grpc.internal.cinterop.GRPC_OP_RECV_MESSAGE
import kotlinx.rpc.grpc.internal.cinterop.GRPC_OP_SEND_INITIAL_METADATA
import kotlinx.rpc.grpc.internal.cinterop.GRPC_OP_SEND_MESSAGE
import kotlinx.rpc.grpc.internal.cinterop.GRPC_OP_SEND_STATUS_FROM_SERVER
import kotlinx.rpc.grpc.internal.cinterop.grpc_byte_buffer
import kotlinx.rpc.grpc.internal.cinterop.grpc_byte_buffer_destroy
import kotlinx.rpc.grpc.internal.cinterop.grpc_call_cancel_with_status
import kotlinx.rpc.grpc.internal.cinterop.grpc_call_unref
import kotlinx.rpc.grpc.internal.cinterop.grpc_op
import kotlinx.rpc.grpc.internal.cinterop.grpc_slice_unref
import kotlinx.rpc.grpc.internal.cinterop.grpc_status_code
import kotlinx.rpc.grpc.internal.shim.InternalNativeRpcApi
import kotlin.concurrent.Volatile
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner

// Bit 30 of [NativeServerCall.state]; once set, no new reader claim is admitted and the next
// path that observes `state == CLOSE_REQUESTED` finalises the call. KRPC-604.
private const val CLOSE_REQUESTED = 1 shl 30

internal class NativeServerCall<Request, Response>(
    // ownership is transferred to the call
    val raw: CPointer<grpc_call>,
    val cq: CompletionQueue,
) : ServerCall<Request, Response>() {

    constructor(
        raw: CPointer<grpc_call>,
        cq: CompletionQueue,
        methodDescriptor: GrpcMethodDescriptor<Request, Response>,
    ) : this(raw, cq) {
        setMethodDescriptor(methodDescriptor)
    }

    // grpc_shutdown() requires all application-owned grpc objects to be destroyed before it runs
    // (grpc/grpc.h). Release the application's +1 grpc_call ref deterministically in
    // tryToCloseCall; the cleaner is the fallback for calls whose RECV_CLOSE_ON_SERVER callback
    // never fires (grpc-core guarantees delivery of all submitted callbacks before CQ destruction,
    // so this is defensive). KRPC-592.
    private val rawGuard = ResourceGuard()

    @Suppress("unused")
    private val rawCleaner = createCleaner(Pair(raw, rawGuard)) { (ptr, guard) ->
        if (guard.released.compareAndSet(expect = false, update = true)) {
            grpc_call_unref(ptr)
        }
    }

    private val listener = DeferredCallListener<Request>()
    private var methodDescriptor: GrpcMethodDescriptor<Request, Response>? = null
    private val callbackMutex = ReentrantLock()
    private var initialized = false
    private var cancelled = false
    // tracks whether GRPC_OP_SEND_STATUS_FROM_SERVER completed. This is purely a gate for
    // rejecting further application-issued batches (request/sendMessage/sendHeaders) after the
    // server has sent trailers — it is NOT the call-lifecycle "closed" gate. The latter is
    // the CLOSE_REQUESTED bit on [state], set by the [tryToCloseCall] CAS. KRPC-604.
    private var sentStatus = false
    // Analog of NativeClientCall.closeInfo. The client stores the full GrpcStatus+trailers it
    // needs to hand to onClose; the server only needs the `cancelled` flag to pick between
    // listener.onCancel() and listener.onComplete(), hence `Boolean?`. null = terminal signal
    // not yet observed (either RECV_CLOSE_ON_SERVER completing OR initialize() failing to submit
    // that batch).
    private val closeInfo = atomic<Boolean?>(null)

    // Lock-free state machine for the call's lifetime claims. Replaces the read-then-CAS pair
    // formed by the prior `inFlight: AtomicInt` + `callClosed: AtomicBoolean` whose SC schedule
    // admitted a UAF window between runBatch and tryToCloseCall (mirror of the client-side
    // crash; same fix applied for symmetry). KRPC-604.
    //
    // Encoding (32-bit signed):
    //   bit 30 (CLOSE_REQUESTED) — once set, no new reader claim is admitted.
    //   bits 0..29              — count of reader claims currently outstanding.
    //
    // A reader claim succeeds via CAS(N -> N+1) iff the bit is clear; the close path claims via
    // CAS(N -> N | bit). [finishClose] runs at most once: by whichever of (a) the close path's
    // CAS observing count == 0, or (b) the last reader's [endOp] post-decrement observing the
    // word at exactly CLOSE_REQUESTED. Both paths gate the body via the rawGuard CAS.
    private val state = atomic(0)

    // tracks whether the initial metadata has been sent.
    // this is used to determine if we have to send the initial metadata
    // when we try to close the call.
    private var sentInitialMetadata = false

    // tracks whether at least one request message has been received on this call.
    private var receivedFirstMessage = false

    // we currently don't buffer messages, so after one `sendMessage` call, ready turns false. (KRPC-192)
    private val ready = atomic(true)

    init {
        initialize()
    }

    /**
     * Sets the method descriptor for this call.
     * It must be set before invoking [ServerCallHandler.startCall].
     */
    fun setMethodDescriptor(methodDescriptor: GrpcMethodDescriptor<Request, Response>) {
        this.methodDescriptor = methodDescriptor
    }

    /**
     * Set the listener created from [ServerCallHandler.startCall].
     * This must be set directly after receiving the listener from the `startCall` invocation.
     */
    fun setListener(listener: Listener<Request>) {
        this.listener.setDelegate(listener)
    }

    private fun initialize() {
        // finishes if the whole connection is closed.
        // this triggers onClose()/onCanceled() callback.
        val arena = Arena()
        val cancelled = arena.alloc<IntVar>()
        val op = arena.alloc<grpc_op> {
            op = GRPC_OP_RECV_CLOSE_ON_SERVER
            data.recv_close_on_server.cancelled = cancelled.ptr
        }
        // beginOp BEFORE submitting so the unref in tryToCloseCall can't race the completion
        // callback — same invariant runBatch enforces for later batches. The claim is
        // guaranteed at construction time (state == 0, bit clear).
        check(beginOp()) { internalError("beginOp refused at construction") }
        val result = cq.runBatch(raw, op.ptr, 1u)
        if (result !is BatchResult.Submitted) {
            // we couldn't submit the initialization batch, so nothing can be done.
            arena.clear()
            try {
                finalize(true)
            } finally {
                endOp()
            }
        } else {
            initialized = true
            result.future.onComplete {
                val cancelled = cancelled.value == 1
                arena.clear()
                try {
                    finalize(cancelled)
                } finally {
                    endOp()
                }
            }
        }
    }

    /**
     * Called when RECV_CLOSE_ON_SERVER completes (or fails to submit) — records the terminal state
     * for the call and lets [tryToCloseCall] dispatch the listener callback and release `raw` once
     * any in-flight batches have drained.
     */
    private fun finalize(cancelled: Boolean) {
        if (closeInfo.compareAndSet(null, cancelled)) {
            tryToCloseCall()
        }
    }

    /**
     * Attempts a reader claim on [raw]. Returns `true` if the claim succeeded — caller must
     * pair it with exactly one [endOp]. Returns `false` if the call has already requested
     * close; caller skips the protected operation.
     */
    private fun beginOp(): Boolean {
        while (true) {
            val cur = state.value
            if (cur and CLOSE_REQUESTED != 0) return false
            if (state.compareAndSet(cur, cur + 1)) return true
        }
    }

    /**
     * Releases a reader claim. If this drops the count to 0 (and close has not yet been
     * requested), invokes [tryToCloseCall] to drive close when [closeInfo] is set; if the
     * decrement transitions the word to exactly CLOSE_REQUESTED, we are the last reader after
     * the close-path winner set the bit, so finalise directly.
     */
    private fun endOp() {
        val v = state.decrementAndGet()
        if (v == 0) {
            tryToCloseCall()
        } else if (v == CLOSE_REQUESTED) {
            // closeInfo was non-null when the close-path CAS set the bit and is monotonic, so
            // it is guaranteed non-null here.
            closeInfo.value?.let { finishClose(it) }
        }
    }

    /**
     * Drives the close transition. Single-CAS reader/destroyer linearisation closes the
     * UAF window that the prior `inFlight + callClosed` two-atomic pattern admitted. KRPC-604.
     *
     * - If [closeInfo] is not yet set (RECV_CLOSE_ON_SERVER still in progress), this does
     *   nothing.
     * - Otherwise CAS-set the close-requested bit. If the count component was 0 at the CAS we
     *   are also the finaliser and run [finishClose] immediately. Otherwise the last reader's
     *   [endOp] runs [finishClose] when the word transitions to exactly CLOSE_REQUESTED.
     */
    private fun tryToCloseCall() {
        val wasCancelled = closeInfo.value ?: return
        while (true) {
            val cur = state.value
            if (cur and CLOSE_REQUESTED != 0) return
            if (state.compareAndSet(cur, cur or CLOSE_REQUESTED)) {
                if (cur == 0) finishClose(wasCancelled)
                return
            }
        }
    }

    /**
     * Runs the terminal listener callback and releases `raw`. Idempotent via the rawGuard CAS:
     * the close-path winner that observed `cur == 0` and the last reader's [endOp] post-
     * decrement can both reach this — only the first wins the CAS and runs the body.
     *
     * Listener dispatch happens under [callbackMutex] so it cannot interleave with a still-
     * draining onMessage/onHalfClose/onReady callback. No safeUserCode wrapper: the server is
     * already at terminal state and has no cancel path to recurse into. A thrown listener
     * exception escapes on the CQ thread; the try/finally still runs grpc_call_unref so we do
     * not leak the call ref.
     */
    private fun finishClose(wasCancelled: Boolean) {
        if (!rawGuard.released.compareAndSet(expect = false, update = true)) return
        try {
            if (wasCancelled) {
                this.cancelled = true
                callbackMutex.withLock {
                    listener.onCancel()
                }
            } else {
                callbackMutex.withLock {
                    listener.onComplete()
                }
            }
        } finally {
            grpc_call_unref(raw)
        }
    }

    fun cancel(status: grpc_status_code, message: String) {
        cancelled = true
        // Hold a reader claim while dereferencing `raw` so tryToCloseCall cannot fire
        // grpc_call_unref concurrently. If the claim is refused the call has already finalised
        // — cancel is a no-op. Mirrors NativeClientCall.cancelInternal.
        if (!beginOp()) return
        try {
            grpc_call_cancel_with_status(raw, status, message, null)
        } finally {
            endOp()
        }
    }

    /**
     * Sets the [ready] flag to true and calls the listener's onReady callback.
     * This is called as soon as the RECV_MESSAGE batch is finished (or failed).
     */
    private fun turnReady() {
        callbackMutex.withLock {
            if (ready.compareAndSet(expect = false, update = true)) {
                listener.onReady()
            }
        }
    }

    override fun isReady(): Boolean {
        return ready.value
    }

    private fun runBatch(
        ops: CPointer<grpc_op>,
        nOps: ULong,
        cleanup: () -> Unit = {},
        onSuccess: () -> Unit = {},
    ) {
        // Application-level gates: once we have sent the trailers or local cancellation has
        // been observed, no further application batches are allowed.
        if (sentStatus || cancelled) return cleanup()

        // Single-CAS reader claim atomically (a) checks the call has not requested close and
        // (b) increments the in-flight count. No read-then-CAS pair → no SC-reorder window in
        // which `cq.runBatch` could touch a freed call. KRPC-604.
        if (!beginOp()) return cleanup()

        when (val result = cq.runBatch(raw, ops, nOps)) {
            is BatchResult.Submitted -> {
                result.future.onComplete {
                    try {
                        onSuccess()
                    } catch (e: Throwable) {
                        cancel(grpc_status_code.GRPC_STATUS_INTERNAL, e.message ?: "Unknown error")
                    } finally {
                        cleanup()
                        endOp()
                    }
                }
            }

            BatchResult.CQShutdown -> {
                cleanup()
                // cancel() does its own beginOp/endOp around grpc_call_cancel_with_status — our
                // outer beginOp still guards the unref across that call.
                cancel(grpc_status_code.GRPC_STATUS_UNAVAILABLE, "Server shutdown")
                endOp()
            }

            is BatchResult.SubmitError -> {
                cleanup()
                cancel(
                    grpc_status_code.GRPC_STATUS_INTERNAL,
                    "Batch could not be submitted: ${result.error}"
                )
                endOp()
            }
        }
    }

    override fun request(numMessages: Int) {
        check(initialized) { internalError("Call not initialized") }
        // TODO: Remove the num constraint (KRPC-213)
        require(numMessages == 1) { internalError("numMessages must be 1") }
        val methodDescriptor = checkNotNull(methodDescriptor) { internalError("Method descriptor not set") }

        val arena = Arena()
        val recvPtr = arena.alloc<CPointerVar<grpc_byte_buffer>>()
        val op = arena.alloc<grpc_op> {
            op = GRPC_OP_RECV_MESSAGE
            data.recv_message.recv_message = recvPtr.ptr
        }

        runBatch(op.ptr, 1u, cleanup = {
            arena.clear()
        }) {
            // if the call was successful, but no message was received, we reached the end-of-stream.
            // and thus the client half-closed.
            val buf = recvPtr.value
            if (buf == null) {
                // end-of-stream observed. for UNARY, absence of any request is a protocol violation.
                if (methodDescriptor.methodType == GrpcMethodType.UNARY && !receivedFirstMessage) {
                    cancel(
                        grpc_status_code.GRPC_STATUS_INTERNAL,
                        "Unary call half-closed before receiving a request message"
                    )
                } else {
                    callbackMutex.withLock {
                        listener.onHalfClose()
                    }
                }
            } else {
                try {
                    val msg = methodDescriptor.requestMarshaller
                        .decode(buf.toKotlin())
                    // Mark that we have received at least one request message
                    receivedFirstMessage = true
                    callbackMutex.withLock {
                        listener.onMessage(msg)
                    }
                } finally {
                    grpc_byte_buffer_destroy(buf)
                }
            }
        }
    }

    @OptIn(UnsafeNumber::class)
    override fun sendHeaders(headers: GrpcMetadata) {
        check(initialized) { internalError("Call not initialized") }
        val arena = Arena()

        val initialMetadata = with(headers) {
            arena.allocRawGrpcMetadata()
        }

        val op = arena.alloc<grpc_op> {
            op = GRPC_OP_SEND_INITIAL_METADATA
            data.send_initial_metadata.count = initialMetadata.count
            data.send_initial_metadata.metadata = initialMetadata.metadata
        }

        sentInitialMetadata = true
        runBatch(op.ptr, 1u, cleanup = {
            initialMetadata.destroyEntries()
            arena.clear()
        }) {
            // nothing to do here
        }
    }

    override fun sendMessage(message: Response) {
        check(initialized) { internalError("Call not initialized") }
        check(isReady()) { internalError("Not yet ready.") }
        val methodDescriptor = checkNotNull(methodDescriptor) { internalError("Method descriptor not set") }

        val arena = Arena()
        tryRun {
            val source = methodDescriptor.responseMarshaller.encode(message)
            val byteBuffer = source.toGrpcByteBuffer()
            ready.value = false

            val op = arena.alloc<grpc_op> {
                op = GRPC_OP_SEND_MESSAGE
                data.send_message.send_message = byteBuffer
            }

            runBatch(op.ptr, 1u, cleanup = {
                arena.clear()
                grpc_byte_buffer_destroy(byteBuffer)
            }) {
                turnReady()
            }
        }
    }

    @OptIn(UnsafeNumber::class)
    override fun close(status: GrpcStatus, trailers: GrpcMetadata) {
        check(initialized) { internalError("Call not initialized") }

        val arena = Arena()
        val trailingMetadata = with(trailers) {
            arena.allocRawGrpcMetadata()
        }

        val details = status.getDescription()?.toGrpcSlice()
        val detailsPtr = details?.getPointer(arena)

        val nOps = if (sentInitialMetadata) 1uL else 2uL

        val ops = arena.allocArray<grpc_op>(nOps.convert())

        ops[0].op = GRPC_OP_SEND_STATUS_FROM_SERVER
        ops[0].data.send_status_from_server.status = status.statusCode.toRaw()
        ops[0].data.send_status_from_server.status_details = detailsPtr
        ops[0].data.send_status_from_server.trailing_metadata_count = trailingMetadata.count
        ops[0].data.send_status_from_server.trailing_metadata = trailingMetadata.metadata

        if (!sentInitialMetadata) {
            // if we haven't sent GRPC_OP_SEND_INITIAL_METADATA yet,
            // so we must do it together with the close operation.
            ops[1].op = GRPC_OP_SEND_INITIAL_METADATA
            ops[1].data.send_initial_metadata.count = 0u
            ops[1].data.send_initial_metadata.metadata = null
        }

        runBatch(ops, nOps, cleanup = {
            if (details != null) grpc_slice_unref(details)
            trailingMetadata.destroyEntries()
            arena.clear()
        }) {
            sentStatus = true
            // nothing to do here
        }
    }

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun getMethodDescriptor(): GrpcMethodDescriptor<Request, Response> {
        val methodDescriptor = checkNotNull(methodDescriptor) { internalError("Method descriptor not set") }
        return methodDescriptor
    }


    private inline fun <T> tryRun(crossinline block: () -> T): T {
        try {
            return block()
        } catch (e: Throwable) {
            // TODO KRPC-551: Log internal error as warning
            val status = when (e) {
                is GrpcStatusException -> e.status
                else -> GrpcStatus(
                    GrpcStatusCode.INTERNAL,
                    description = "Internal error, so canceling the stream",
                    cause = e
                )
            }
            cancel(status.statusCode.toRaw(), status.getDescription() ?: "Unknown error")
            throw GrpcStatusException(status, trailers = null)
        }
    }
}

/**
 * A listener implementation that defers execution of its methods until a delegate is set.
 *
 * This class extends `ServerCall.Listener`, allowing it to serve as a listener for server calls.
 * Initially, incoming method calls (e.g., `onMessage`, `onHalfClose`, etc.) are queued until a delegate
 * is assigned through the `setDelegate` method. Once the delegate is set, queued methods are delivered
 * in order and all future method calls are forwarded directly to the delegate.
 */
private class DeferredCallListener<T> : ServerCall.Listener<T>() {
    @Volatile
    private var delegate: ServerCall.Listener<T>? = null
    private val mutex = ReentrantLock()
    private val queue = ArrayDeque<(ServerCall.Listener<T>) -> Unit>()

    fun setDelegate(d: ServerCall.Listener<T>) {
        mutex.withLock {
            if (delegate != null) return
            // Drain BEFORE publishing `delegate`, and keep both inside the same mutex.
            // A concurrent deliver() fast path reads `delegate` lock-free; if it sees non-null,
            // it must also be able to assume the queue is already drained, otherwise its
            // invocation on `d` can run on a different thread concurrently with this drain loop.
            // Inverting the order + holding the mutex across the drain closes that window:
            // readers either see null and fall to the mutex-guarded slow path, or see `d` and
            // are guaranteed the drain has completed. KRPC-599.
            queue.forEach { it(d) }
            queue.clear()
            delegate = d
        }
    }

    private inline fun deliver(crossinline invokeListener: (ServerCall.Listener<T>) -> Unit) {
        val currentDelegate = delegate
        if (currentDelegate != null) {
            // fast path (delegate is already set)
            invokeListener(currentDelegate); return
        }
        // slow path: re-check under lock
        val safeCurrentDelegate = mutex.withLock {
            val cur = delegate
            if (cur == null) {
                queue.addLast { invokeListener(it) }
                null
            } else {
                cur
            }
        }
        // if the delegate was already set, call it
        if (safeCurrentDelegate != null) {
            invokeListener(safeCurrentDelegate)
        }
    }

    override fun onMessage(message: T) = deliver { it.onMessage(message) }
    override fun onHalfClose() = deliver { it.onHalfClose() }
    override fun onCancel() = deliver { it.onCancel() }
    override fun onComplete() = deliver { it.onComplete() }
    override fun onReady() = deliver { it.onReady() }
}
