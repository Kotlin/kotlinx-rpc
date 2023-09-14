import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.jetbrains.krpc.RPCMessage
import org.jetbrains.krpc.RPCTransport

class StringTransport {
    public val logg = mutableListOf<String>()
    private val clientIncoming = MutableSharedFlow<RPCMessage>()
    private val serverIncoming = MutableSharedFlow<RPCMessage>()

    val client: RPCTransport = object : RPCTransport {
        override val incoming: SharedFlow<RPCMessage> = clientIncoming

        override suspend fun send(message: RPCMessage) {
            log("Client: $message")
            serverIncoming.emit(message)
        }
    }

    val server: RPCTransport = object : RPCTransport {
        override val incoming: SharedFlow<RPCMessage>
            get() = serverIncoming

        override suspend fun send(message: RPCMessage) {
            log("Server: $message")
            clientIncoming.emit(message)
        }
    }

    private fun log(message: String) {
        logg.add(message)
        println(message)
    }
}