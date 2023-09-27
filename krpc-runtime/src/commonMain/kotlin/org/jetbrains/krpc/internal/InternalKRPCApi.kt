package org.jetbrains.krpc.internal

@RequiresOptIn(
    message = "This is internal kRPC api that is subject to change and should not be used",
    level = RequiresOptIn.Level.ERROR,
)
annotation class InternalKRPCApi
