/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.codec.test

import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlinx.rpc.grpc.codec.CodecConfig
import kotlinx.rpc.grpc.codec.SourcedMessageCodec
import kotlinx.rpc.grpc.codec.WithCodec
import kotlinx.rpc.grpc.codec.codec
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals


@WithCodec(MyMessageCodec::class)
data class MyMessage(
    val value: String
)

class MyCodecConfig(
    val appendHello: Boolean
): CodecConfig

object MyMessageCodec: SourcedMessageCodec<MyMessage> {
    override fun encodeToSource(value: MyMessage, config: CodecConfig?): Source = Buffer().apply {
        val appendHello = (config as? MyCodecConfig)?.appendHello ?: false
        writeString(value.value + if (appendHello) "Hello" else "")
    }

    override fun decodeFromSource(stream: Source, config: CodecConfig?): MyMessage {
        return MyMessage(stream.readString())
    }
}


class CodecTest {

    @Test
    fun `test custom codec`() {
        val msg = MyMessage("test")
        val encoded = codec<MyMessage>(typeOf<MyMessage>()).encode(msg)
        val decoded = codec<MyMessage>().decode(encoded)
        assertEquals(msg, decoded)
    }

    @Test
    fun `test custom codec with default config`() {
        val msg = MyMessage("test")
        val config = MyCodecConfig(true)

        val firstEncoded = codec<MyMessage>(config).encode(msg)
        val secondEncoded = codec<MyMessage>().encode(msg)

        val firstDecoded = codec<MyMessage>().decode(firstEncoded)
        val secondDecoded = codec<MyMessage>().decode(secondEncoded)

        assertEquals(msg.value + "Hello", firstDecoded.value)
        assertEquals(msg, secondDecoded)
    }

    @Test
    fun `test custom codec with overwritten config`() {
        val msg = MyMessage("test")
        val config = MyCodecConfig(true)

        val codec = codec<MyMessage>(config)
        val firstEncoded = codec.encode(msg, MyCodecConfig(false))
        val secondEncoded = codec.encode(msg)

        val firstDecoded = codec.decode(firstEncoded)
        val secondDecoded = codec.decode(secondEncoded)

        assertEquals(msg.value, firstDecoded.value)
        assertEquals(msg.value + "Hello", secondDecoded.value)
    }

}