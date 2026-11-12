/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.jvm.JvmInline

/** A protobuf field number and wire type packed into a 32-bit value. */
@InternalRpcApi
@JvmInline
public value class WireTag(public val raw: Int)
