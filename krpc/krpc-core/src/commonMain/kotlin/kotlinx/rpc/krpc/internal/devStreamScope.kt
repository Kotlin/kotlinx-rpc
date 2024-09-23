/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal

import kotlinx.rpc.internal.utils.InternalRPCApi

/**
 * For legacy internal users ONLY.
 * Special dev builds may set this value to `false`.
 *
 * If the value is `false`, absence of [kotlinx.rpc.krpc.streamScoped] for a call
 * is replaced with service's [kotlinx.rpc.krpc.StreamScope]
 * obtained via [kotlinx.rpc.krpc.withClientStreamScope].
 */
@InternalRPCApi
public const val STREAM_SCOPES_ENABLED: Boolean = true
