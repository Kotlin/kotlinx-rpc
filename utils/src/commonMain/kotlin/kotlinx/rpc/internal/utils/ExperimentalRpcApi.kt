/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal.utils

@RequiresOptIn(
    message = "This API is experimental and can be changed or removed at any time.",
    level = RequiresOptIn.Level.ERROR,
)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.ANNOTATION_CLASS,
)
public annotation class ExperimentalRpcApi
