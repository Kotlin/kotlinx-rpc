/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.sample

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.rpc.sample.messages.ChatEntry
import kotlinx.rpc.sample.messages.MessageService
import kotlinx.rpc.sample.messages.ReceiveMessagesRequest
import kotlinx.rpc.sample.messages.SendMessageRequest
import kotlinx.rpc.sample.messages.SendMessageResponse
import kotlinx.rpc.sample.messages.invoke

class MessageServiceImpl: MessageService {
    private val bus = MutableSharedFlow<ChatEntry>(extraBufferCapacity = 64, replay = 0)

    override suspend fun SendMessage(message: SendMessageRequest): SendMessageResponse {
        // ensure server timestamp / id if you carry those
        val entry = ChatEntry {
            user = message.user
            text = message.text
            tsMillis = System.currentTimeMillis()
        }
        val ok = bus.tryEmit(entry)
        return SendMessageResponse { success = ok }
    }

    override fun ReceiveMessages(message: ReceiveMessagesRequest): Flow<ChatEntry> {
        // broadcast to everyone
        return bus.asSharedFlow()
            .filter { it.user != message.user }
    }
}