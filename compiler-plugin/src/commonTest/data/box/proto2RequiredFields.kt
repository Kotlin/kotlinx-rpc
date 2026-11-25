/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// RUN_PIPELINE_TILL: BACKEND

@file:OptIn(kotlinx.rpc.internal.utils.InternalRpcApi::class, kotlinx.rpc.internal.utils.ExperimentalRpcApi::class)

package test.proto2_required_fields

import kotlinx.rpc.grpc.marshaller.GrpcEncodedMessage
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.grpc.marshaller.GrpcMessageReader
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtobufException
import kotlinx.rpc.protobuf.internal.GeneratedProtoMessage
import kotlinx.rpc.protobuf.internal.ProtoDescriptor

@GeneratedProtoMessage
interface RequiredMessage {
    val number: Int
    val text: String
}

class RequiredMessageInternal : RequiredMessage.Builder {
    private val presenceMask = BooleanArray(2)

    override var number: Int = 0
        set(value) {
            field = value
            presenceMask[0] = true
        }

    override var text: String = ""
        set(value) {
            field = value
            presenceMask[1] = true
        }

    @InternalRpcApi
    fun checkRequiredFields() {
        if (!presenceMask[0]) {
            throw ProtobufException.missingRequiredField("RequiredMessage", "number")
        }
        if (!presenceMask[1]) {
            throw ProtobufException.missingRequiredField("RequiredMessage", "text")
        }
    }

    object DESCRIPTOR : ProtoDescriptor<RequiredMessage> {
        override val fullName: String = "test.proto2_required_fields.RequiredMessage"
    }

    object MARSHALLER : GrpcMarshaller<RequiredMessage> {
        override fun prepare(value: RequiredMessage, config: GrpcMarshallerConfig?): GrpcEncodedMessage {
            TODO()
        }

        override fun decode(reader: GrpcMessageReader, config: GrpcMarshallerConfig?): RequiredMessage {
            TODO()
        }
    }
}

private fun expectMissingRequiredField(fieldName: String, body: RequiredMessage.Builder.() -> Unit): String? {
    try {
        RequiredMessage(body)
    } catch (exception: ProtobufException) {
        val expectedMessage = ProtobufException.missingRequiredField("RequiredMessage", fieldName).message
        return if (exception.message == expectedMessage) null else "Fail: Unexpected exception: $exception"
    }
    return "Fail: Expected an exception for missing required field '$fieldName'."
}

fun box(): String {
    expectMissingRequiredField("number") {}?.let { return it }
    expectMissingRequiredField("text") { number = 42 }?.let { return it }
    expectMissingRequiredField("number") { text = "initialized" }?.let { return it }

    val message = RequiredMessage {
        number = 42
        text = "initialized"
    }
    if (message.number != 42 || message.text != "initialized") {
        return "Fail: Initialized required fields have incorrect values."
    }

    val defaults = RequiredMessage {
        number = 0
        text = ""
    }
    if (defaults.number != 0 || defaults.text != "") {
        return "Fail: Required fields initialized to defaults have incorrect values."
    }

    return "OK"
}
