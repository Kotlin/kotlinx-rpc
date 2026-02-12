/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// RUN_PIPELINE_TILL: FRONTEND

// MODULE: main

@file:OptIn(kotlinx.rpc.internal.utils.ExperimentalRpcApi::class)

import kotlinx.rpc.grpc.codec.codec
import kotlinx.rpc.grpc.codec.CodecConfig
import kotlinx.rpc.grpc.codec.WithCodec
import kotlinx.rpc.grpc.codec.MessageCodec
import kotlinx.io.Source

@WithCodec(TestCodec::class)
open class Test

@WithCodec(<!CODEC_TYPE_MISMATCH!>TestCodec::class<!>)
class Test1

@WithCodec(<!CODEC_TYPE_MISMATCH!>TestCodec::class<!>)
class Test2 : Test()

object TestCodec : MessageCodec<Test> {
    override fun encode(value: Test, config: CodecConfig?): Source {
        error("Not implemented")
    }

    override fun decode(source: Source, config: CodecConfig?): Test {
        error("Not implemented")
    }
}

@WithCodec(<!NOT_AN_OBJECT_REFERENCE_IN_WITH_CODEC_ANNOTATION!>TestCodec3::class<!>)
class Test3

class TestCodec3 : MessageCodec<Test3> {
    override fun encode(value: Test3, config: CodecConfig?): Source {
        error("Not implemented")
    }

    override fun decode(source: Source, config: CodecConfig?): Test3 {
        error("Not implemented")
    }
}

@WithCodec(TestCodec4::class)
class Test4

object TestCodec4 : ATestCodec4(), Whatever

interface Whatever

abstract class ATestCodec4 : MessageCodec<Test4> {
    override fun encode(value: Test4, config: CodecConfig?): Source {
        error("Not implemented")
    }

    override fun decode(source: Source, config: CodecConfig?): Test4 {
        error("Not implemented")
    }
}

@WithCodec(<!CODEC_TYPE_MISMATCH!>TestCodec4::class<!>)
class Test5

interface CustomCodec1<A> : MessageCodec<A>
interface CustomCodec2<B> : MessageCodec<B>
interface CustomCodec3<C> : CustomCodec2<C>
interface CustomCodec4<D> : CustomCodec3<D>, CustomCodec1<D>

@WithCodec(TestCodec8::class)
class Test8

object TestCodec8 : CustomCodec4<Test8> {
    override fun encode(value: Test8, config: CodecConfig?): Source {
        error("Not implemented")
    }

    override fun decode(source: Source, config: CodecConfig?): Test8 {
        error("Not implemented")
    }
}

@WithCodec(<!CODEC_TYPE_MISMATCH!>TestCodec9::class<!>)
class Test9

object TestCodec9 : CustomCodec4<Test8> {
    override fun encode(value: Test8, config: CodecConfig?): Source {
        error("Not implemented")
    }

    override fun decode(source: Source, config: CodecConfig?): Test8 {
        error("Not implemented")
    }
}

class Test10
val testCodec10 = codec<<!CHECKED_ANNOTATION_VIOLATION!>Test10<!>>()

val testCodec = codec<Test>()

/* GENERATED_FIR_TAGS: annotationUseSiteTargetFile, classDeclaration, classReference, functionDeclaration,
interfaceDeclaration, nullableType, objectDeclaration, override, stringLiteral, typeParameter */
