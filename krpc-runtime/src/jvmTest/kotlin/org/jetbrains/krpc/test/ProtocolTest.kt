/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.test

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import org.jetbrains.krpc.RPCTransportMessage
import org.jetbrains.krpc.internal.hex.hexToByteArrayInternal
import org.jetbrains.krpc.internal.hex.toHexStringInternal
import org.jetbrains.krpc.internal.transport.RPCPlugin
import org.jetbrains.krpc.rpcClientConfig
import org.jetbrains.krpc.rpcServerConfig
import org.jetbrains.krpc.serialization.protobuf
import org.junit.Test

@Suppress("detekt.MaxLineLength")
@OptIn(ExperimentalSerializationApi::class)
class ProtocolTest : ProtocolTestBase() {
    @Test
    fun testHandshakeWithUpToDateEndpoints() = runTest {
        // the client sends the handshake message first,
        // so as the service is not initializaed yet here,
        // the server should not have any handshake data yet
        assertEquals(0, defaultServer.clientPlugins.size)

        service.sendRequest()

        // after a call is finished - all handshake data should be exchanged
        assertEquals(RPCPlugin.ALL, defaultClient.serverPlugins)
        assertEquals(RPCPlugin.ALL, defaultServer.clientPlugins[defaultClient.id])
    }

    // old client: pre 5.3-beta (including)
    @Test
    fun testNoHandshakeFromClientJson() = runTest {
        // init
        defaultServer

        val message = RPCTransportMessage.StringMessage(
            "{\"type\":\"org.jetbrains.krpc.RPCMessage.CallData\",\"callId\":\"1:org.jetbrains.krpc.ProtocolTestServiceClient.SendRequest_RPCData:1\",\"serviceType\":\"org.jetbrains.krpc.test.ProtocolTestService\",\"method\":\"sendRequest\",\"callType\":\"Method\",\"data\":\"{}\"}",
        )

        transport.client.send(message)

        val response = transport.client.receive() as RPCTransportMessage.StringMessage
        assertEquals(
            "{\"type\":\"org.jetbrains.krpc.RPCMessage.CallSuccess\",\"callId\":\"1:org.jetbrains.krpc.ProtocolTestServiceClient.SendRequest_RPCData:1\",\"serviceType\":\"org.jetbrains.krpc.test.ProtocolTestService\",\"data\":\"{}\"}",
            response.value
        )
        assertEquals(0, defaultServer.clientPlugins.size)
    }

    // old client: pre 5.3-beta (including)
    @Test
    fun testNoHandshakeFromClientProtobuf() = runTest(
        serverConfig = rpcServerConfig {
            serialization {
                protobuf()
            }
        }
    ) {
        // init
        defaultServer

        // same value as testNoHandshakeFromClientJson but in protobuf format
        val message = RPCTransportMessage.BinaryMessage(
            "0a3f6f72672e6a6574627261696e732e6b7270632e696e7465726e616c2e7472616e73706f72742e5250434d6573736167652e43616c6c4461746142696e6172791284010a44313a6f72672e6a6574627261696e732e6b7270632e50726f746f636f6c5465737453657276696365436c69656e742e53656e64526571756573745f525043446174613a31122b6f72672e6a6574627261696e732e6b7270632e746573742e50726f746f636f6c54657374536572766963651a0b73656e645265717565737420002a00".hexToByteArrayInternal(),
        )

        transport.client.send(message)

        val response = transport.client.receive() as RPCTransportMessage.BinaryMessage
        assertEquals(
            "0a426f72672e6a6574627261696e732e6b7270632e696e7465726e616c2e7472616e73706f72742e5250434d6573736167652e43616c6c5375636365737342696e61727912750a44313a6f72672e6a6574627261696e732e6b7270632e50726f746f636f6c5465737453657276696365436c69656e742e53656e64526571756573745f525043446174613a31122b6f72672e6a6574627261696e732e6b7270632e746573742e50726f746f636f6c54657374536572766963651a00",
            response.value.toHexStringInternal()
        )
        assertEquals(0, defaultServer.clientPlugins.size)
    }

    @Test
    fun testUnknownPluginForClient() = runTest {
        val job = launch {
            service.sendRequest()
        }

        transport.server.receive()

        val connectionId = 1

        val serverHandshakeMessage = RPCTransportMessage.StringMessage(
            "{\"type\":\"org.jetbrains.krpc.internal.transport.RPCProtocolMessage.Handshake\",\"connectionId\":$connectionId,\"supportedPlugins\":[-32767, 32766, 32767]}" // 32766 and 32767 are is unknown to client
        )

        transport.server.send(serverHandshakeMessage)

        transport.server.receive()

        val serverResponseMessage = RPCTransportMessage.StringMessage(
            "{\"type\":\"org.jetbrains.krpc.RPCMessage.CallSuccess\",\"callId\":\"$connectionId:org.jetbrains.krpc.ProtocolTestServiceClient.SendRequest_RPCData:1\",\"serviceType\":\"org.jetbrains.krpc.test.ProtocolTestService\",\"data\":\"{}\"}"
        )

        transport.server.send(serverResponseMessage)

        job.join()

        assertEquals(RPCPlugin.ALL + RPCPlugin.UNKNOWN, defaultClient.serverPlugins)
    }

    @Test
    fun testUnknownPluginForServer() = runTest {
        // init
        defaultServer

        val clientHandshakeMessage = RPCTransportMessage.StringMessage(
            "{\"type\":\"org.jetbrains.krpc.internal.transport.RPCProtocolMessage.Handshake\",\"connectionId\":null,\"supportedPlugins\":[-32767, 32766, 32767]}" // 32766 and 32767 are unknown to server
        )

        transport.client.send(clientHandshakeMessage)

        transport.client.receive()

        assertEquals(RPCPlugin.ALL + RPCPlugin.UNKNOWN, defaultServer.clientPlugins.values.single())
    }

    @Test
    fun testMultipleUnknownPluginParamEntriesForServer() = runTest {
        // init
        defaultServer

        val clientHandshakeMessage = RPCTransportMessage.StringMessage(
            "{\"type\":\"org.jetbrains.krpc.internal.transport.RPCProtocolMessage.Handshake\",\"connectionId\":null,\"supportedPlugins\":[-32767],\"pluginParams\":{32766:\"from 32766\", 32767:\"from 32767\"}}" // 32766 and 32767 are unknown to server
        )

        transport.client.send(clientHandshakeMessage)

        transport.client.receive()
        // no checks here, we just make sure that test does not fail on double UNKNOWN key values
    }

    @Test
    fun testMultipleUnknownPluginParamEntriesForServerProtobuf() = runTest(
        serverConfig = rpcServerConfig {
            serialization {
                protobuf()
            }
        },
        clientConfig = rpcClientConfig {
            serialization {
                protobuf()
            }
        }
    ) {
        // init
        defaultServer

        // same value as testMultipleUnknownPluginParamEntriesForServer but in protobuf format
        val clientHandshakeMessage = RPCTransportMessage.BinaryMessage(
            "0a426f72672e6a6574627261696e732e6b7270632e696e7465726e616c2e7472616e73706f72742e52504350726f746f636f6c4d6573736167652e48616e647368616b6512310801108180feffffffffffff011a1008feff01120a66726f6d2033323736361a1008ffff01120a66726f6d203332373637".hexToByteArrayInternal() // 32766 and 32767 are unknown to server
        )

        transport.client.send(clientHandshakeMessage)

        transport.client.receive()
        // no checks here, we just make sure that test does not fail on double UNKNOWN key values
    }
}
