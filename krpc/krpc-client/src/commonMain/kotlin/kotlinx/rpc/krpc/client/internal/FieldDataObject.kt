/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.client.internal

import kotlinx.rpc.internal.RPCMethodClassArguments
import kotlinx.serialization.Serializable

/**
 * Used for field initialization call
 */
@Serializable
internal object FieldDataObject : RPCMethodClassArguments {
    override fun asArray(): Array<Any?> = emptyArray()
}
