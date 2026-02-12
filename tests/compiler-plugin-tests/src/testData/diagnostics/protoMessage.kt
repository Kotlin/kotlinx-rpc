/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// RUN_PIPELINE_TILL: FRONTEND

// MODULE: main
@file:OptIn(InternalRpcApi::class)

import kotlinx.rpc.protobuf.ProtoMessage
import kotlinx.rpc.internal.utils.InternalRpcApi

// valid, because there is a corresponding internal class with a DESCRIPTOR object
@ProtoMessage
interface MyMessage1 { }
class MyMessage1Internal : MyMessage1 {
    object DESCRIPTOR {}
}

// invalid, because there message is class, not interface
<!PROTO_MESSAGE_IS_GENERATED_ONLY!>@ProtoMessage<!>
open class MyMessage2 { }
class MyMessage2Internal : MyMessage2() {
    object DESCRIPTOR {}
}

// invalid, because there is no corresponding internal class
<!PROTO_MESSAGE_IS_GENERATED_ONLY!>@ProtoMessage<!>
interface MyMessage3 { }

// invalid, because there internal class has no DESCRIPTOR object
<!PROTO_MESSAGE_IS_GENERATED_ONLY!>@ProtoMessage<!>
interface MyMessage4 { }
class MyMessage4Internal : MyMessage4 { }

// invalid, because there is the corresponding internal class doesn't implement the message
<!PROTO_MESSAGE_IS_GENERATED_ONLY!>@ProtoMessage<!>
interface MyMessage5 { }
class MyMessage5Internal {
    object DESCRIPTOR {}
}

/* GENERATED_FIR_TAGS: classDeclaration, interfaceDeclaration, nestedClass, objectDeclaration */
