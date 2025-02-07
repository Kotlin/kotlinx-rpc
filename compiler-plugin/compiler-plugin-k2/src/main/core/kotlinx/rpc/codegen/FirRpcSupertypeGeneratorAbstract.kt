/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.common.RpcClassId
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.declarations.utils.isInterface
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.FirSupertypeGenerationExtension
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.name.ClassId

abstract class FirRpcSupertypeGeneratorAbstract(
    session: FirSession,
    @Suppress("unused") private val logger: MessageCollector,
) : FirSupertypeGenerationExtension(session) {
    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(FirRpcPredicates.rpc)
    }

    override fun needTransformSupertypes(declaration: FirClassLikeDeclaration): Boolean {
        return session.predicateBasedProvider.matches(
            predicate = FirRpcPredicates.rpc,
            declaration = declaration,
        ) && declaration is FirClass && declaration.isInterface
    }

    protected fun computeAdditionalSupertypesAbstract(
        resolvedSupertypes: List<FirResolvedTypeRef>,
    ): List<ClassId> {
        if (resolvedSupertypes.any { it.doesMatchesClassId(session, RpcClassId.remoteServiceInterface) }) {
            return emptyList()
        }

        return listOf(RpcClassId.remoteServiceInterface)
    }
}

