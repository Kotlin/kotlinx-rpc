package org.jetbrains.krpc.internal

@InternalKRPCApi
interface SeqIdConuter {
    fun nextId(): Long
}
