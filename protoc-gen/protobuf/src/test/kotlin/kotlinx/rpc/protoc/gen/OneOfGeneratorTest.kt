/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen

import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Type
import com.google.protobuf.DescriptorProtos.FileDescriptorProto
import kotlinx.rpc.protoc.gen.core.Config
import kotlinx.rpc.protoc.gen.core.GeneratedMetadata
import kotlinx.rpc.protoc.gen.core.Platform
import kotlinx.rpc.protoc.gen.fixture.proto.protobufProto
import kotlinx.rpc.protoc.gen.fixture.proto.toGeneratorModel
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

/**
 * Generator output for `oneof` declarations: flat member properties, the top-level case enum,
 * the case/clear/when extensions, the presence getters and the slot-backed internal accessors.
 */
class OneOfGeneratorTest {
    private fun config(
        generateOneOfWhenFunctions: Boolean = true,
        generateOptionalFieldOrNullGetters: Boolean = false,
        camelCaseNames: Boolean = true,
    ) = Config(
        explicitApiModeEnabled = false,
        generateComments = false,
        generateFileLevelComments = false,
        generateOptionalFieldOrNullGetters = generateOptionalFieldOrNullGetters,
        generateOneOfWhenFunctions = generateOneOfWhenFunctions,
        indentSize = 4,
        platform = Platform.Jvm,
        protoNamesOutput = null,
        camelCaseNames = camelCaseNames,
    )

    private class Generated(val public: String, val ext: String, val internal: String)

    /** Trims the margin and indents the block by one level, keeping blank lines empty. */
    private fun String.indented(levels: Int = 1): String = trimIndent().lines().joinToString("\n") { line ->
        if (line.isBlank()) line else "    ".repeat(levels) + line
    }

    private fun FileDescriptorProto.generate(config: Config): Generated {
        val files = ModelToProtobufKotlinCommonGenerator(
            config = config,
            generatedMetadata = GeneratedMetadata(),
            model = toGeneratorModel(config),
        ).generateKotlinFiles()

        return Generated(
            public = files[0].build(),
            ext = files[1].build(),
            internal = files[2].build(),
        )
    }

    private fun mixedEvent() = protobufProto {
        message("inner") { field("a", Type.TYPE_INT32) }
        enumType("level", "LEVEL_UNSPECIFIED", "LEVEL_HIGH")
        message("event") {
            oneOf("payload") {
                field("text")
                field("code", Type.TYPE_INT32)
                field("ratio", Type.TYPE_DOUBLE)
                messageField("inner", "inner")
                enumField("level", "level")
            }
        }
    }

    @Test
    fun `mixed oneof - public interface has flat non-nullable members and nothing else`() {
        val generated = mixedEvent().generate(config())

        assertContains(
            generated.public,
            """
            @GeneratedProtoMessage
            interface Event {
                val text: String
                val code: Int
                val ratio: Double
                val inner: Inner
                val level: Level
            }
            """.trimIndent(),
        )
        assertFalse(generated.public.contains("sealed interface"))
        assertFalse(generated.public.contains("Payload"))
    }

