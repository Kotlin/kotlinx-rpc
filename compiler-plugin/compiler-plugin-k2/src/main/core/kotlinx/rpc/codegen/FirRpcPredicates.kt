/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.common.RpcClassId
import org.jetbrains.kotlin.fir.extensions.predicate.DeclarationPredicate

object FirRpcPredicates {
    internal val rpc = DeclarationPredicate.create {
        annotated(RpcClassId.rpcAnnotation.asSingleFqName()) // @Rpc
    }

    internal val grpc = DeclarationPredicate.create {
        annotated(RpcClassId.grpcAnnotation.asSingleFqName()) // @Grpc
    }

    internal val checkedAnnotationMeta = DeclarationPredicate.create {
        metaAnnotated(RpcClassId.checkedTypeAnnotation.asSingleFqName(), includeItself = false)
    }
}
