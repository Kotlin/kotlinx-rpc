/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.common.RpcClassId
import org.jetbrains.kotlin.fir.extensions.predicate.DeclarationPredicate

object FirRpcPredicates {
    internal val rpc = DeclarationPredicate.create {
        metaAnnotated(RpcClassId.rpcAnnotation.asSingleFqName(), includeItself = true) // @Rpc
    }

    internal val checkedAnnotationMeta = DeclarationPredicate.create {
        metaAnnotated(RpcClassId.checkedTypeAnnotation.asSingleFqName(), includeItself = false)
    }
}
