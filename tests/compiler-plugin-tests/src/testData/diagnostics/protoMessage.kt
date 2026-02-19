/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// RUN_PIPELINE_TILL: FRONTEND

// MODULE: main
@file:OptIn(InternalRpcApi::class)

import kotlinx.rpc.protobuf.GeneratedProtoMessage
import kotlinx.rpc.protobuf.internal.ProtoDescriptor
import kotlinx.rpc.internal.utils.InternalRpcApi

// valid, because there is a corresponding internal class with a DESCRIPTOR object
@GeneratedProtoMessage
interface MyMessage1 { }
class MyMessage1Internal : MyMessage1 {
    object DESCRIPTOR: ProtoDescriptor<MyMessage1> {
        override val fullName: String = "fullName"
    }
}

// invalid, because there message is class, not interface
<!PROTO_MESSAGE_IS_GENERATED_ONLY!>@GeneratedProtoMessage<!>
open class MyMessage2 { }
class MyMessage2Internal : MyMessage2() {
    object DESCRIPTOR: ProtoDescriptor<MyMessage2> {
        override val fullName: String = "fullName"
    }
}

// invalid, because there is no corresponding internal class
<!PROTO_MESSAGE_IS_GENERATED_ONLY!>@GeneratedProtoMessage<!>
interface MyMessage3 { }

// invalid, because there internal class has no DESCRIPTOR object
<!PROTO_MESSAGE_IS_GENERATED_ONLY!>@GeneratedProtoMessage<!>
interface MyMessage4 { }
class MyMessage4Internal : MyMessage4 { }

// invalid, because there is the corresponding internal class doesn't implement the message
<!PROTO_MESSAGE_IS_GENERATED_ONLY!>@GeneratedProtoMessage<!>
interface MyMessage5 { }
class MyMessage5Internal {
    object DESCRIPTOR: ProtoDescriptor<MyMessage5> {
        override val fullName: String = "fullName"
    }
}

// invalid, because there is the descriptor doesn't implement the ProtoDescriptor interface
<!PROTO_MESSAGE_IS_GENERATED_ONLY!>@GeneratedProtoMessage<!>
interface MyMessage6 { }
class MyMessage6Internal: MyMessage6 {
    object DESCRIPTOR {}
}

/* GENERATED_FIR_TAGS: classDeclaration, interfaceDeclaration, nestedClass, objectDeclaration */
