/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal

/**
 * For legacy internal users ONLY.
 * Special dev builds may set this value to `false`.
 *
 * If the value is `false`, absence of [streamScoped] for a call is replaced with service's [StreamScope]
 * obtained via [withClientStreamScope].
 */
@InternalRPCApi
public const val STREAM_SCOPES_ENABLED: Boolean = true
