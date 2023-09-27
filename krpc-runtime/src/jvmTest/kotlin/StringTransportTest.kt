import org.jetbrains.krpc.RPCTransport
import org.jetbrains.krpc.test.KRPCTransportTestBase

class StringTransportTest : KRPCTransportTestBase() {
    val transport = StringTransport()

    override val clientTransport: RPCTransport
        get() = transport.client

    override val serverTransport: RPCTransport
        get() = transport.server
}