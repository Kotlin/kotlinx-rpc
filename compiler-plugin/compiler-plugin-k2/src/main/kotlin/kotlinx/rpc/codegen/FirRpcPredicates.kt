/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.common.ProtoClassId
import kotlinx.rpc.codegen.common.RpcClassId
import org.jetbrains.kotlin.fir.extensions.predicate.DeclarationPredicate

object FirRpcPredicates {
    internal val rpc = DeclarationPredicate.create {
        metaAnnotated(RpcClassId.rpcAnnotation.asSingleFqName(), includeItself = true) // @Rpc
    }

    internal val grpc = DeclarationPredicate.create {
        metaAnnotated(RpcClassId.grpcAnnotation.asSingleFqName(), includeItself = true)
    }

    internal val withCodec = DeclarationPredicate.create {
        annotated(RpcClassId.withCodecAnnotation.asSingleFqName())
    }

    internal val checkedAnnotationMeta = DeclarationPredicate.create {
        metaAnnotated(RpcClassId.checkedTypeAnnotation.asSingleFqName(), includeItself = false)
    }

    internal val generatedProtoMessage = DeclarationPredicate.create {
        annotated(ProtoClassId.protoMessageAnnotation.asSingleFqName())
    }
}
