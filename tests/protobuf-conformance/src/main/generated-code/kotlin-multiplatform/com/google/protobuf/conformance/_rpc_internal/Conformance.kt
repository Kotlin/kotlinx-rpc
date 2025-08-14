@file:OptIn(ExperimentalRpcApi::class, kotlinx.rpc.internal.utils.InternalRpcApi::class)
package com.google.protobuf.conformance

import kotlinx.rpc.internal.utils.*
import kotlinx.rpc.protobuf.input.stream.asInputStream
import kotlinx.rpc.protobuf.internal.*

class TestStatusInternal: com.google.protobuf.conformance.TestStatus, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    override var name: String by MsgFieldDelegate { "" }
    override var failureMessage: String by MsgFieldDelegate { "" }
    override var matchedName: String by MsgFieldDelegate { "" }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.conformance.TestStatus> { 
        override fun encode(value: com.google.protobuf.conformance.TestStatus): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf.conformance.TestStatus { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf.conformance.TestStatusInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.conformance.TestStatusInternal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    companion object
}

class FailureSetInternal: com.google.protobuf.conformance.FailureSet, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    override var test: List<com.google.protobuf.conformance.TestStatus> by MsgFieldDelegate { mutableListOf() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.conformance.FailureSet> { 
        override fun encode(value: com.google.protobuf.conformance.FailureSet): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf.conformance.FailureSet { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf.conformance.FailureSetInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.conformance.FailureSetInternal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    companion object
}

class ConformanceRequestInternal: com.google.protobuf.conformance.ConformanceRequest, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 1) { 
    private object PresenceIndices { 
        const val jspbEncodingOptions: Int = 0
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    override var requestedOutputFormat: com.google.protobuf.conformance.WireFormat by MsgFieldDelegate { com.google.protobuf.conformance.WireFormat.UNSPECIFIED }
    override var messageType: String by MsgFieldDelegate { "" }
    override var testCategory: com.google.protobuf.conformance.TestCategory by MsgFieldDelegate { com.google.protobuf.conformance.TestCategory.UNSPECIFIED_TEST }
    override var jspbEncodingOptions: com.google.protobuf.conformance.JspbEncodingConfig by MsgFieldDelegate(PresenceIndices.jspbEncodingOptions) { com.google.protobuf.conformance.JspbEncodingConfigInternal() }
    override var printUnknownFields: Boolean by MsgFieldDelegate { false }
    override var payload: com.google.protobuf.conformance.ConformanceRequest.Payload? = null

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.conformance.ConformanceRequest> { 
        override fun encode(value: com.google.protobuf.conformance.ConformanceRequest): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf.conformance.ConformanceRequest { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf.conformance.ConformanceRequestInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.conformance.ConformanceRequestInternal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    companion object
}

class ConformanceResponseInternal: com.google.protobuf.conformance.ConformanceResponse, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    override var result: com.google.protobuf.conformance.ConformanceResponse.Result? = null

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.conformance.ConformanceResponse> { 
        override fun encode(value: com.google.protobuf.conformance.ConformanceResponse): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf.conformance.ConformanceResponse { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf.conformance.ConformanceResponseInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.conformance.ConformanceResponseInternal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    companion object
}

class JspbEncodingConfigInternal: com.google.protobuf.conformance.JspbEncodingConfig, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    override var useJspbArrayAnyFormat: Boolean by MsgFieldDelegate { false }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.conformance.JspbEncodingConfig> { 
        override fun encode(value: com.google.protobuf.conformance.JspbEncodingConfig): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf.conformance.JspbEncodingConfig { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf.conformance.JspbEncodingConfigInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.conformance.JspbEncodingConfigInternal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    companion object
}

operator fun com.google.protobuf.conformance.TestStatus.Companion.invoke(body: com.google.protobuf.conformance.TestStatusInternal.() -> Unit): com.google.protobuf.conformance.TestStatus { 
    val msg = com.google.protobuf.conformance.TestStatusInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf.conformance.FailureSet.Companion.invoke(body: com.google.protobuf.conformance.FailureSetInternal.() -> Unit): com.google.protobuf.conformance.FailureSet { 
    val msg = com.google.protobuf.conformance.FailureSetInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf.conformance.ConformanceRequest.Companion.invoke(body: com.google.protobuf.conformance.ConformanceRequestInternal.() -> Unit): com.google.protobuf.conformance.ConformanceRequest { 
    val msg = com.google.protobuf.conformance.ConformanceRequestInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf.conformance.ConformanceResponse.Companion.invoke(body: com.google.protobuf.conformance.ConformanceResponseInternal.() -> Unit): com.google.protobuf.conformance.ConformanceResponse { 
    val msg = com.google.protobuf.conformance.ConformanceResponseInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

operator fun com.google.protobuf.conformance.JspbEncodingConfig.Companion.invoke(body: com.google.protobuf.conformance.JspbEncodingConfigInternal.() -> Unit): com.google.protobuf.conformance.JspbEncodingConfig { 
    val msg = com.google.protobuf.conformance.JspbEncodingConfigInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf.conformance.TestStatusInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf.conformance.TestStatusInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (name.isNotEmpty()) { 
        encoder.writeString(fieldNr = 1, value = name)
    }

    if (failureMessage.isNotEmpty()) { 
        encoder.writeString(fieldNr = 2, value = failureMessage)
    }

    if (matchedName.isNotEmpty()) { 
        encoder.writeString(fieldNr = 3, value = matchedName)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf.conformance.TestStatusInternal.Companion.decodeWith(msg: com.google.protobuf.conformance.TestStatusInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.name = decoder.readString()
            }

            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.failureMessage = decoder.readString()
            }

            tag.fieldNr == 3 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.matchedName = decoder.readString()
            }

            else -> { 
                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag.wireType)
            }
        }
    }
}

private fun com.google.protobuf.conformance.TestStatusInternal.computeSize(): Int { 
    var __result = 0
    if (name.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(name).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (failureMessage.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(failureMessage).let { kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (matchedName.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(matchedName).let { kotlinx.rpc.protobuf.internal.WireSize.tag(3, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf.conformance.TestStatus.asInternal(): com.google.protobuf.conformance.TestStatusInternal { 
    return this as? com.google.protobuf.conformance.TestStatusInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf.conformance.FailureSetInternal.checkRequiredFields() { 
    // no required fields to check
    test.forEach { 
        it.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf.conformance.FailureSetInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (test.isNotEmpty()) { 
        test.forEach { 
            encoder.writeMessage(fieldNr = 2, value = it.asInternal()) { encodeWith(it) }
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf.conformance.FailureSetInternal.Companion.decodeWith(msg: com.google.protobuf.conformance.FailureSetInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.conformance.TestStatusInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf.conformance.TestStatusInternal::decodeWith)
                (msg.test as MutableList).add(elem)
            }

            else -> { 
                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag.wireType)
            }
        }
    }
}

private fun com.google.protobuf.conformance.FailureSetInternal.computeSize(): Int { 
    var __result = 0
    if (test.isNotEmpty()) { 
        __result = test.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf.conformance.FailureSet.asInternal(): com.google.protobuf.conformance.FailureSetInternal { 
    return this as? com.google.protobuf.conformance.FailureSetInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf.conformance.ConformanceRequestInternal.checkRequiredFields() { 
    // no required fields to check
    if (presenceMask[0]) { 
        jspbEncodingOptions.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf.conformance.ConformanceRequestInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (com.google.protobuf.conformance.WireFormat.UNSPECIFIED != requestedOutputFormat) { 
        encoder.writeEnum(fieldNr = 3, value = requestedOutputFormat.number)
    }

    if (messageType.isNotEmpty()) { 
        encoder.writeString(fieldNr = 4, value = messageType)
    }

    if (com.google.protobuf.conformance.TestCategory.UNSPECIFIED_TEST != testCategory) { 
        encoder.writeEnum(fieldNr = 5, value = testCategory.number)
    }

    if (presenceMask[0]) { 
        encoder.writeMessage(fieldNr = 6, value = jspbEncodingOptions.asInternal()) { encodeWith(it) }
    }

    if (printUnknownFields != false) { 
        encoder.writeBool(fieldNr = 9, value = printUnknownFields)
    }

    payload?.also { 
        when (val value = it) { 
            is com.google.protobuf.conformance.ConformanceRequest.Payload.ProtobufPayload -> { 
                encoder.writeBytes(fieldNr = 1, value = value.value)
            }

            is com.google.protobuf.conformance.ConformanceRequest.Payload.JsonPayload -> { 
                encoder.writeString(fieldNr = 2, value = value.value)
            }

            is com.google.protobuf.conformance.ConformanceRequest.Payload.JspbPayload -> { 
                encoder.writeString(fieldNr = 7, value = value.value)
            }

            is com.google.protobuf.conformance.ConformanceRequest.Payload.TextPayload -> { 
                encoder.writeString(fieldNr = 8, value = value.value)
            }
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf.conformance.ConformanceRequestInternal.Companion.decodeWith(msg: com.google.protobuf.conformance.ConformanceRequestInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 3 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.requestedOutputFormat = com.google.protobuf.conformance.WireFormat.fromNumber(decoder.readEnum())
            }

            tag.fieldNr == 4 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.messageType = decoder.readString()
            }

            tag.fieldNr == 5 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.testCategory = com.google.protobuf.conformance.TestCategory.fromNumber(decoder.readEnum())
            }

            tag.fieldNr == 6 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[0]) { 
                    msg.jspbEncodingOptions = com.google.protobuf.conformance.JspbEncodingConfigInternal()
                }

                decoder.readMessage(msg.jspbEncodingOptions.asInternal(), com.google.protobuf.conformance.JspbEncodingConfigInternal::decodeWith)
            }

            tag.fieldNr == 9 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.printUnknownFields = decoder.readBool()
            }

            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.payload = com.google.protobuf.conformance.ConformanceRequest.Payload.ProtobufPayload(decoder.readBytes())
            }

            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.payload = com.google.protobuf.conformance.ConformanceRequest.Payload.JsonPayload(decoder.readString())
            }

            tag.fieldNr == 7 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.payload = com.google.protobuf.conformance.ConformanceRequest.Payload.JspbPayload(decoder.readString())
            }

            tag.fieldNr == 8 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.payload = com.google.protobuf.conformance.ConformanceRequest.Payload.TextPayload(decoder.readString())
            }

            else -> { 
                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag.wireType)
            }
        }
    }
}

private fun com.google.protobuf.conformance.ConformanceRequestInternal.computeSize(): Int { 
    var __result = 0
    if (com.google.protobuf.conformance.WireFormat.UNSPECIFIED != requestedOutputFormat) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(3, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.enum(requestedOutputFormat.number))
    }

    if (messageType.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(messageType).let { kotlinx.rpc.protobuf.internal.WireSize.tag(4, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (com.google.protobuf.conformance.TestCategory.UNSPECIFIED_TEST != testCategory) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(5, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.enum(testCategory.number))
    }

    if (presenceMask[0]) { 
        __result += jspbEncodingOptions.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(6, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (printUnknownFields != false) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(9, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.bool(printUnknownFields))
    }

    payload?.also { 
        when (val value = it) { 
            is com.google.protobuf.conformance.ConformanceRequest.Payload.ProtobufPayload -> { 
                __result += kotlinx.rpc.protobuf.internal.WireSize.bytes(value.value).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }

            is com.google.protobuf.conformance.ConformanceRequest.Payload.JsonPayload -> { 
                __result += kotlinx.rpc.protobuf.internal.WireSize.string(value.value).let { kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }

            is com.google.protobuf.conformance.ConformanceRequest.Payload.JspbPayload -> { 
                __result += kotlinx.rpc.protobuf.internal.WireSize.string(value.value).let { kotlinx.rpc.protobuf.internal.WireSize.tag(7, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }

            is com.google.protobuf.conformance.ConformanceRequest.Payload.TextPayload -> { 
                __result += kotlinx.rpc.protobuf.internal.WireSize.string(value.value).let { kotlinx.rpc.protobuf.internal.WireSize.tag(8, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }
        }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf.conformance.ConformanceRequest.asInternal(): com.google.protobuf.conformance.ConformanceRequestInternal { 
    return this as? com.google.protobuf.conformance.ConformanceRequestInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf.conformance.ConformanceResponseInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf.conformance.ConformanceResponseInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    result?.also { 
        when (val value = it) { 
            is com.google.protobuf.conformance.ConformanceResponse.Result.ParseError -> { 
                encoder.writeString(fieldNr = 1, value = value.value)
            }

            is com.google.protobuf.conformance.ConformanceResponse.Result.SerializeError -> { 
                encoder.writeString(fieldNr = 6, value = value.value)
            }

            is com.google.protobuf.conformance.ConformanceResponse.Result.TimeoutError -> { 
                encoder.writeString(fieldNr = 9, value = value.value)
            }

            is com.google.protobuf.conformance.ConformanceResponse.Result.RuntimeError -> { 
                encoder.writeString(fieldNr = 2, value = value.value)
            }

            is com.google.protobuf.conformance.ConformanceResponse.Result.ProtobufPayload -> { 
                encoder.writeBytes(fieldNr = 3, value = value.value)
            }

            is com.google.protobuf.conformance.ConformanceResponse.Result.JsonPayload -> { 
                encoder.writeString(fieldNr = 4, value = value.value)
            }

            is com.google.protobuf.conformance.ConformanceResponse.Result.Skipped -> { 
                encoder.writeString(fieldNr = 5, value = value.value)
            }

            is com.google.protobuf.conformance.ConformanceResponse.Result.JspbPayload -> { 
                encoder.writeString(fieldNr = 7, value = value.value)
            }

            is com.google.protobuf.conformance.ConformanceResponse.Result.TextPayload -> { 
                encoder.writeString(fieldNr = 8, value = value.value)
            }
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf.conformance.ConformanceResponseInternal.Companion.decodeWith(msg: com.google.protobuf.conformance.ConformanceResponseInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.result = com.google.protobuf.conformance.ConformanceResponse.Result.ParseError(decoder.readString())
            }

            tag.fieldNr == 6 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.result = com.google.protobuf.conformance.ConformanceResponse.Result.SerializeError(decoder.readString())
            }

            tag.fieldNr == 9 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.result = com.google.protobuf.conformance.ConformanceResponse.Result.TimeoutError(decoder.readString())
            }

            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.result = com.google.protobuf.conformance.ConformanceResponse.Result.RuntimeError(decoder.readString())
            }

            tag.fieldNr == 3 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.result = com.google.protobuf.conformance.ConformanceResponse.Result.ProtobufPayload(decoder.readBytes())
            }

            tag.fieldNr == 4 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.result = com.google.protobuf.conformance.ConformanceResponse.Result.JsonPayload(decoder.readString())
            }

            tag.fieldNr == 5 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.result = com.google.protobuf.conformance.ConformanceResponse.Result.Skipped(decoder.readString())
            }

            tag.fieldNr == 7 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.result = com.google.protobuf.conformance.ConformanceResponse.Result.JspbPayload(decoder.readString())
            }

            tag.fieldNr == 8 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.result = com.google.protobuf.conformance.ConformanceResponse.Result.TextPayload(decoder.readString())
            }

            else -> { 
                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag.wireType)
            }
        }
    }
}

private fun com.google.protobuf.conformance.ConformanceResponseInternal.computeSize(): Int { 
    var __result = 0
    result?.also { 
        when (val value = it) { 
            is com.google.protobuf.conformance.ConformanceResponse.Result.ParseError -> { 
                __result += kotlinx.rpc.protobuf.internal.WireSize.string(value.value).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }

            is com.google.protobuf.conformance.ConformanceResponse.Result.SerializeError -> { 
                __result += kotlinx.rpc.protobuf.internal.WireSize.string(value.value).let { kotlinx.rpc.protobuf.internal.WireSize.tag(6, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }

            is com.google.protobuf.conformance.ConformanceResponse.Result.TimeoutError -> { 
                __result += kotlinx.rpc.protobuf.internal.WireSize.string(value.value).let { kotlinx.rpc.protobuf.internal.WireSize.tag(9, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }

            is com.google.protobuf.conformance.ConformanceResponse.Result.RuntimeError -> { 
                __result += kotlinx.rpc.protobuf.internal.WireSize.string(value.value).let { kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }

            is com.google.protobuf.conformance.ConformanceResponse.Result.ProtobufPayload -> { 
                __result += kotlinx.rpc.protobuf.internal.WireSize.bytes(value.value).let { kotlinx.rpc.protobuf.internal.WireSize.tag(3, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }

            is com.google.protobuf.conformance.ConformanceResponse.Result.JsonPayload -> { 
                __result += kotlinx.rpc.protobuf.internal.WireSize.string(value.value).let { kotlinx.rpc.protobuf.internal.WireSize.tag(4, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }

            is com.google.protobuf.conformance.ConformanceResponse.Result.Skipped -> { 
                __result += kotlinx.rpc.protobuf.internal.WireSize.string(value.value).let { kotlinx.rpc.protobuf.internal.WireSize.tag(5, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }

            is com.google.protobuf.conformance.ConformanceResponse.Result.JspbPayload -> { 
                __result += kotlinx.rpc.protobuf.internal.WireSize.string(value.value).let { kotlinx.rpc.protobuf.internal.WireSize.tag(7, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }

            is com.google.protobuf.conformance.ConformanceResponse.Result.TextPayload -> { 
                __result += kotlinx.rpc.protobuf.internal.WireSize.string(value.value).let { kotlinx.rpc.protobuf.internal.WireSize.tag(8, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }
        }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf.conformance.ConformanceResponse.asInternal(): com.google.protobuf.conformance.ConformanceResponseInternal { 
    return this as? com.google.protobuf.conformance.ConformanceResponseInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf.conformance.JspbEncodingConfigInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf.conformance.JspbEncodingConfigInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (useJspbArrayAnyFormat != false) { 
        encoder.writeBool(fieldNr = 1, value = useJspbArrayAnyFormat)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf.conformance.JspbEncodingConfigInternal.Companion.decodeWith(msg: com.google.protobuf.conformance.JspbEncodingConfigInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.useJspbArrayAnyFormat = decoder.readBool()
            }

            else -> { 
                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag.wireType)
            }
        }
    }
}

private fun com.google.protobuf.conformance.JspbEncodingConfigInternal.computeSize(): Int { 
    var __result = 0
    if (useJspbArrayAnyFormat != false) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.bool(useJspbArrayAnyFormat))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf.conformance.JspbEncodingConfig.asInternal(): com.google.protobuf.conformance.JspbEncodingConfigInternal { 
    return this as? com.google.protobuf.conformance.JspbEncodingConfigInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf.conformance.WireFormat.Companion.fromNumber(number: Int): com.google.protobuf.conformance.WireFormat { 
    return when (number) { 
        0 -> { 
            com.google.protobuf.conformance.WireFormat.UNSPECIFIED
        }

        1 -> { 
            com.google.protobuf.conformance.WireFormat.PROTOBUF
        }

        2 -> { 
            com.google.protobuf.conformance.WireFormat.JSON
        }

        3 -> { 
            com.google.protobuf.conformance.WireFormat.JSPB
        }

        4 -> { 
            com.google.protobuf.conformance.WireFormat.TEXT_FORMAT
        }

        else -> { 
            com.google.protobuf.conformance.WireFormat.UNRECOGNIZED(number)
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
fun com.google.protobuf.conformance.TestCategory.Companion.fromNumber(number: Int): com.google.protobuf.conformance.TestCategory { 
    return when (number) { 
        0 -> { 
            com.google.protobuf.conformance.TestCategory.UNSPECIFIED_TEST
        }

        1 -> { 
            com.google.protobuf.conformance.TestCategory.BINARY_TEST
        }

        2 -> { 
            com.google.protobuf.conformance.TestCategory.JSON_TEST
        }

        3 -> { 
            com.google.protobuf.conformance.TestCategory.JSON_IGNORE_UNKNOWN_PARSING_TEST
        }

        4 -> { 
            com.google.protobuf.conformance.TestCategory.JSPB_TEST
        }

        5 -> { 
            com.google.protobuf.conformance.TestCategory.TEXT_FORMAT_TEST
        }

        else -> { 
            com.google.protobuf.conformance.TestCategory.UNRECOGNIZED(number)
        }
    }
}

