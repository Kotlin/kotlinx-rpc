import io.ktor.websocket.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.krpc.RPCMessage
import org.jetbrains.krpc.RPCTransport

class KtorTransport(
    private val json: Json,
    private val webSocketSession: WebSocketSession
) : RPCTransport {

    private object FlowEnd

    private val incomingFlow: Flow<Any> = flow {
        for (message in webSocketSession.incoming) {
            check(message is Frame.Text)
            val messageText = message.readText()
            val rpcMessage = json.decodeFromString<RPCMessage>(messageText)
            emit(rpcMessage)
        }
        emit(FlowEnd)
    }

    override val incoming: SharedFlow<RPCMessage>
        get() = incomingFlow
            .takeWhile { it != FlowEnd }
            .map { it as RPCMessage }
            .shareIn(webSocketSession, started = SharingStarted.Eagerly)

    override suspend fun send(message: RPCMessage) {
        val messageText = json.encodeToString(message)
        webSocketSession.send(messageText)
    }
}