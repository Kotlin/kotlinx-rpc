package org.jetbrains.krpc.client

import kotlinx.serialization.Serializable
import org.jetbrains.krpc.RPCMethodClassArguments

/**
 * Used for field initialization call
 */
@Serializable
internal object FieldDataObject : RPCMethodClassArguments {
    override fun asArray(): Array<Any?> = emptyArray()
}
