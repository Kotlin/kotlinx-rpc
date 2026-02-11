/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.google.protobuf.kotlin

import kotlinx.rpc.protobuf.ProtoMessage
import kotlin.reflect.KClass

public inline fun <@ProtoMessage reified T: kotlin.Any> Any.isA(): Boolean = this.isA(T::class)

/**
 * Check if the given Protobuf Any instance is of type [messageClass].
 */
public fun <@ProtoMessage T: kotlin.Any> Any.isA(messageClass: KClass<T>): Boolean {
    TODO("Implement")
    //    protoDescriptorOf(messageClass)
}