/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.adapter

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.server.GrpcServerCallScope
import kotlinx.rpc.grpc.server.GrpcServerInterceptor
import kotlinx.rpc.grpc.server.testing.adapterTest
import kotlinx.rpc.grpc.server.testing.assertClosedWith
import kotlinx.rpc.grpc.server.testing.assertMessages
import kotlin.test.Test
import kotlin.test.assertEquals

class AdapterInterceptorOrderTest {
    @Test
    fun interceptorsRunInRegistrationOrderBeforeTheService() = adapterTest { fixture ->
        val marks = mutableListOf<String>()
        val first = object : GrpcServerInterceptor {
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> {
                marks += "first"
                return proceed(request)
            }
        }
        val second = object : GrpcServerInterceptor {
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> {
                marks += "second"
                return proceed(request)
            }
        }
        val session = fixture.unary(listOf(first, second)) {
            marks += "service"
            it
        }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        assertEquals(listOf("first", "second", "service"), marks)
        session.call.assertClosedWith(GrpcStatusCode.OK)
    }

    @Test
    fun reversedRegistrationReversesInterceptorEntryOrder() = adapterTest { fixture ->
        val marks = mutableListOf<String>()
        fun recordingInterceptor(name: String) = object : GrpcServerInterceptor {
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> {
                marks += name
                return proceed(request)
            }
        }
        val first = recordingInterceptor("first")
        val second = recordingInterceptor("second")
        val session = fixture.unary(listOf(second, first)) {
            marks += "service"
            it
        }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        assertEquals(listOf("second", "first", "service"), marks)
        session.call.assertClosedWith(GrpcStatusCode.OK)
    }

    @Test
    fun requestTransformationsRunInRegistrationOrder() = adapterTest { fixture ->
        val received = mutableListOf<String>()
        fun wrappingInterceptor(name: String) = object : GrpcServerInterceptor {
            @Suppress("UNCHECKED_CAST")
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> = proceed(request.map { "$name($it)" as Request })
        }
        val interceptors = listOf(wrappingInterceptor("first"), wrappingInterceptor("second"))
        val session = fixture.bidiStreaming(interceptors) { requests ->
            requests.map {
                received += it
                it
            }
        }

        for (message in listOf("one", "two")) {
            fixture.runAdapterTasks()
            session.deliverRequest(message)
        }
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        val expected = listOf("second(first(one))", "second(first(two))")
        assertEquals(expected, received)
        session.call.assertMessages(expected)
        session.call.assertClosedWith(GrpcStatusCode.OK)
    }

    @Test
    fun responseTransformationsRunInReverseRegistrationOrder() = adapterTest { fixture ->
        fun wrappingInterceptor(name: String) = object : GrpcServerInterceptor {
            @Suppress("UNCHECKED_CAST")
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> = proceed(request).map { "$name($it)" as Response }
        }
        val interceptors = listOf(wrappingInterceptor("first"), wrappingInterceptor("second"))
        val session = fixture.serverStreaming(interceptors) { request ->
            flowOf("$request-one", "$request-two")
        }

        fixture.runAdapterTasks()
        session.deliverRequest("response")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        session.call.assertMessages("first(second(response-one))", "first(second(response-two))")
        session.call.assertClosedWith(GrpcStatusCode.OK)
    }
}
