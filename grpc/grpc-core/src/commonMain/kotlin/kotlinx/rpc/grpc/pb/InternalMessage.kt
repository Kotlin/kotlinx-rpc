/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.pb

import kotlinx.rpc.grpc.utils.BitSet
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@InternalRpcApi
public abstract class InternalMessage(fieldsWithPresence: Int) {
    public val presenceMask: BitSet = BitSet(fieldsWithPresence)
    public abstract val _size: Int
}

public class MsgFieldDelegate<T : Any>(
    private val presenceIdx: Int? = null,
    private val defaultProvider: (() -> T)? = null
) : ReadWriteProperty<InternalMessage, T> {

    private var valueSet = false
    private var _value: T? = null

    override operator fun getValue(thisRef: InternalMessage, property: KProperty<*>): T {
        if (!valueSet) {
            if (defaultProvider != null) {
                _value = defaultProvider.invoke()
                valueSet = true
            } else {
                error("Property ${property.name} not initialized")
            }
        }
        @Suppress("UNCHECKED_CAST")
        return _value as T
    }

    override operator fun setValue(thisRef: InternalMessage, property: KProperty<*>, new: T) {
        presenceIdx?.let { thisRef.presenceMask[it] = true }
        _value = new
        valueSet = true
    }
}