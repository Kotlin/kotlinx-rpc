/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal.utils

@RequiresOptIn(
    message = "This is internal kotlinx.rpc api that is subject to change and should not be used",
    level = RequiresOptIn.Level.ERROR,
)
@InternalRPCApi
public annotation class InternalRPCApi
