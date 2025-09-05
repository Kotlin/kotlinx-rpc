/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.launch
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public interface KrpcEndpoint {
    @InternalRpcApi
    public val sender: KrpcMessageSender
    @InternalRpcApi
    public val supportedPlugins: Set<KrpcPlugin>

    @InternalRpcApi
    public fun sendCancellation(
        type: CancellationType,
        serviceId: String?,
        cancellationId: String?,
        closeTransportAfterSending: Boolean = false,
    ) {
        if (!supportedPlugins.contains(KrpcPlugin.CANCELLATION)) {
            if (closeTransportAfterSending) {
                sender.drainSendQueueAndClose("Transport finished")
            }

            return
        }

        val sendJob = sender.transportScope.launch(
            CoroutineName("krpc-endpoint-cancellation-$serviceId-$cancellationId"),
        ) {
            val message = KrpcGenericMessage(
                connectionId = null,
                pluginParams = listOfNotNull(
                    KrpcPluginKey.GENERIC_MESSAGE_TYPE to KrpcGenericMessage.CANCELLATION_TYPE,
                    KrpcPluginKey.CANCELLATION_TYPE to type.toString(),
                    serviceId?.let { KrpcPluginKey.CLIENT_SERVICE_ID to serviceId },
                    cancellationId?.let { KrpcPluginKey.CANCELLATION_ID to cancellationId },
                ).toMap()
            )

            sender.sendMessage(message)
        }

        if (closeTransportAfterSending) {
            sendJob.invokeOnCompletion {
                sender.drainSendQueueAndClose("Transport finished")
            }
        }
    }

    @InternalRpcApi
    public suspend fun handleGenericMessage(message: KrpcGenericMessage) {
        try {
            when (message.pluginParams?.get(KrpcPluginKey.GENERIC_MESSAGE_TYPE)) {
                KrpcGenericMessage.CANCELLATION_TYPE -> {
                    handleCancellation(message)
                }

                else -> {
                    // ignore, unknown type
                }
            }
        } catch (e: IllegalStateException) {
            val failure = KrpcProtocolMessage.Failure(
                errorMessage = e.message ?: "Unknown error",
                connectionId = message.connectionId,
            )

            sender.sendMessage(failure)
        }
    }

    @InternalRpcApi
    public suspend fun handleCancellation(message: KrpcGenericMessage)
}
