package org.jetbrains.krpc.internal

@InternalKRPCApi
interface SequentialIdCounter {
    fun nextId(): Long
}