    @Test
    fun `mixed oneof - ext file has case enum, case property, clear and when function`() {
        val generated = mixedEvent().generate(config())

        assertContains(
            generated.ext,
            """
            enum class EventPayloadCase {
                TEXT,
                CODE,
                RATIO,
                INNER,
                LEVEL,
                NOT_SET,
            }
            """.trimIndent(),
        )
        assertContains(generated.ext, "val Event.payload: EventPayloadCase get() = this.asInternal()._payloadCase")
        assertContains(
            generated.ext,
            """
            fun Event.Builder.clearPayload() {
                this.asInternal().clearPayloadInternal()
            }
            """.trimIndent(),
        )
        assertContains(
            generated.ext,
            """
            inline fun <R> Event.whenPayload(
                text: (String) -> R,
                code: (Int) -> R,
                ratio: (Double) -> R,
                inner: (Inner) -> R,
                level: (Level) -> R,
                notSet: () -> R,
            ): R {
                return when (this.payload) {
                    EventPayloadCase.TEXT -> text(this.text)
                    EventPayloadCase.CODE -> code(this.code)
                    EventPayloadCase.RATIO -> ratio(this.ratio)
                    EventPayloadCase.INNER -> inner(this.inner)
                    EventPayloadCase.LEVEL -> level(this.level)
                    EventPayloadCase.NOT_SET -> notSet()
                }
            }
            """.trimIndent(),
        )
        assertContains(
            generated.ext,
            """
            interface EventPresence {
                val hasText: Boolean

                val hasCode: Boolean

                val hasRatio: Boolean

                val hasInner: Boolean

                val hasLevel: Boolean
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `mixed oneof - internal class uses a reference slot and a 64-bit numeric slot`() {
        val generated = mixedEvent().generate(config())

        assertContains(generated.internal, "InternalMessage(fieldsWithPresence = 5)")
        assertContains(
            generated.internal,
            """
                private var _payloadRef: Any? = null
                private var _payloadNum: Long = 0L

                @InternalRpcApi
                val _payloadCase: EventPayloadCase get() = when {
                    presenceMask[PresenceIndices.text] -> EventPayloadCase.TEXT
                    presenceMask[PresenceIndices.code] -> EventPayloadCase.CODE
                    presenceMask[PresenceIndices.ratio] -> EventPayloadCase.RATIO
                    presenceMask[PresenceIndices.inner] -> EventPayloadCase.INNER
                    presenceMask[PresenceIndices.level] -> EventPayloadCase.LEVEL
                    else -> EventPayloadCase.NOT_SET
                }

                @InternalRpcApi
                fun clearPayloadInternal() {
                    presenceMask.clearRange(PresenceIndices.text, PresenceIndices.level)
                    _payloadRef = null
                    _payloadNum = 0L
                }
            """.indented(),
        )
        assertContains(
            generated.internal,
            """
                override var text: String
                    get() = if (presenceMask[PresenceIndices.text]) (_payloadRef as String) else ""
                    set(value) { presenceMask.setExclusive(PresenceIndices.text, PresenceIndices.text, PresenceIndices.level); _payloadRef = value; _payloadNum = 0L }

                override fun clearText() {
                    if (presenceMask[PresenceIndices.text]) clearPayloadInternal()
                }
            """.indented(),
        )
        assertContains(
            generated.internal,
            """
                override var code: Int
                    get() = if (presenceMask[PresenceIndices.code]) _payloadNum.toInt() else 0
                    set(value) { presenceMask.setExclusive(PresenceIndices.code, PresenceIndices.text, PresenceIndices.level); _payloadNum = value.toLong(); _payloadRef = null }
            """.indented(),
        )
        assertContains(
            generated.internal,
            """
                override var ratio: Double
                    get() = if (presenceMask[PresenceIndices.ratio]) Double.fromBits(_payloadNum) else 0.0
                    set(value) { presenceMask.setExclusive(PresenceIndices.ratio, PresenceIndices.text, PresenceIndices.level); _payloadNum = value.toRawBits(); _payloadRef = null }
            """.indented(),
        )
        assertContains(
            generated.internal,
            """
                override var inner: Inner
                    get() = if (presenceMask[PresenceIndices.inner]) (_payloadRef as Inner) else InnerInternal.DEFAULT
            """.indented(),
        )
        assertContains(
            generated.internal,
            """
                override var level: Level
                    get() = if (presenceMask[PresenceIndices.level]) Level.fromNumber(_payloadNum.toInt()) else Level.UNSPECIFIED
                    set(value) { presenceMask.setExclusive(PresenceIndices.level, PresenceIndices.text, PresenceIndices.level); _payloadNum = value.number.toLong(); _payloadRef = null }
            """.indented(),
        )
    }

    @Test
    fun `mixed oneof - hashCode uses field number of the active member, toString prints only the active member`() {
        val generated = mixedEvent().generate(config())

        assertContains(
            generated.internal,
            """
                override fun hashCode(): Int {
                    var result = when {
                        presenceMask[PresenceIndices.text] -> 1 * 31 + this.text.hashCode()
                        presenceMask[PresenceIndices.code] -> 2 * 31 + this.code.hashCode()
                        presenceMask[PresenceIndices.ratio] -> 3 * 31 + this.ratio.toBits().hashCode()
                        presenceMask[PresenceIndices.inner] -> 4 * 31 + this.inner.hashCode()
                        presenceMask[PresenceIndices.level] -> 5 * 31 + this.level.hashCode()
                        else -> 0
                    }

                    return result
                }
            """.indented(),
        )
        assertContains(
            generated.internal,
            """
                    if (presenceMask[PresenceIndices.text]) {
                        builder.appendLine("${'$'}{nextIndentString}text=${'$'}{this.text},")
                    }
            """.indented(levels = 2),
        )
        assertFalse(generated.internal.contains("<unset>"))
    }

    @Test
    fun `mixed oneof - message members merge on decode, scalars assign through the setter`() {
        val generated = mixedEvent().generate(config())

        assertContains(generated.internal, "msg.text = decoder.readString()")
        assertContains(generated.internal, "msg.level = Level.fromNumber(decoder.readEnum())")
        assertContains(
            generated.internal,
            "val target = if (msg.presenceMask[EventInternal.PresenceIndices.inner]) msg.inner.asInternal() else InnerInternal().also { msg.inner = it }",
        )
        assertContains(
            generated.internal,
            """
                if (presenceMask[EventInternal.PresenceIndices.inner]) {
                    encoder.writeMessage(fieldNr = 4, value = this.inner.asInternal()) { encoder -> encodeWith(encoder, config) }
                }
            """.indented(),
        )
    }

    @Test
    fun `numeric-only 32-bit oneof has a single Int slot`() {
        val generated = protobufProto {
            message("numbers") {
                oneOf("value") {
                    field("i", Type.TYPE_INT32)
                    field("u", Type.TYPE_UINT32)
                    field("f", Type.TYPE_FLOAT)
                    field("b", Type.TYPE_BOOL)
                }
            }
        }.generate(config())

        assertContains(generated.internal, "private var _valueNum: Int = 0")
        assertFalse(generated.internal.contains("_valueRef"))
        assertContains(
            generated.internal,
            """
                override var i: Int
                    get() = if (presenceMask[PresenceIndices.i]) _valueNum else 0
                    set(value) { presenceMask.setExclusive(PresenceIndices.i, PresenceIndices.i, PresenceIndices.b); _valueNum = value }
            """.indented(),
        )
        assertContains(
            generated.internal,
            """
                override var u: UInt
                    get() = if (presenceMask[PresenceIndices.u]) _valueNum.toUInt() else 0u
                    set(value) { presenceMask.setExclusive(PresenceIndices.u, PresenceIndices.i, PresenceIndices.b); _valueNum = value.toInt() }
            """.indented(),
        )
        assertContains(
            generated.internal,
            """
                override var f: Float
                    get() = if (presenceMask[PresenceIndices.f]) Float.fromBits(_valueNum) else 0.0f
                    set(value) { presenceMask.setExclusive(PresenceIndices.f, PresenceIndices.i, PresenceIndices.b); _valueNum = value.toRawBits() }
            """.indented(),
        )
        assertContains(
            generated.internal,
            """
                override var b: Boolean
                    get() = if (presenceMask[PresenceIndices.b]) (_valueNum != 0) else false
                    set(value) { presenceMask.setExclusive(PresenceIndices.b, PresenceIndices.i, PresenceIndices.b); _valueNum = if (value) 1 else 0 }
            """.indented(),
        )
        assertContains(
            generated.internal,
            """
                fun clearValueInternal() {
                    presenceMask.clearRange(PresenceIndices.i, PresenceIndices.b)
                    _valueNum = 0
                }
            """.indented(),
        )
    }

    @Test
    fun `64-bit numeric members widen the numeric slot to Long`() {
        val generated = protobufProto {
            message("numbers") {
                oneOf("value") {
                    field("u32", Type.TYPE_FIXED32)
                    field("u64", Type.TYPE_UINT64)
                    field("f", Type.TYPE_FLOAT)
                }
            }
        }.generate(config())

        assertContains(generated.internal, "private var _valueNum: Long = 0L")
        assertContains(generated.internal, "get() = if (presenceMask[PresenceIndices.u32]) _valueNum.toUInt() else 0u")
        assertContains(generated.internal, "_valueNum = value.toLong() }")
        assertContains(generated.internal, "get() = if (presenceMask[PresenceIndices.u64]) _valueNum.toULong() else 0uL")
        assertContains(generated.internal, "get() = if (presenceMask[PresenceIndices.f]) Float.fromBits(_valueNum.toInt()) else 0.0f")
        assertContains(generated.internal, "_valueNum = value.toRawBits().toLong() }")
    }

    @Test
    fun `reference-only oneof has a single reference slot`() {
        val generated = protobufProto {
            message("inner") { field("a") }
            message("refs") {
                oneOf("value") {
                    field("s")
                    field("b", Type.TYPE_BYTES)
                    messageField("m", "inner")
                }
            }
        }.generate(config())

        assertContains(generated.internal, "private var _valueRef: Any? = null")
        assertFalse(generated.internal.contains("_valueNum"))
        assertContains(
            generated.internal,
            """
                override var s: String
                    get() = if (presenceMask[PresenceIndices.s]) (_valueRef as String) else ""
                    set(value) { presenceMask.setExclusive(PresenceIndices.s, PresenceIndices.s, PresenceIndices.m); _valueRef = value }
            """.indented(),
        )
        assertContains(
            generated.internal,
            "get() = if (presenceMask[PresenceIndices.b]) (_valueRef as ByteString) else ByteString()",
        )
        assertContains(
            generated.internal,
            """
                fun clearValueInternal() {
                    presenceMask.clearRange(PresenceIndices.s, PresenceIndices.m)
                    _valueRef = null
                }
            """.indented(),
        )
    }

    @Test
    fun `generateOneOfWhenFunctions=false omits the when function only`() {
        val generated = mixedEvent().generate(config(generateOneOfWhenFunctions = false))

        assertFalse(generated.ext.contains("whenPayload"))
        assertContains(generated.ext, "val Event.payload: EventPayloadCase")
        assertContains(generated.ext, "fun Event.Builder.clearPayload()")
        assertContains(generated.ext, "enum class EventPayloadCase")
    }

    @Test
    fun `generateOptionalFieldOrNullGetters emits orNull getters for oneof members`() {
        val generated = mixedEvent().generate(config(generateOptionalFieldOrNullGetters = true))

        assertContains(generated.ext, "val Event.textOrNull: String? get() = if (this.presence.hasText) this.text else null")
        assertContains(generated.ext, "val Event.codeOrNull: Int? get() = if (this.presence.hasCode) this.code else null")
        assertContains(generated.ext, "val Event.innerOrNull: Inner? get() = if (this.presence.hasInner) this.inner else null")
    }

    @Test
    fun `oneof presence bits are consecutive and interleave with regular presence fields in declaration order`() {
        val generated = protobufProto {
            message("msg") {
                field("first", Type.TYPE_INT32)
                oneOf("choice") {
                    field("a")
                    field("b", Type.TYPE_INT32)
                }
                field("last", Type.TYPE_INT32)
            }
        }.let { file ->
            // give the regular fields explicit presence (proto3 optional)
            val message = file.messageTypeList.single().toBuilder()
            message.fieldBuilderList.forEach { field ->
                if (!field.hasOneofIndex()) {
                    field.setProto3Optional(true)
                    field.setOneofIndex(message.oneofDeclCount)
                    message.addOneofDeclBuilder().setName("_${field.name}")
                }
            }
            file.toBuilder().clearMessageType().addMessageType(message).build()
        }.generate(config())

        assertContains(
            generated.internal,
            """
                internal object PresenceIndices {
                    const val first: Int = 0
                    const val a: Int = 1
                    const val b: Int = 2
                    const val last: Int = 3
                }
            """.indented(),
        )
        assertContains(generated.internal, "presenceMask.setExclusive(PresenceIndices.a, PresenceIndices.a, PresenceIndices.b)")
        // synthetic oneofs of proto3 optional fields get no case type
        assertFalse(generated.ext.contains("Msg_firstCase"))
        assertContains(generated.ext, "enum class MsgChoiceCase")
    }

    @Test
    fun `nested message case type name is flattened`() {
        val generated = protobufProto {
            message("outer") {
                nested("inner") {
                    oneOf("kind") {
                        field("a")
                        field("b")
                    }
                }
            }
        }.generate(config())

        assertContains(generated.ext, "enum class OuterInnerKindCase")
        assertContains(generated.ext, "val Outer.Inner.kind: OuterInnerKindCase get() = this.asInternal()._kindCase")
        assertContains(generated.ext, "fun Outer.Inner.Builder.clearKind()")
        assertContains(generated.ext, "inline fun <R> Outer.Inner.whenKind(")
    }

    @Test
    fun `member named not_set does not collide with the NOT_SET entry`() {
        val generated = protobufProto {
            message("msg") {
                oneOf("choice") {
                    field("not_set")
                    field("other")
                }
            }
        }.generate(config())

        assertContains(
            generated.ext,
            """
            enum class MsgChoiceCase {
                NOT_SET,
                OTHER,
                NOT_SET_,
            }
            """.trimIndent(),
        )
        assertContains(generated.ext, "notSet_: () -> R,")
        assertContains(generated.ext, "MsgChoiceCase.NOT_SET -> notSet(this.notSet)")
        assertContains(generated.ext, "MsgChoiceCase.NOT_SET_ -> notSet_()")
        assertContains(generated.internal, "else -> MsgChoiceCase.NOT_SET_")
    }

    @Test
    fun `keyword member and oneof names are escaped`() {
        val generated = protobufProto {
            message("msg") {
                oneOf("package") {
                    field("for", Type.TYPE_INT32)
                    field("when")
                }
            }
        }.generate(config())

        assertContains(generated.public, "val `for`: Int")
        assertContains(generated.ext, "val Msg.`package`: MsgPackageCase")
        assertContains(generated.ext, "fun Msg.Builder.clearPackage()")
        assertContains(generated.ext, "`for`: (Int) -> R,")
        assertContains(generated.ext, "MsgPackageCase.FOR -> `for`(this.`for`)")
        assertContains(generated.ext, "val hasFor: Boolean")
        assertContains(generated.internal, "override fun clearFor()")
    }

    @Test
    fun `camelCaseNames=false keeps proto names for members, case entries and the oneof`() {
        val generated = protobufProto {
            message("user_profile") {
                oneOf("contact_info") {
                    field("email_address")
                    field("phone_number")
                }
            }
        }.generate(config(camelCaseNames = false))

        assertContains(generated.public, "val email_address: String")
        assertContains(generated.ext, "enum class user_profilecontact_infoCase")
        assertContains(generated.ext, "EMAIL_ADDRESS,")
        assertContains(generated.ext, "val user_profile.contact_info: user_profilecontact_infoCase")
        assertContains(generated.ext, "fun user_profile.Builder.clearContact_info()")
        assertContains(generated.ext, "inline fun <R> user_profile.whenContact_info(")
    }
}
