/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal.utils

/**
 * Opt-in marker for subclassing library types that are not intended to be inherited by user code.
 *
 * Types annotated with `@SubclassOptInRequired(RpcTypeInheritance::class)` require an explicit opt-in
 * before they can be subclassed. This is used to guard interfaces and classes whose implementations
 * are provided by the library and may change in future versions.
 */
@Target(AnnotationTarget.CLASS)
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR, message =
        "This type is not intended to be inherited by user code, " +
            "as the library may handle predefined implementations in a special manner. " +
            "If you need to inherit from this, please describe your use case in " +
            "https://github.com/Kotlin/kotlinx-rpc/issues/new?template=feature_request.md"
)
public annotation class RpcTypeInheritance
