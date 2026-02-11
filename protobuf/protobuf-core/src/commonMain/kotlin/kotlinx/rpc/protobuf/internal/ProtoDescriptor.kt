/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.rpc.internal.internalRpcError
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtoMessage
import kotlin.reflect.KClass

@InternalRpcApi
@Target(AnnotationTarget.CLASS)
expect public annotation class WithProtoDescriptor(
    @Suppress("unused")
    val descriptor: KClass<out ProtoDescriptor<*>>,
)

@InternalRpcApi
public interface ProtoDescriptor<@ProtoMessage Message: Any> {
    public val fullName: String
}

internal inline fun <@ProtoMessage reified T: Any> protoDescriptorOf(): ProtoDescriptor<T> = protoDescriptorOf(T::class)

internal fun <@ProtoMessage T: Any> protoDescriptorOf(clazz: KClass<T>): ProtoDescriptor<T> {
    val maybeDescriptor = findProtoDescriptorOf(clazz)
        ?: internalRpcError("Unable to find a proto descriptor of the $clazz. " +
            "Check that you have applied the “kotlinx.rpc” plugin to your build module.")
    if (maybeDescriptor is ProtoDescriptor<*>) {
        @Suppress("UNCHECKED_CAST")
        return maybeDescriptor as ProtoDescriptor<T>
    }

    internalRpcError("Located descriptor object is not of a desired type ${ProtoDescriptor::class}, " +
                "instead found $maybeDescriptor of the class ${maybeDescriptor::class}")
}

internal expect fun <@ProtoMessage T: Any> findProtoDescriptorOf(kClass: KClass<T>): Any?

