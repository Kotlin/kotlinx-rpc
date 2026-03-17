/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.io.Buffer
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.cast

@InternalRpcApi
public abstract class InternalMessage(
    fieldsWithPresence: Int
) {
    public val presenceMask: BitSet = BitSet(fieldsWithPresence)

    public val _extensions: MutableMap<Int, ExtensionValue> = mutableMapOf()

    @Suppress("PropertyName")
    public abstract val _size: Int

    @Suppress("PropertyName")
    public abstract val _unknownFields: Buffer

    public abstract fun copyInternal(): InternalMessage

    public fun <V: Any> getExtensionValue(descriptor: InternalExtensionDescriptor<*, V>): V? {
        val value = _extensions[descriptor.fieldNumber] ?: return null
        if (value.descriptor != descriptor) return null
        @Suppress("UNCHECKED_CAST")
        return value.value as V
    }

    public fun <V: Any> setExtensionValue(descriptor: InternalExtensionDescriptor<*, V>, value: V?) {
        if (value == null) {
            _extensions.remove(descriptor.fieldNumber)
        } else {
            _extensions[descriptor.fieldNumber] = ExtensionValue(value, descriptor)
        }
    }

    protected fun extensionsEqual(other: InternalMessage): Boolean {
        if (_extensions.size != other._extensions.size) return false
        _extensions.entries.forEach { (key, value) ->
            val otherValue = other._extensions[key] ?: return false
            if (value.value != otherValue.value) return false
        }
        return true
    }

    protected fun extensionsHashCode(): Int {
        var result = 0
        _extensions.values.forEach { value ->
            result = 31 * result + value.value.hashCode()
        }
        return result
    }

    protected fun StringBuilder.appendExtensions(nextIndentString: String) {
        _extensions.values.forEach { value ->
            appendLine("${nextIndentString}${value.descriptor.name}=${value.value},")
        }
    }

    public fun extensionsSize(): Int {
        var result = 0
        _extensions.forEach { (key, value) ->
            result += value.descriptor.size(key, value.value)
        }
        return result
    }

    protected fun copyExtensionsFrom(other: InternalMessage) {
        other._extensions.forEach { (key, value) ->
            val descriptor = value.descriptor
            val castedValue = descriptor.valueType.cast(value.value)
            val valueCopy = descriptor.copy(castedValue)
            _extensions[key] = ExtensionValue(valueCopy, value.descriptor)
        }
    }
}

@InternalRpcApi
public class MsgFieldDelegate<T>(
    private val presenceIdx: Int? = null,
    private val defaultProvider: (() -> T)? = null
) : ReadWriteProperty<InternalMessage, T> {

    private var valueSet = false
    private var value: T? = null

    private fun storeValue(thisRef: InternalMessage, value: T) {
        presenceIdx?.let { thisRef.presenceMask[it] = true }
        this.value = value
        valueSet = true
    }

    override operator fun setValue(thisRef: InternalMessage, property: KProperty<*>, value: T) {
        storeValue(thisRef, value)
    }

    /**
     * Returns the set value or the default if not set.
     */
    override operator fun getValue(thisRef: InternalMessage, property: KProperty<*>): T {
        if (!valueSet) {
            if (defaultProvider != null) {
                return defaultProvider.invoke()
            } else {
                error("Property ${property.name} not initialized")
            }
        }
        @Suppress("UNCHECKED_CAST")
        return value as T
    }

    /**
     * Gets the value if set, otherwise create a new instance from the [factory].
     * This is used during decoding to create a new instance or merge with the existing one.
     * It will also set the presence of the field.
     */
    public fun getOrCreate(thisRef: InternalMessage, factory: () -> T): T {
        if (!valueSet) {
            storeValue(thisRef, factory())
        }

        @Suppress("UNCHECKED_CAST")
        return value as T
    }
}

@InternalRpcApi
public class ExtensionValue(
    public val value: Any,
    public val descriptor: InternalExtensionDescriptor<*, *>
)

@InternalRpcApi
public interface InternalPresenceObject {
    public val _message: InternalMessage

    public fun hasExtension(descriptor: InternalExtensionDescriptor<*, *>): Boolean {
        val extension = _message._extensions[descriptor.fieldNumber] ?: return false
        return extension.descriptor == descriptor
    }
}
