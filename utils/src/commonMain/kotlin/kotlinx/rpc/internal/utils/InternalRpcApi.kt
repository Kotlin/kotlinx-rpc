/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal.utils

import org.jetbrains.kotlinx.library.api.watchdog.InternalAnnotationMarker

/**
 * Marks API that has no compatibility guarantees.
 */
@RequiresOptIn(
    message = "This is internal kotlinx.rpc api that is subject to change and should not be used",
    level = RequiresOptIn.Level.ERROR,
)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CONSTRUCTOR,
)
@InternalAnnotationMarker
public annotation class InternalRpcApi
