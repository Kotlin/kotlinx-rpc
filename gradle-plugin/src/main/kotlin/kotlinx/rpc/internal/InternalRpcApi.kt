/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal

import org.jetbrains.kotlinx.library.api.watchdog.InternalAnnotationMarker

/**
 * Marks API that has no compatibility guarantees.
 */
@InternalAnnotationMarker
@RequiresOptIn("This API is internal and should not be used outside of kotlinx-rpc")
public annotation class InternalRpcApi

/**
 * Marks API that has no compatibility guarantees for subclassing.
 */
@InternalAnnotationMarker
@RequiresOptIn("This API is not intended to be subclassed")
public annotation class InternalForSubclassingRpcApi
