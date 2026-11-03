/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.FirRpcPredicates.rpcMeta
import kotlinx.rpc.codegen.common.RpcNames
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.FirSupertypeGenerationExtension
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.types.ConeClassLikeType
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.fir.types.constructType

class FirRpcServiceSuperTypeExtension(session: FirSession) : FirSupertypeGenerationExtension(session) {
    override fun needTransformSupertypes(declaration: FirClassLikeDeclaration): Boolean {
        if (declaration !is FirClass || declaration.classKind != ClassKind.INTERFACE) {
            return false
        }

        return session.predicateBasedProvider.matches(rpcMeta, declaration)
    }

    override fun computeAdditionalSupertypes(
        classLikeDeclaration: FirClassLikeDeclaration,
        resolvedSupertypes: List<FirResolvedTypeRef>,
        typeResolver: TypeResolveService,
    ): List<ConeKotlinType> {
        val serviceClass = classLikeDeclaration.symbol.rpcAnnotation(session, rpcMeta)
            ?.classFromRpcMeta(session, RpcNames.RPC_META_SERVICE_CLASS) as? ConeClassLikeType
            ?: return emptyList()
        val selfType = classLikeDeclaration.symbol.constructType()

        return listOf(serviceClass.lookupTag.classId.constructClassLikeType(arrayOf(selfType)))
    }

    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(rpcMeta)
    }
}

