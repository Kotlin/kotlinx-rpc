import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.jetbrains.krpc.RPCMessage
import org.jetbrains.krpc.RPCTransport

class StringTransport {
    private val clientIncoming = MutableSharedFlow<RPCMessage>()
    private val serverIncoming = MutableSharedFlow<RPCMessage>()

    val client: RPCTransport = object : RPCTransport {
        override val incoming: SharedFlow<RPCMessage> = clientIncoming

        override suspend fun send(message: RPCMessage) {
            serverIncoming.emit(message)
        }
    }

    val server: RPCTransport = object : RPCTransport {
        override val incoming: SharedFlow<RPCMessage>
            get() = serverIncoming

        override suspend fun send(message: RPCMessage) {
            clientIncoming.emit(message)
        }
    }
}