/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal

import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.rpc.internal.utils.InternalRPCApi

@InternalRPCApi
public interface RPCEndpoint {
    @InternalRPCApi
    public val sender: RPCMessageSender
    @InternalRPCApi
    public val supportedPlugins: Set<RPCPlugin>

    @InternalRPCApi
    public fun sendCancellation(
        type: CancellationType,
        serviceId: String?,
        cancellationId: String?,
        closeTransportAfterSending: Boolean = false,
    ) {
        if (!supportedPlugins.contains(RPCPlugin.CANCELLATION)) {
            if (closeTransportAfterSending) {
                sender.cancel("Transport finished")
            }

            return
        }

        val sendJob = sender.launch {
            val message = RPCGenericMessage(
                connectionId = null,
                pluginParams = listOfNotNull(
                    RPCPluginKey.GENERIC_MESSAGE_TYPE to RPCGenericMessage.CANCELLATION_TYPE,
                    RPCPluginKey.CANCELLATION_TYPE to type.toString(),
                    serviceId?.let { RPCPluginKey.CLIENT_SERVICE_ID to serviceId },
                    cancellationId?.let { RPCPluginKey.CANCELLATION_ID to cancellationId },
                ).toMap()
            )

            sender.sendMessage(message)
        }

        if (closeTransportAfterSending) {
            sendJob.invokeOnCompletion {
                sender.cancel("Transport finished")
            }
        }
    }

    @InternalRPCApi
    public suspend fun handleGenericMessage(message: RPCGenericMessage) {
        try {
            when (message.pluginParams?.get(RPCPluginKey.GENERIC_MESSAGE_TYPE)) {
                RPCGenericMessage.CANCELLATION_TYPE -> {
                    handleCancellation(message)
                }

                else -> {
                    // ignore, unknown type
                }
            }
        } catch (e: IllegalStateException) {
            val failure = RPCProtocolMessage.Failure(
                errorMessage = e.message ?: "Unknown error",
                connectionId = message.connectionId,
            )

            sender.sendMessage(failure)
        }
    }

    @InternalRPCApi
    public suspend fun handleCancellation(message: RPCGenericMessage)
}
