/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.common.RpcClassId
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.FirSupertypeGenerationExtension
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.fir.types.toFirResolvedTypeRef

class FirRpcSupertypeGenerator(
    session: FirSession,
    @Suppress("unused") private val logger: MessageCollector,
) : FirSupertypeGenerationExtension(session) {
    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(FirRpcPredicates.rpc)
    }

    override fun needTransformSupertypes(declaration: FirClassLikeDeclaration): Boolean {
        return session.predicateBasedProvider.matches(FirRpcPredicates.rpc, declaration)
    }

    override fun computeAdditionalSupertypes(
        classLikeDeclaration: FirClassLikeDeclaration,
        resolvedSupertypes: List<FirResolvedTypeRef>,
        typeResolver: TypeResolveService,
    ): List<FirResolvedTypeRef> {
        if (resolvedSupertypes.any { it.type.classId == RpcClassId.rpcInterface }) {
            return emptyList()
        }

        return listOf(
            RpcClassId.rpcInterface
                .constructClassLikeType(emptyArray(), isNullable = false)
                .toFirResolvedTypeRef()
        )
    }
}
