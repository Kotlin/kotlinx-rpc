/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.io.Buffer
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@InternalRpcApi
public abstract class InternalMessage(fieldsWithPresence: Int) {
    public val presenceMask: BitSet = BitSet(fieldsWithPresence)

    @Suppress("PropertyName")
    public abstract val _size: Int

    @Suppress("PropertyName")
    public abstract val _unknownFields: Buffer

    public abstract val _descriptor: ProtoDescriptor<*>
}

@InternalRpcApi
public class MsgFieldDelegate<T>(
    private val presenceIdx: Int? = null,
    private val defaultProvider: (() -> T)? = null
) : ReadWriteProperty<InternalMessage, T> {

    private var valueSet = false
    private var value: T? = null

    override operator fun getValue(thisRef: InternalMessage, property: KProperty<*>): T {
        if (!valueSet) {
            if (defaultProvider != null) {
                value = defaultProvider.invoke()
                valueSet = true
            } else {
                error("Property ${property.name} not initialized")
            }
        }
        @Suppress("UNCHECKED_CAST")
        return value as T
    }

    override operator fun setValue(thisRef: InternalMessage, property: KProperty<*>, value: T) {
        presenceIdx?.let { thisRef.presenceMask[it] = true }
        this@MsgFieldDelegate.value = value
        valueSet = true
    }
}
