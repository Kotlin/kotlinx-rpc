/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.jvm.JvmInline

/**
 * A protobuf field number and wire type packed into a 32-bit value. The raw value 0 marks the end of a message.
 *
 * [WireDecoder.readTag] validates tags. Direct construction doesn't validate [raw].
 */
@InternalRpcApi
@JvmInline
public value class WireTag(public val raw: Int) {
    /** Constructs a tag from [fieldNr] and a [WireType] value. */
    public constructor(fieldNr: Int, wireType: Int) : this((fieldNr shl TYPE_BITS) or wireType)

    /** The field number, or 0 for the end marker. */
    public val fieldNr: Int get() = raw ushr TYPE_BITS

    /** Whether this tag marks the end of a message. */
    public val isEnd: Boolean get() = raw == 0

    /** The encoding of the field value. Use only for a non-end tag. */
    public val wireType: Int get() = raw and TYPE_MASK

    public companion object {
        /** The marker returned at the end of a message. */
        public inline val END: WireTag get() = WireTag(0)

        /** The number of low bits that store the wire type. */
        public const val TYPE_BITS: Int = 3

        /** The mask for the wire type bits. */
        public const val TYPE_MASK: Int = 7
    }
}
