/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.annotations

import kotlinx.rpc.RpcProtocol
import kotlin.reflect.KClass

@Target(AnnotationTarget.ANNOTATION_CLASS)
public annotation class RpcMeta(
    val protocolClass: KClass<out RpcProtocol>
)
