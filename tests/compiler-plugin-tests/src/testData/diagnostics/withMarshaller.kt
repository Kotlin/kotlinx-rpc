/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// RUN_PIPELINE_TILL: FRONTEND

// MODULE: main

@file:OptIn(kotlinx.rpc.internal.utils.ExperimentalRpcApi::class)

import kotlinx.rpc.grpc.marshaller.marshallerOf
import kotlinx.rpc.grpc.marshaller.MarshallerConfig
import kotlinx.rpc.grpc.marshaller.WithMarshaller
import kotlinx.rpc.grpc.marshaller.MessageMarshaller
import kotlinx.io.Source

@WithMarshaller(TestMarshaller::class)
open class Test

@WithMarshaller(<!MARSHALLER_TYPE_MISMATCH!>TestMarshaller::class<!>)
class Test1

@WithMarshaller(<!MARSHALLER_TYPE_MISMATCH!>TestMarshaller::class<!>)
class Test2 : Test()

object TestMarshaller : MessageMarshaller<Test> {
    override fun encode(value: Test, config: MarshallerConfig?): Source {
        error("Not implemented")
    }

    override fun decode(source: Source, config: MarshallerConfig?): Test {
        error("Not implemented")
    }
}

@WithMarshaller(<!NOT_AN_OBJECT_REFERENCE_IN_WITH_MARSHALLER_ANNOTATION!>TestMarshaller3::class<!>)
class Test3

class TestMarshaller3 : MessageMarshaller<Test3> {
    override fun encode(value: Test3, config: MarshallerConfig?): Source {
        error("Not implemented")
    }

    override fun decode(source: Source, config: MarshallerConfig?): Test3 {
        error("Not implemented")
    }
}

@WithMarshaller(TestMarshaller4::class)
class Test4

object TestMarshaller4 : ATestMarshaller4(), Whatever

interface Whatever

abstract class ATestMarshaller4 : MessageMarshaller<Test4> {
    override fun encode(value: Test4, config: MarshallerConfig?): Source {
        error("Not implemented")
    }

    override fun decode(source: Source, config: MarshallerConfig?): Test4 {
        error("Not implemented")
    }
}

@WithMarshaller(<!MARSHALLER_TYPE_MISMATCH!>TestMarshaller4::class<!>)
class Test5

interface CustomMarshaller1<A> : MessageMarshaller<A>
interface CustomMarshaller2<B> : MessageMarshaller<B>
interface CustomMarshaller3<C> : CustomMarshaller2<C>
interface CustomMarshaller4<D> : CustomMarshaller3<D>, CustomMarshaller1<D>

@WithMarshaller(TestMarshaller8::class)
class Test8

object TestMarshaller8 : CustomMarshaller4<Test8> {
    override fun encode(value: Test8, config: MarshallerConfig?): Source {
        error("Not implemented")
    }

    override fun decode(source: Source, config: MarshallerConfig?): Test8 {
        error("Not implemented")
    }
}

@WithMarshaller(<!MARSHALLER_TYPE_MISMATCH!>TestMarshaller9::class<!>)
class Test9

object TestMarshaller9 : CustomMarshaller4<Test8> {
    override fun encode(value: Test8, config: MarshallerConfig?): Source {
        error("Not implemented")
    }

    override fun decode(source: Source, config: MarshallerConfig?): Test8 {
        error("Not implemented")
    }
}

class Test10
val testMarshaller10 = marshallerOf<<!CHECKED_ANNOTATION_VIOLATION!>Test10<!>>()

val testMarshaller = marshallerOf<Test>()

/* GENERATED_FIR_TAGS: annotationUseSiteTargetFile, classDeclaration, classReference, functionDeclaration,
interfaceDeclaration, nullableType, objectDeclaration, override, stringLiteral, typeParameter */
