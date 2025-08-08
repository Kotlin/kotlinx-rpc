/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// RUN_PIPELINE_TILL: FRONTEND

// MODULE: main

@file:OptIn(kotlinx.rpc.internal.utils.ExperimentalRpcApi::class)

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
    override fun encode(value: Test): Source {
        error("Not implemented")
    }

    override fun decode(stream: Source): Test {
        error("Not implemented")
    }
}

@WithCodec(<!NOT_AN_OBJECT_REFERENCE_IN_WITH_CODEC_ANNOTATION!>TestCodec3::class<!>)
class Test3

class TestCodec3 : MessageCodec<Test3> {
    override fun encode(value: Test3): Source {
        error("Not implemented")
    }

    override fun decode(stream: Source): Test3 {
        error("Not implemented")
    }
}

@WithCodec(TestCodec4::class)
class Test4

object TestCodec4 : ATestCodec4(), Whatever

interface Whatever

abstract class ATestCodec4 : MessageCodec<Test4> {
    override fun encode(value: Test4): Source {
        error("Not implemented")
    }

    override fun decode(stream: Source): Test4 {
        error("Not implemented")
    }
}

@WithCodec(<!CODEC_TYPE_MISMATCH!>TestCodec4::class<!>)
class Test5
