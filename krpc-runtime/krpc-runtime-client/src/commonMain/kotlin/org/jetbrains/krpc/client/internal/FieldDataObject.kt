/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.client.internal

import kotlinx.serialization.Serializable
import org.jetbrains.krpc.internal.RPCMethodClassArguments

/**
 * Used for field initialization call
 */
@Serializable
internal object FieldDataObject : RPCMethodClassArguments {
    override fun asArray(): Array<Any?> = emptyArray()
}
