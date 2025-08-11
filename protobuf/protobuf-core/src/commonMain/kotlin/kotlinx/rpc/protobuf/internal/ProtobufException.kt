/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

public sealed class ProtobufException : RuntimeException {
    protected constructor(message: String, cause: Throwable? = null) : super(message, cause)
}


public class ProtobufDecodingException : ProtobufException {
    public constructor(message: String, cause: Throwable? = null) : super(message, cause)

    public companion object Companion {
        internal fun missingRequiredField(messageName: String, fieldName: String) =
            ProtobufDecodingException("Message '$messageName' is missing a required field: $fieldName")

        internal fun negativeSize() = ProtobufDecodingException(
            "Decoder encountered an embedded string or message which claimed to have negative size."
        )

        internal fun invalidTag() = ProtobufDecodingException(
            "Protocol message contained an invalid tag (zero)."
        )

        internal fun truncatedMessage() = ProtobufDecodingException(
            ("While parsing a protocol message, the input ended unexpectedly "
                    + "in the middle of a field.  This could mean either that the "
                    + "input has been truncated or that an embedded message "
                    + "misreported its own length.")
        )

        internal fun genericParsingError() = ProtobufDecodingException("Failed to parse the message.")
    }
}

public class ProtobufEncodingException : ProtobufException {
    public constructor(message: String, cause: Throwable? = null) : super(message, cause)
}
