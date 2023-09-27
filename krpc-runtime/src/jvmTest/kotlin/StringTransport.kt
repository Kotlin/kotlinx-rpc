import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.jetbrains.krpc.RPCMessage
import org.jetbrains.krpc.RPCTransport
import kotlin.coroutines.CoroutineContext

class StringTransport : CoroutineScope {
    override val coroutineContext = Job()
    private val clientIncoming = MutableSharedFlow<RPCMessage>()
    private val serverIncoming = MutableSharedFlow<RPCMessage>()

    val client: RPCTransport = object : RPCTransport {
        override val coroutineContext: CoroutineContext = this@StringTransport.coroutineContext

        override val incoming: SharedFlow<RPCMessage> = clientIncoming

        override suspend fun send(message: RPCMessage) {
            serverIncoming.emit(message)
        }
    }

    val server: RPCTransport = object : RPCTransport {
        override val coroutineContext: CoroutineContext = this@StringTransport.coroutineContext

        override val incoming: SharedFlow<RPCMessage>
            get() = serverIncoming

        override suspend fun send(message: RPCMessage) {
            clientIncoming.emit(message)
        }
    }
}