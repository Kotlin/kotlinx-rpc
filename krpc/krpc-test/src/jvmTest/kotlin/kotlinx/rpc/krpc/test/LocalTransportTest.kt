/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import kotlinx.rpc.krpc.RPCTransport
import kotlinx.rpc.krpc.serialization.RPCSerialFormatConfiguration
import kotlinx.rpc.krpc.serialization.cbor
import kotlinx.rpc.krpc.serialization.json
import kotlinx.rpc.krpc.serialization.protobuf
import kotlinx.serialization.ExperimentalSerializationApi

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
