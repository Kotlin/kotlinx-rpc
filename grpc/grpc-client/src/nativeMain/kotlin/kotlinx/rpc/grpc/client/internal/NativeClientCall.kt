/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class, InternalRpcApi::class,
    InternalNativeRpcApi::class)

package kotlinx.rpc.grpc.client.internal

import cnames.structs.grpc_call
import kotlinx.atomicfu.atomic
import kotlinx.cinterop.Arena
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.get
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readValue
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableJob
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.descriptor.GrpcMethodDescriptor
import kotlinx.rpc.grpc.internal.BatchResult
import kotlinx.rpc.grpc.internal.CompletionQueue
import kotlinx.rpc.grpc.internal.ResourceGuard
import kotlinx.rpc.grpc.internal.destroyEntries
import kotlinx.rpc.grpc.internal.internalError
import kotlinx.rpc.grpc.internal.toByteArray
import kotlinx.rpc.grpc.internal.toGrpcByteBuffer
import kotlinx.rpc.grpc.internal.toKotlin
import kotlinx.rpc.grpc.GrpcCompression
import kotlinx.rpc.grpc.client.GrpcEmptyCallCredentials
import kotlinx.rpc.grpc.client.GrpcCallOptions
import kotlinx.rpc.grpc.client.createRaw
import kotlinx.rpc.grpc.internal.cinterop.GRPC_OP_RECV_INITIAL_METADATA
import kotlinx.rpc.grpc.internal.cinterop.GRPC_OP_RECV_MESSAGE
import kotlinx.rpc.grpc.internal.cinterop.GRPC_OP_RECV_STATUS_ON_CLIENT
import kotlinx.rpc.grpc.internal.cinterop.GRPC_OP_SEND_CLOSE_FROM_CLIENT
import kotlinx.rpc.grpc.internal.cinterop.GRPC_OP_SEND_INITIAL_METADATA
import kotlinx.rpc.grpc.internal.cinterop.GRPC_OP_SEND_MESSAGE
import kotlinx.rpc.grpc.internal.cinterop.gpr_free
import kotlinx.rpc.grpc.internal.cinterop.grpc_byte_buffer
import kotlinx.rpc.grpc.internal.cinterop.grpc_byte_buffer_destroy
import kotlinx.rpc.grpc.internal.cinterop.grpc_call_cancel_with_status
import kotlinx.rpc.grpc.internal.cinterop.grpc_call_credentials_release
import kotlinx.rpc.grpc.internal.cinterop.grpc_call_error
import kotlinx.rpc.grpc.internal.cinterop.grpc_call_set_credentials
import kotlinx.rpc.grpc.internal.cinterop.grpc_call_unref
import kotlinx.rpc.grpc.internal.cinterop.grpc_metadata_array
import kotlinx.rpc.grpc.internal.cinterop.grpc_metadata_array_destroy
import kotlinx.rpc.grpc.internal.cinterop.grpc_metadata_array_init
import kotlinx.rpc.grpc.internal.cinterop.grpc_op
import kotlinx.rpc.grpc.internal.cinterop.grpc_slice
import kotlinx.rpc.grpc.internal.cinterop.grpc_slice_unref
import kotlinx.rpc.grpc.internal.cinterop.grpc_status_code
import kotlinx.rpc.grpc.internal.shim.InternalNativeRpcApi
import kotlin.coroutines.CoroutineContext
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner

// Bit 30 of [NativeClientCall.state]; once set, no new reader claim is admitted and the next
// path that observes `state == CLOSE_REQUESTED` finalises the call. KRPC-604.
private const val CLOSE_REQUESTED = 1 shl 30

