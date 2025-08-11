/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class)

package kotlinx.rpc.grpc.internal

import kotlinx.cinterop.Arena
import kotlinx.cinterop.ExperimentalForeignApi

internal suspend fun withArena(block: suspend (Arena) -> Unit) =
    Arena().let { arena ->
        try {
            block(arena)
        } finally {
            arena.clear()
        }
    }

