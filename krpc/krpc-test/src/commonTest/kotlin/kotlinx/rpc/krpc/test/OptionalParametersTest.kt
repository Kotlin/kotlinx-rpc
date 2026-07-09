/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.descriptor.RpcInvokator
import kotlinx.rpc.descriptor.serviceDescriptorOf
import kotlinx.rpc.descriptor.unaryInvokator
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.krpc.KrpcTransportMessage
import kotlinx.rpc.krpc.internal.CallableParametersSerializer
import kotlinx.rpc.registerService
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Rpc
interface OptionalParamsService {
    suspend fun concat(a: Int, b: String = "default", c: String? = "nullable"): String
}

// "old client" view of OptionalParamsService: the same method before the optional parameters were added
@Rpc
interface OptionalParamsServiceV1 {
    suspend fun concat(a: Int): String
}

private class OptionalParamsServiceImpl : OptionalParamsService {
    override suspend fun concat(a: Int, b: String, c: String?): String = "$a-$b-$c"
}

@OptIn(ExperimentalRpcApi::class, ExperimentalSerializationApi::class)
class OptionalParametersTest : ProtocolTestBase() {
    private val callable = serviceDescriptorOf<OptionalParamsService>().getCallable("concat")
        ?: error("No 'concat' callable found")

    private val callableV1 = serviceDescriptorOf<OptionalParamsServiceV1>().getCallable("concat")
        ?: error("No V1 'concat' callable found")

    // simulates an old client that does not know about the optional parameters
    // added to the method by a newer server (#543)
    @Test
    fun testAbsentOptionalParametersOnTheWire() = runTest {
        defaultServer.registerService<OptionalParamsService> { OptionalParamsServiceImpl() }

        val message = KrpcTransportMessage.StringMessage(
            "{\"type\":\"org.jetbrains.krpc.RPCMessage.CallData\"," +
                    "\"callId\":\"1:concat:1\"," +
                    "\"serviceType\":\"kotlinx.rpc.krpc.test.OptionalParamsService\"," +
                    "\"method\":\"concat\"," +
                    "\"callType\":\"Method\"," +
                    "\"data\":\"{\\\"a\\\":1}\"}"
        )

        transport.client.send(message)

        val response = transport.client.receive() as KrpcTransportMessage.StringMessage
        assertTrue(
            response.value.contains("1-default-nullable"),
            "Expected the server to apply default values, got: ${response.value}",
        )
    }

    @Test
    fun testAllOptionalParametersPresentOnTheWire() = runTest {
        defaultServer.registerService<OptionalParamsService> { OptionalParamsServiceImpl() }

        val message = KrpcTransportMessage.StringMessage(
            "{\"type\":\"org.jetbrains.krpc.RPCMessage.CallData\"," +
                    "\"callId\":\"1:concat:1\"," +
                    "\"serviceType\":\"kotlinx.rpc.krpc.test.OptionalParamsService\"," +
                    "\"method\":\"concat\"," +
                    "\"callType\":\"Method\"," +
                    "\"data\":\"{\\\"a\\\":5,\\\"b\\\":\\\"x\\\",\\\"c\\\":\\\"y\\\"}\"}"
        )

        transport.client.send(message)

        val response = transport.client.receive() as KrpcTransportMessage.StringMessage
        assertTrue(
            response.value.contains("5-x-y"),
            "Expected provided values to be used, got: ${response.value}",
        )
    }

    @Test
    fun testAbsentParametersSerialization() = runTest {
        val json = Json
        val serializer = CallableParametersSerializer(callable, json.serializersModule)
        val service = OptionalParamsServiceImpl()

        // absent optional parameters are not encoded
        val encoded = json.encodeToString(serializer, arrayOf<Any?>(1, RpcInvokator.Absent, RpcInvokator.Absent))
        assertEquals("""{"a":1}""", encoded)

        // and are decoded back as absent
        val decoded = json.decodeFromString(serializer, encoded)
        assertEquals(listOf<Any?>(1, RpcInvokator.Absent, RpcInvokator.Absent), decoded.toList())

        assertEquals("1-default-nullable", callable.unaryInvokator.call(service, decoded))

        // explicit null for a parameter of a nullable type is preserved, not defaulted
        val decodedNull = json.decodeFromString(serializer, """{"a":2,"c":null}""")
        assertEquals("2-default-null", callable.unaryInvokator.call(service, decodedNull))
    }

    // the serializer always encodes explicit nulls, even with explicitNulls = false —
    // so between kRPC peers, wire-field absence always means argument absence, never explicit null
    @Test
    fun testExplicitNullSurvivesExplicitNullsFalse() = runTest {
        val json = Json { explicitNulls = false }
        val serializer = CallableParametersSerializer(callable, json.serializersModule)

        val encoded = json.encodeToString(serializer, arrayOf<Any?>(1, "b", null))
        assertEquals("""{"a":1,"b":"b","c":null}""", encoded)

        val decoded = json.decodeFromString(serializer, encoded)
        assertEquals("1-b-null", callable.unaryInvokator.call(OptionalParamsServiceImpl(), decoded))
    }

    @Test
    fun testAbsentOptionalParametersDecodeCbor() = runTest {
        val cbor = Cbor
        val v1Payload = cbor.encodeToByteArray(
            CallableParametersSerializer(callableV1, cbor.serializersModule),
            arrayOf<Any?>(1),
        )

        val decoded = cbor.decodeFromByteArray(CallableParametersSerializer(callable, cbor.serializersModule), v1Payload)
        assertEquals(listOf<Any?>(1, RpcInvokator.Absent, RpcInvokator.Absent), decoded.toList())
        assertEquals("1-default-nullable", callable.unaryInvokator.call(OptionalParamsServiceImpl(), decoded))
    }

    @Test
    fun testAbsentOptionalParametersCborDefiniteLengthRoundTrip() = runTest {
        val cbor = Cbor { useDefiniteLengthEncoding = true }
        val serializer = CallableParametersSerializer(callable, cbor.serializersModule)

        val encoded = cbor.encodeToByteArray(serializer, arrayOf<Any?>(1, RpcInvokator.Absent, RpcInvokator.Absent))
        val decoded = cbor.decodeFromByteArray(serializer, encoded)

        assertEquals(listOf<Any?>(1, RpcInvokator.Absent, RpcInvokator.Absent), decoded.toList())
    }

    @Test
    fun testAbsentOptionalParametersDecodeProtobuf() = runTest {
        val protobuf = ProtoBuf
        val v1Payload = protobuf.encodeToByteArray(
            CallableParametersSerializer(callableV1, protobuf.serializersModule),
            arrayOf<Any?>(1),
        )

        val decoded = protobuf.decodeFromByteArray(
            CallableParametersSerializer(callable, protobuf.serializersModule),
            v1Payload,
        )
        assertEquals(listOf<Any?>(1, RpcInvokator.Absent, RpcInvokator.Absent), decoded.toList())
        assertEquals("1-default-nullable", callable.unaryInvokator.call(OptionalParamsServiceImpl(), decoded))
    }
}