internal class NativeClientCall<Request, Response>(
    private val cq: CompletionQueue,
    internal val raw: CPointer<grpc_call>,
    private val methodDescriptor: GrpcMethodDescriptor<Request, Response>,
    private val callOptions: GrpcCallOptions,
    private val callJob: CompletableJob,
    private val coroutineContext: CoroutineContext,
) : ClientCall<Request, Response>() {

    // grpc_shutdown() requires all application-owned grpc objects to be destroyed before it runs
    // (grpc/grpc.h). Release the application's +1 grpc_call ref deterministically in
    // tryToCloseCall; the cleaner is the fallback for cases where onClose never fires. KRPC-586.
    private val rawGuard = ResourceGuard()

    @Suppress("unused")
    private val rawCleaner = createCleaner(Pair(raw, rawGuard)) { (ptr, guard) ->
        if (guard.released.compareAndSet(expect = false, update = true)) {
            grpc_call_unref(ptr)
        }
    }

    private val rawCallCredentials = callOptions.callCredentials.let {
        if (it is GrpcEmptyCallCredentials) null else it.createRaw(coroutineContext)
    }

    // Mirrors rawGuard for the application-owned +1 ref on grpc_call_credentials. Null when no
    // credentials are attached — avoids registering a cleaner in the common no-credentials path.
    // Deterministic release happens alongside grpc_call_unref; the cleaner is the guarded
    // fallback. KRPC-588.
    private val rawCallCredentialsGuard: ResourceGuard? =
        if (rawCallCredentials != null) ResourceGuard() else null

    @Suppress("unused")
    private val rawCallCredentialsCleaner = rawCallCredentials?.let { ptr ->
        val guard = rawCallCredentialsGuard!!
        createCleaner(Pair(ptr, guard)) { (p, g) ->
            if (g.released.compareAndSet(expect = false, update = true)) {
                grpc_call_credentials_release(p)
            }
        }
    }

    /** Idempotent deterministic release of the app-owned +1 on [rawCallCredentials]. KRPC-588. */
    private fun releaseCallCredentialsIfNeeded() {
        val ptr = rawCallCredentials ?: return
        val guard = rawCallCredentialsGuard ?: return
        if (guard.released.compareAndSet(expect = false, update = true)) {
            grpc_call_credentials_release(ptr)
        }
    }

    init {
        // cancel the call if the job is canceled.
        callJob.invokeOnCompletion {
            when (it) {
                is CancellationException -> {
                    cancelInternal(grpc_status_code.GRPC_STATUS_UNAVAILABLE, "Channel shutdownNow invoked")
                }

                is Throwable -> {
                    cancelInternal(grpc_status_code.GRPC_STATUS_INTERNAL, "Call failed: ${it.message}")
                }
            }
            // Fallback deterministic release for calls that never reached tryToCloseCall — e.g., a
            // client interceptor threw before start() submitted any batch, so no
            // RECV_STATUS_ON_CLIENT ever completes and closeInfo stays null. Without this, the
            // grpc_call is owned past grpc_shutdown(). rawGuard blocks double-unref. KRPC-586.
            if (rawGuard.released.compareAndSet(expect = false, update = true)) {
                grpc_call_unref(raw)
            }
            releaseCallCredentialsIfNeeded()
        }
    }

    private var listener: Listener<Response>? = null
    private var halfClosed = false
    private var cancelled = false

    // Lock-free state machine for the call's lifetime claims. Replaces the read-then-CAS pair
    // formed by the prior `inFlight: AtomicInt` + `closed: AtomicBoolean` whose SC schedule
    // admitted a UAF window: the destroyer reading `inFlight==0` could race a runBatch that
    // had read `closed==false` post-beginOp and then proceed into `grpc_call_start_batch` on a
    // freed call (manifested as `__cxa_deleted_virtual` inside ExecCtx::Flush). KRPC-604.
    //
    // Encoding (32-bit signed):
    //   bit 30 (CLOSE_REQUESTED) — once set, no new reader claim is admitted.
    //   bits 0..29              — count of reader claims currently outstanding.
    //
    // A reader claim succeeds via CAS(N -> N+1) iff the bit is clear; the close path claims via
    // CAS(N -> N | bit). [finishClose] is invoked exactly once: by whichever of (a) the close
    // path's CAS observing count == 0, or (b) the last reader's [endOp] post-decrement
    // observing the word at exactly CLOSE_REQUESTED. Both paths gate the body via the rawGuard
    // CAS for idempotency. There is no read-then-CAS pair on a `closed`/`inFlight` pair, so no
    // SC-reorder window in which a reader could enter `cq.runBatch` after the close path has
    // scheduled `grpc_call_unref`.
    private val state = atomic(0)

    // holds the received status information returned by the RECV_STATUS_ON_CLIENT batch.
    // if null, the call is still in progress. otherwise, the call can be closed as soon as the
    // last reader claim drains.
    private val closeInfo = atomic<Pair<GrpcStatus, GrpcMetadata>?>(null)

    // we currently don't buffer messages, so after one `sendMessage` call, ready turns false. (KRPC-192)
    private val ready = atomic(true)

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
            // The close-path winner set the bit while we held the claim; closeInfo was non-null
            // when the bit was set and is monotonic, so this is guaranteed non-null here.
            closeInfo.value?.let { finishClose(it) }
        }
    }

    /**
     * Drives the close transition. Single-CAS reader/destroyer linearisation closes the
     * UAF window that the prior `inFlight + closed` two-atomic pattern admitted. KRPC-604.
     *
     * - If [closeInfo] is not yet set (RECV_STATUS_ON_CLIENT still in progress and no cancel
     *   has fired), this does nothing.
     * - Otherwise CAS-set the close-requested bit. If the word's count component was 0 at the
     *   CAS, we are also the finaliser and run [finishClose] immediately. If the count was
     *   non-zero, the last reader's [endOp] runs [finishClose] when it observes the word
     *   transition to exactly CLOSE_REQUESTED.
     */
    private fun tryToCloseCall() {
        val info = closeInfo.value ?: return
        while (true) {
            val cur = state.value
            // Already requested. The reader path drains independently; nothing more here.
            if (cur and CLOSE_REQUESTED != 0) return
            if (state.compareAndSet(cur, cur or CLOSE_REQUESTED)) {
                if (cur == 0) finishClose(info)
                return
            }
        }
    }

    /**
     * Runs the terminal listener callback and releases `raw`. Idempotent via the rawGuard CAS:
     * the close-path winner that observed `cur == 0` and the last reader's [endOp] post-
     * decrement can both reach this — only the first wins the CAS and runs the body. The
     * cleaner's fallback unref path uses the same CAS, so a successful finishClose guarantees
     * the cleaner is a no-op. KRPC-604.
     */
    private fun finishClose(info: Pair<GrpcStatus, GrpcMetadata>) {
        if (!rawGuard.released.compareAndSet(expect = false, update = true)) return
        // allows the managed channel to join for the call to finish.
        callJob.complete()
        // Listener may be null if the call failed before start() (e.g., an interceptor throws
        // mid-chain and the call is later cancelled via shutdownNow → markClosePending). No
        // user observer to notify in that case; resources still need to be released below.
        listener?.let { lst ->
            safeUserCode("Failed to call onClose.") {
                lst.onClose(info.first, info.second)
            }
        }
        // Deterministic grpc_call_unref. The cleaner remains as the GC fallback; the rawGuard
        // CAS we just won prevents a double-unref.
        grpc_call_unref(raw)
        // Safe to release call credentials here: the call's internal ref on them was dropped
        // inside grpc-core when RECV_STATUS_ON_CLIENT completed. KRPC-588.
        releaseCallCredentialsIfNeeded()
    }

    /**
     * Sets the [closeInfo] and calls [tryToCloseCall].
     * This is called as soon as the RECV_STATUS_ON_CLIENT batch (started with [startRecvStatus]) finished.
     */
    private fun markClosePending(status: GrpcStatus, trailers: GrpcMetadata) {
        closeInfo.compareAndSet(null, Pair(status, trailers))
        tryToCloseCall()
    }

    /**
     * Sets the [ready] flag to true and calls the listener's onReady callback.
     * This is called as soon as the RECV_MESSAGE batch is finished (or failed).
     */
    private fun turnReady() {
        if (ready.compareAndSet(expect = false, update = true)) {
            safeUserCode("Failed to call onReady.") {
                listener?.onReady()
            }
        }
    }


    override fun start(
        responseListener: Listener<Response>,
        headers: GrpcMetadata,
    ) {
        check(listener == null) { internalError("Already started") }

        listener = responseListener

        // attach call credentials to the call.
        if (rawCallCredentials != null) {
            grpc_call_set_credentials(raw, rawCallCredentials)
        }

        // start receiving the status from the completion queue,
        // which is bound to the lifetime of the call.
        val success = startRecvStatus()
        if (!success) return

        sendAndReceiveInitialMetadata(headers)
    }

    /**
     * Submits a batch operation to the [CompletionQueue] and handle
     * the returned [kotlinx.rpc.grpc.internal.BatchResult].
     * If the batch was successfully submitted, [onSuccess] is called.
     * In any case, [cleanup] is called.
     */
    private fun runBatch(
        ops: CPointer<grpc_op>,
        nOps: ULong,
        cleanup: () -> Unit = {},
        onSuccess: () -> Unit = {},
    ) {
        // Single-CAS reader claim atomically (a) checks the call has not requested close and
        // (b) increments the in-flight count. No read-then-CAS pair → no SC-reorder window in
        // which `cq.runBatch` could touch a freed call. KRPC-604.
        if (!beginOp()) return cleanup()

        when (val callResult = cq.runBatch(this@NativeClientCall.raw, ops, nOps)) {
            is BatchResult.Submitted -> {
                callResult.future.onComplete { success ->
                    try {
                        if (success) {
                            // if the batch doesn't succeed, this is reflected in the recv status op batch.
                            onSuccess()
                        }
                    } finally {
                        // ignore failure, as it is reflected in the client status op
                        cleanup()
                        endOp()
                    }
                }
            }

            BatchResult.CQShutdown -> {
                cleanup()
                // Hold our reader claim while cancelInternal runs so it cannot race with a
                // concurrent tryToCloseCall unref; release only after cancelInternal returns.
                cancelInternal(grpc_status_code.GRPC_STATUS_UNAVAILABLE, "Channel shutdown")
                endOp()
            }

            is BatchResult.SubmitError -> {
                cleanup()
                cancelInternal(
                    grpc_status_code.GRPC_STATUS_INTERNAL,
                    "Batch could not be submitted: ${callResult.error}"
                )
                endOp()
            }
        }
    }

    /**
     * Starts a batch operation to receive the status from the completion queue (RECV_STATUS_ON_CLIENT).
     * This operation is bound to the lifetime of the call, so it will finish once all other operations are done.
     * If this operation fails, it will call [markClosePending] with the corresponding error, as the entire call
     * si considered failed.
     *
     * @return true if the batch was successfully submitted, false otherwise.
     * In this case, the call is considered failed.
     */
    @OptIn(ExperimentalStdlibApi::class)
    private fun startRecvStatus(): Boolean {
        checkNotNull(listener) { internalError("Not yet started") }
        val arena = Arena()
        val statusCode = arena.alloc<grpc_status_code.Var>()
        val statusDetails = arena.alloc<grpc_slice>()
        val errorStr = arena.alloc<CPointerVar<ByteVar>>()

        val trailingMetadata = arena.alloc<grpc_metadata_array>()
        grpc_metadata_array_init(trailingMetadata.ptr)

        val op = arena.alloc<grpc_op> {
            op = GRPC_OP_RECV_STATUS_ON_CLIENT
            data.recv_status_on_client.status = statusCode.ptr
            data.recv_status_on_client.status_details = statusDetails.ptr
            data.recv_status_on_client.error_string = errorStr.ptr
            data.recv_status_on_client.trailing_metadata = trailingMetadata.ptr
        }

        when (val callResult = cq.runBatch(this@NativeClientCall.raw, op.ptr, 1u)) {
            is BatchResult.Submitted -> {
                callResult.future.onComplete {
                    val details = statusDetails.toByteArray().toKString()
                    val kStatusCode = statusCode.value.toKotlin()
                    val status = GrpcStatus(kStatusCode, details, null)
                    val trailers = GrpcMetadata(trailingMetadata)

                    // cleanup
                    grpc_slice_unref(statusDetails.readValue())
                    if (errorStr.value != null) gpr_free(errorStr.value)
                    // the entries are owned by the call object, so we must only destroy the array
                    grpc_metadata_array_destroy(trailingMetadata.readValue())
                    arena.clear()

                    // set close info and try to close the call.
                    markClosePending(status, trailers)
                }
                return true
            }

            BatchResult.CQShutdown -> {
                arena.clear()
                markClosePending(GrpcStatus(GrpcStatusCode.UNAVAILABLE, "Channel shutdown"), GrpcMetadata())
                return false
            }

            is BatchResult.SubmitError -> {
                arena.clear()
                markClosePending(
                    GrpcStatus(GrpcStatusCode.INTERNAL, "Failed to start call: ${callResult.error}"),
                    GrpcMetadata()
                )
                return false
            }
        }
    }

    @OptIn(UnsafeNumber::class)
    private fun sendAndReceiveInitialMetadata(headers: GrpcMetadata) {
        // sending and receiving initial metadata
        val arena = Arena()
        val opsNum = 2uL
        val ops = arena.allocArray<grpc_op>(opsNum.convert())

        // add compression algorithm to the call metadata.
        // the gRPC core will read the header and perform the compression (compression_filter.cc).
        if (callOptions.compression !is GrpcCompression.None) {
            if (callOptions.compression !is GrpcCompression.Gzip) {
                // to match the behavior of grpc-java, we cancel the call if the compression algorithm
                // is not supported. Return early to avoid submitting a batch on the cancelled call,
                // which would leave an orphaned CQ operation that delays shutdown.
                arena.clear()
                cancelInternal(
                    grpc_status_code.GRPC_STATUS_INTERNAL,
                    "Unable to find compressor by name ${callOptions.compression.name}"
                )
                return
            }
            headers.append("grpc-internal-encoding-request", callOptions.compression.name)
        }

        // turn given headers into a grpc_metadata_array.
        val sendInitialMetadata: grpc_metadata_array = with(headers) {
            arena.allocRawGrpcMetadata()
        }

        // send initial meta data to server
        ops[0].op = GRPC_OP_SEND_INITIAL_METADATA
        ops[0].data.send_initial_metadata.count = sendInitialMetadata.count
        ops[0].data.send_initial_metadata.metadata = sendInitialMetadata.metadata

        val recvInitialMetadata = arena.alloc<grpc_metadata_array>()
        grpc_metadata_array_init(recvInitialMetadata.ptr)
        ops[1].op = GRPC_OP_RECV_INITIAL_METADATA
        ops[1].data.recv_initial_metadata.recv_initial_metadata = recvInitialMetadata.ptr

        runBatch(ops, opsNum, cleanup = {
            // we must not destroy the array itself, as it is cleared when clearing the arena.
            sendInitialMetadata.destroyEntries()
            // the entries are owned by the call object, so we must only destroy the array
            grpc_metadata_array_destroy(recvInitialMetadata.readValue())
            arena.clear()
        }) {
            val headers = GrpcMetadata(recvInitialMetadata)
            safeUserCode("Failed to call onHeaders.") {
                listener?.onHeaders(headers)
            }
        }
    }

    /**
     * Requests [numMessages] messages from the server.
     * This must only be called again after [numMessages] were received in the [Listener.onMessage] callback.
     */
    override fun request(numMessages: Int) {
        check(numMessages > 0) { internalError("numMessages must be > 0") }
        // limit numMessages to prevent potential stack overflows
        check(numMessages <= 16) { internalError("numMessages must be <= 16") }
        val listener = checkNotNull(listener) { internalError("Not yet started") }
        if (cancelled) {
            // no need to send message if the call got already cancelled.
            return
        }

        var remainingMessages = numMessages

        // we need to request only one message at a time, so we use a recursive function that
        // requests one message and then calls itself again.
        fun post() {
            if (remainingMessages-- <= 0) return

            val arena = Arena()
            val recvPtr = arena.alloc<CPointerVar<grpc_byte_buffer>>()
            val op = arena.alloc<grpc_op> {
                op = GRPC_OP_RECV_MESSAGE
                data.recv_message.recv_message = recvPtr.ptr
            }
            runBatch(op.ptr, 1u, cleanup = {
                if (recvPtr.value != null) grpc_byte_buffer_destroy(recvPtr.value)
                arena.clear()
            }) {
                // if the call was successful, but no message was received, we reached the end-of-stream.
                val buf = recvPtr.value ?: return@runBatch
                val msg = methodDescriptor.responseMarshaller
                    .decode(buf.toKotlin())
                safeUserCode("Failed to call onClose.") {
                    listener.onMessage(msg)
                }
                post()
            }
        }

        // start requesting messages
        post()
    }

    override fun cancel(message: String?, cause: Throwable?) {
        cancelled = true
        val status = GrpcStatus(GrpcStatusCode.CANCELLED, message ?: "Call cancelled", cause)
        // user side cancellation must always win over any other status (even if the call is already completed).
        // this will also preserve the cancellation cause, which cannot be passed to the grpc-core.
        closeInfo.value = Pair(status, GrpcMetadata())
        cancelInternal(
            grpc_status_code.GRPC_STATUS_CANCELLED,
            message ?: "Call cancelled with cause: ${cause?.message}"
        )
    }

    private fun cancelInternal(statusCode: grpc_status_code, message: String) {
        // Hold a reader claim while using `raw` so tryToCloseCall cannot fire grpc_call_unref
        // concurrently. If the claim is refused the call is already closed — cancel after
        // onClose is a no-op (matches gRPC-Java semantics).
        if (!beginOp()) return
        try {
            val cancelResult = grpc_call_cancel_with_status(raw, statusCode, message, null)
            if (cancelResult != grpc_call_error.GRPC_CALL_OK) {
                markClosePending(
                    GrpcStatus(GrpcStatusCode.INTERNAL, "Failed to cancel call: $cancelResult"),
                    GrpcMetadata()
                )
            }
        } finally {
            endOp()
        }
    }

    override fun halfClose() {
        check(!halfClosed) { internalError("Already half closed.") }
        if (cancelled) return
        halfClosed = true

        val arena = Arena()
        val op = arena.alloc<grpc_op> {
            op = GRPC_OP_SEND_CLOSE_FROM_CLIENT
        }

        runBatch(op.ptr, 1u, cleanup = { arena.clear() }) {
            // nothing to do here
        }
    }

    override fun isReady(): Boolean = ready.value

    override fun sendMessage(message: Request) {
        checkNotNull(listener) { internalError("Not yet started") }
        check(!halfClosed) { internalError("Already half closed.") }
        check(isReady()) { internalError("Not yet ready.") }

        if (cancelled) return

        // set ready false, as only one message can be sent at a time.
        ready.value = false

        val arena = Arena()
        val source = methodDescriptor.requestMarshaller.encode(message)
        val byteBuffer = source.toGrpcByteBuffer()

        val op = arena.alloc<grpc_op> {
            op = GRPC_OP_SEND_MESSAGE
            data.send_message.send_message = byteBuffer
        }

        runBatch(op.ptr, 1u, cleanup = {
            // actual cleanup
            grpc_byte_buffer_destroy(byteBuffer)
            arena.clear()
        }) {
            // set ready true, as we can now send another message.
            turnReady()
        }
    }

    /**
     * Safely executes the provided block of user code, catching any thrown exceptions or errors.
     * If an exception is caught, it cancels the operation with the specified message and cause.
     */
    private fun safeUserCode(cancelMsg: String, block: () -> Unit) {
        try {
            block()
        } catch (e: Throwable) {
            cancel(cancelMsg, e)
        }
    }

}
