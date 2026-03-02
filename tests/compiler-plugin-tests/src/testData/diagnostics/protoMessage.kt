/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// RUN_PIPELINE_TILL: FRONTEND

// MODULE: main
@file:OptIn(InternalRpcApi::class, ExperimentalRpcApi::class)

import kotlinx.io.Source
import kotlinx.rpc.protobuf.internal.GeneratedProtoMessage
import kotlinx.rpc.protobuf.internal.ProtoDescriptor
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.grpc.marshaller.MessageMarshaller
import kotlinx.rpc.grpc.marshaller.MarshallerConfig

// valid, because there is a corresponding builder interface and internal class with DESCRIPTOR and MARSHALLER objects
@GeneratedProtoMessage
interface MyMessage1
class MyMessage1Internal : MyMessage1.Builder {
    object DESCRIPTOR: ProtoDescriptor<MyMessage1> {
        override val fullName: String = "fullName"
    }

    object MARSHALLER: MessageMarshaller<MyMessage1> {
        override fun encode(value: MyMessage1, config: MarshallerConfig?): Source {
            TODO()
        }

        override fun decode(source: Source, config: MarshallerConfig?): MyMessage1 {
            TODO()
        }
    }
}

// no builder
<!PROTO_MESSAGE_IS_GENERATED_ONLY!>@GeneratedProtoMessage<!>
interface MyMessage9 { }
class MyMessage9Internal : MyMessage9 {
    object DESCRIPTOR: ProtoDescriptor<MyMessage9> {
        override val fullName: String = "fullName"
    }

    object MARSHALLER: MessageMarshaller<MyMessage9> {
        override fun encode(value: MyMessage9, config: MarshallerConfig?): Source {
            TODO()
        }

        override fun decode(source: Source, config: MarshallerConfig?): MyMessage9 {
            TODO()
        }
    }
}

// invalid, because there message is class, not interface
<!PROTO_MESSAGE_IS_GENERATED_ONLY!>@GeneratedProtoMessage<!>
open class MyMessage2
class MyMessage2Internal : MyMessage2.<!UNRESOLVED_REFERENCE!>Builder<!> {
    object DESCRIPTOR: ProtoDescriptor<MyMessage2> {
        override val fullName: String = "fullName"
    }
    object MARSHALLER: MessageMarshaller<MyMessage2> {
        override fun encode(value: MyMessage2, config: MarshallerConfig?): Source {
            TODO()
        }

        override fun decode(source: Source, config: MarshallerConfig?): MyMessage2 {
            TODO()
        }
    }
}

// invalid, because there is no corresponding internal class
<!PROTO_MESSAGE_IS_GENERATED_ONLY!>@GeneratedProtoMessage<!>
interface MyMessage3

// invalid, because there internal class has no DESCRIPTOR object
<!PROTO_MESSAGE_IS_GENERATED_ONLY!>@GeneratedProtoMessage<!>
interface MyMessage4
class MyMessage4Internal : MyMessage4.Builder {
    object MARSHALLER: MessageMarshaller<MyMessage4> {
        override fun encode(value: MyMessage4, config: MarshallerConfig?): Source {
            TODO()
        }

        override fun decode(source: Source, config: MarshallerConfig?): MyMessage4 {
            TODO()
        }
    }
}

// invalid, because there is the corresponding internal class doesn't implement the builder
<!PROTO_MESSAGE_IS_GENERATED_ONLY!>@GeneratedProtoMessage<!>
interface MyMessage5
class MyMessage5Internal: MyMessage5 {
    object DESCRIPTOR: ProtoDescriptor<MyMessage5> {
        override val fullName: String = "fullName"
    }

    object MARSHALLER: MessageMarshaller<MyMessage5> {
        override fun encode(value: MyMessage5, config: MarshallerConfig?): Source {
            TODO()
        }

        override fun decode(source: Source, config: MarshallerConfig?): MyMessage5 {
            TODO()
        }
    }
}

// invalid, because there is the descriptor doesn't implement the ProtoDescriptor interface
<!PROTO_MESSAGE_IS_GENERATED_ONLY!>@GeneratedProtoMessage<!>
interface MyMessage6
class MyMessage6Internal: MyMessage6.Builder {
    object DESCRIPTOR {}

    object MARSHALLER: MessageMarshaller<MyMessage6> {
        override fun encode(value: MyMessage6, config: MarshallerConfig?): Source {
            TODO()
        }

        override fun decode(source: Source, config: MarshallerConfig?): MyMessage6 {
            TODO()
        }
    }
}

// invalid, because there is the marshaller doesn't implement the MessageMarshaller interface
<!PROTO_MESSAGE_IS_GENERATED_ONLY!>@GeneratedProtoMessage<!>
interface MyMessage7
class MyMessage7Internal: MyMessage7.Builder {
    object DESCRIPTOR: ProtoDescriptor<MyMessage7> {
        override val fullName: String = "fullName"
    }

    object MARSHALLER {}
}

// invalid, because there is no marshaller
<!PROTO_MESSAGE_IS_GENERATED_ONLY!>@GeneratedProtoMessage<!>
interface MyMessage8
class MyMessage8Internal: MyMessage8.Builder {
    object DESCRIPTOR: ProtoDescriptor<MyMessage8> {
        override val fullName: String = "fullName"
    }
}

/* GENERATED_FIR_TAGS: classDeclaration, interfaceDeclaration, nestedClass, objectDeclaration */
