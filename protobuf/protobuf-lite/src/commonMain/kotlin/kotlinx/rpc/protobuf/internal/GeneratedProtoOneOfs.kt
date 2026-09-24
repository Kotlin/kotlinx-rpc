/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * Marks the generated internal implementation class of a message that declares `oneof`s.
 *
 * The compiler plugin reads [names] to declare a `clear<OneOf>()` function
 * on the message's `Builder` interface for each oneof.
 * The internal class implements these functions.
 *
 * @property names The names of the `oneof` declarations of the message, in declaration order,
 * as they appear in the derived Kotlin names (`clear<OneOf>`, `when<OneOf>`).
 */
@InternalRpcApi
@Target(AnnotationTarget.CLASS)
public annotation class GeneratedProtoOneOfs(val names: Array<String>)
