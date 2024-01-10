/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc

import kotlinx.coroutines.CoroutineScope

// Marker interface for services
interface RPC : CoroutineScope {
    companion object
}
