/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class, InternalRpcApi::class)

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
import kotlinx.rpc.grpc.Status
import kotlinx.rpc.grpc.StatusCode
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.descriptor.MethodDescriptor
import kotlinx.rpc.grpc.internal.BatchResult
import kotlinx.rpc.grpc.internal.CompletionQueue
import kotlinx.rpc.grpc.internal.destroyEntries
import kotlinx.rpc.grpc.internal.internalError
import kotlinx.rpc.grpc.internal.toByteArray
import kotlinx.rpc.grpc.internal.toGrpcByteBuffer
import kotlinx.rpc.grpc.internal.toKotlin
import kotlinx.rpc.grpc.GrpcCompression
import kotlinx.rpc.grpc.client.EmptyCallCredentials
import kotlinx.rpc.grpc.client.GrpcCallOptions
import kotlinx.rpc.grpc.client.createRaw
import libkgrpc.GRPC_OP_RECV_INITIAL_METADATA
import libkgrpc.GRPC_OP_RECV_MESSAGE
import libkgrpc.GRPC_OP_RECV_STATUS_ON_CLIENT
import libkgrpc.GRPC_OP_SEND_CLOSE_FROM_CLIENT
import libkgrpc.GRPC_OP_SEND_INITIAL_METADATA
import libkgrpc.GRPC_OP_SEND_MESSAGE
import libkgrpc.gpr_free
import libkgrpc.grpc_byte_buffer
import libkgrpc.grpc_byte_buffer_destroy
import libkgrpc.grpc_call_cancel_with_status
import libkgrpc.grpc_call_credentials_release
import libkgrpc.grpc_call_error
import libkgrpc.grpc_call_set_credentials
import libkgrpc.grpc_call_unref
import libkgrpc.grpc_metadata_array
import libkgrpc.grpc_metadata_array_destroy
import libkgrpc.grpc_metadata_array_init
import libkgrpc.grpc_op
import libkgrpc.grpc_slice
import libkgrpc.grpc_slice_unref
import libkgrpc.grpc_status_code
import kotlin.coroutines.CoroutineContext
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner


