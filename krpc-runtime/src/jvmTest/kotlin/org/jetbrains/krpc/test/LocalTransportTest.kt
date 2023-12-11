package org.jetbrains.krpc.test

import kotlinx.serialization.ExperimentalSerializationApi
import org.jetbrains.krpc.RPCTransport
import org.jetbrains.krpc.serialization.RPCSerialFormatConfiguration
import org.jetbrains.krpc.serialization.cbor
import org.jetbrains.krpc.serialization.json
import org.jetbrains.krpc.serialization.protobuf

abstract class LocalTransportTest : KRPCTransportTestBase() {
    private val transport = LocalTransport()

    override val clientTransport: RPCTransport
        get() = transport.client

    override val serverTransport: RPCTransport
        get() = transport.server
}

class JsonLocalTransportTest : LocalTransportTest() {
    override val serializationConfig: RPCSerialFormatConfiguration.() -> Unit = {
        json()
    }
}

class CborLocalTransportTest : LocalTransportTest() {
    @OptIn(ExperimentalSerializationApi::class)
    override val serializationConfig: RPCSerialFormatConfiguration.() -> Unit = {
        cbor()
    }
}

@Suppress("detekt.EmptyFunctionBlock")
class ProtoBufLocalTransportTest : LocalTransportTest() {
    @OptIn(ExperimentalSerializationApi::class)
    override val serializationConfig: RPCSerialFormatConfiguration.() -> Unit = {
        protobuf()
    }

    // 'null' is not supported in ProtoBuf
    override fun nullable() { }

    override fun testByteArraySerialization() { }

    override fun testNullables() { }

    override fun testNullableLists() { }
}
