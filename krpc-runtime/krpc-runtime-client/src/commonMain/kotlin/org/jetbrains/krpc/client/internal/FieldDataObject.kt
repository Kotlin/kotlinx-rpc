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