internal class NativeClientCall<Request, Response>(
    private val cq: CompletionQueue,
    internal val raw: CPointer<grpc_call>,
    private val methodDescriptor: MethodDescriptor<Request, Response>,
    private val callOptions: GrpcCallOptions,
    private val callJob: CompletableJob,
    private val coroutineContext: CoroutineContext,
) : ClientCall<Request, Response>() {

    @Suppress("unused")
    private val rawCleaner = createCleaner(raw) {
        grpc_call_unref(it)
    }

    private val rawCallCredentials = callOptions.callCredentials.let {
        if (it is EmptyCallCredentials) null else it.createRaw(coroutineContext)
    }

    @Suppress("unused")
    private val rawCallCredentialsCleaner = createCleaner(rawCallCredentials) {
        if (it != null) grpc_call_credentials_release(it)
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
        }
    }

    private var listener: Listener<Response>? = null
    private var halfClosed = false
    private var cancelled = false
    private val closed = atomic(false)

    // tracks how many operations are in flight (not yet completed by the listener).
    // if 0 and we got a closeInfo (containing the status), there are no more ongoing operations.
    // in this case, we can safely call onClose on the listener.
    // we need this mechanism to ensure that onClose is not called while any other callback is still running
    // on the listener.
    private val inFlight = atomic(0)

    // holds the received status information returned by the RECV_STATUS_ON_CLIENT batch.
    // if null, the call is still in progress. otherwise, the call can be closed as soon as inFlight is 0.
    private val closeInfo = atomic<Pair<Status, GrpcMetadata>?>(null)

    // we currently don't buffer messages, so after one `sendMessage` call, ready turns false. (KRPC-192)
    private val ready = atomic(true)

    /**
     * Increments the [inFlight] counter by one.
     * This should be called before starting a batch.
     */
    private fun beginOp() {
        inFlight.incrementAndGet()
    }

    /**
     * Decrements the [inFlight] counter by one.
     * This should be called after a batch has finished (in case of success AND error)
     * AND the corresponding listener callback returned.
     *
     * If the counter reaches 0, no more listener callbacks are executed, and the call can be closed by
     * calling [tryToCloseCall].
     */
    private fun endOp() {
        if (inFlight.decrementAndGet() == 0) {
            tryToCloseCall()
        }
    }

    /**
     * Tries to close the call by invoking the listener's onClose callback.
     *
     * - If the call is already closed, this does nothing.
     * - If the RECV_STATUS_ON_CLIENT batch is still in progress, this does nothing.
     * - If the [inFlight] counter is not 0, this does nothing.
     * - Otherwise, the listener's onClose callback is invoked and the call is closed.
     */
    private fun tryToCloseCall() {
        val info = closeInfo.value ?: return
        if (inFlight.value == 0 && closed.compareAndSet(expect = false, update = true)) {
            val lst = checkNotNull(listener) { internalError("Not yet started") }
            // allows the managed channel to join for the call to finish.
            callJob.complete()
            safeUserCode("Failed to call onClose.") {
                lst.onClose(info.first, info.second)
            }
        }
    }

    /**
     * Sets the [closeInfo] and calls [tryToCloseCall].
     * This is called as soon as the RECV_STATUS_ON_CLIENT batch (started with [startRecvStatus]) finished.
     */
    private fun markClosePending(status: Status, trailers: GrpcMetadata) {
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
     * Submits a batch operation to the [CompletionQueue] and handle the returned [kotlinx.rpc.grpc.internal.BatchResult].
     * If the batch was successfully submitted, [onSuccess] is called.
     * In any case, [cleanup] is called.
     */
    private fun runBatch(
        ops: CPointer<grpc_op>,
        nOps: ULong,
        cleanup: () -> Unit = {},
        onSuccess: () -> Unit = {},
    ) {
        // we must not try to run a batch after the call is closed.
        if (closed.value) return cleanup()

        // pre-book the batch, so onClose cannot be called before the batch finished.
        beginOp()

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
                endOp()
                cancelInternal(grpc_status_code.GRPC_STATUS_UNAVAILABLE, "Channel shutdown")
            }

            is BatchResult.SubmitError -> {
                cleanup()
                endOp()
                cancelInternal(
                    grpc_status_code.GRPC_STATUS_INTERNAL,
                    "Batch could not be submitted: ${callResult.error}"
                )
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
                    val status = Status(kStatusCode, details, null)
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
                markClosePending(Status(StatusCode.UNAVAILABLE, "Channel shutdown"), GrpcMetadata())
                return false
            }

            is BatchResult.SubmitError -> {
                arena.clear()
                markClosePending(
                    Status(StatusCode.INTERNAL, "Failed to start call: ${callResult.error}"),
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
                // to match the behavior of grpc-java, we throw an error if the compression algorithm is not supported.
                cancelInternal(grpc_status_code.GRPC_STATUS_INTERNAL, "Unable to find compressor by name ${callOptions.compression.name}")
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
                val msg = methodDescriptor.responseCodec
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
        val status = Status(StatusCode.CANCELLED, message ?: "Call cancelled", cause)
        // user side cancellation must always win over any other status (even if the call is already completed).
        // this will also preserve the cancellation cause, which cannot be passed to the grpc-core.
        closeInfo.value = Pair(status, GrpcMetadata())
        cancelInternal(
            grpc_status_code.GRPC_STATUS_CANCELLED,
            message ?: "Call cancelled with cause: ${cause?.message}"
        )
    }

    private fun cancelInternal(statusCode: grpc_status_code, message: String) {
        val cancelResult = grpc_call_cancel_with_status(raw, statusCode, message, null)
        if (cancelResult != grpc_call_error.GRPC_CALL_OK) {
            markClosePending(Status(StatusCode.INTERNAL, "Failed to cancel call: $cancelResult"), GrpcMetadata())
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
        val source = methodDescriptor.requestCodec.encode(message)
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
