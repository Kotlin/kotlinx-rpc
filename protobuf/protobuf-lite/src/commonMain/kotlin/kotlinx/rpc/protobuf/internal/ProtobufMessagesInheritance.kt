/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

@Target(AnnotationTarget.CLASS)
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR, message =
        "Generated protobuf messages are not intended to be inherited from, " +
            "as the library may handle predefined instances of this in a special manner. " +
            "If you need to inherit from this, please describe your use case in " +
            "https://github.com/Kotlin/kotlinx-rpc/issues/new?template=feature_request.md"
)
public annotation class ProtobufMessagesInheritance
