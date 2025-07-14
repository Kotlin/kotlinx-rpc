/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.krpc.KrpcTransport
import kotlinx.rpc.krpc.serialization.KrpcSerialFormatConfiguration
import kotlinx.rpc.krpc.serialization.cbor.cbor
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.krpc.serialization.protobuf.protobuf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.test.Test

abstract class LocalTransportTest : KrpcTransportTestBase() {
    private val transport = LocalTransport()

    override val clientTransport: KrpcTransport
        get() = transport.client

    override val serverTransport: KrpcTransport
        get() = transport.server
}

class JsonLocalTransportTest : LocalTransportTest() {
    override val serializationConfig: KrpcSerialFormatConfiguration.() -> Unit = {
        json {
            serializersModule = this@JsonLocalTransportTest.serializersModule
        }
    }
}

class CborLocalTransportTest : LocalTransportTest() {
    @OptIn(ExperimentalSerializationApi::class)
    override val serializationConfig: KrpcSerialFormatConfiguration.() -> Unit = {
        cbor {
            serializersModule = this@CborLocalTransportTest.serializersModule
        }
    }
}

@Suppress("detekt.EmptyFunctionBlock")
class ProtoBufLocalTransportTest : LocalTransportTest() {
    @OptIn(ExperimentalSerializationApi::class)
    override val serializationConfig: KrpcSerialFormatConfiguration.() -> Unit = {
        protobuf {
            serializersModule = this@ProtoBufLocalTransportTest.serializersModule
        }
    }

    // 'null' is not supported in ProtoBuf
    @Test
    override fun nullableReturn(): TestResult = runTest { }

    @Test
    override fun nullableNonSerializableParameter(): TestResult = runTest { }

    @Test
    override fun testByteArraySerialization(): TestResult = runTest { }

    @Test
    override fun testNullables(): TestResult = runTest { }

    @Test
    override fun testNullableLists(): TestResult = runTest { }
}
