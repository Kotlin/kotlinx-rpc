/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// RUN_PIPELINE_TILL: BACKEND

// MODULE: main
// TOP_LEVEL_CLASS_NAMES_FOR_GENERATED_PROTOBUF_CLASSES_FILE: src/test/resources/protoNames/protoMessage.txt
// FILE: module_main_protoMessage.kt

@file:OptIn(InternalRpcApi::class, ExperimentalRpcApi::class)

package test.my_message

import kotlinx.rpc.protobuf.internal.GeneratedProtoMessage
import kotlinx.rpc.protobuf.internal.ProtoDescriptor
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.io.Source

@GeneratedProtoMessage
interface MyMessage {
    val field: Int
    val optionalField: Int
}

class MyMessageInternal : MyMessage.Builder {
    override var field: Int = 1

    override var optionalField: Int = 2

    override fun clearOptionalField() {}

    private object PresenceIndices {
        const val optionalField = 0
    }

    object DESCRIPTOR: ProtoDescriptor<MyMessage> {
        override val fullName: String = "fullName"
    }

    object MARSHALLER: GrpcMarshaller<MyMessage> {
        override fun encode(value: MyMessage, config: GrpcMarshallerConfig?): Source {
            TODO()
        }

        override fun decode(source: Source, config: GrpcMarshallerConfig?): MyMessage {
            TODO()
        }
    }
}

fun box(): String {
    return "OK"
}
