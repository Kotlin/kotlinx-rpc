/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.constructClassLikeType

class FirRpcSupertypeGenerator(
    session: FirSession,
    logger: MessageCollector,
) : FirRpcSupertypeGeneratorAbstract(session, logger) {
    override fun computeAdditionalSupertypes(
        classLikeDeclaration: FirClassLikeDeclaration,
        resolvedSupertypes: List<FirResolvedTypeRef>,
        typeResolver: TypeResolveService,
    ): List<FirResolvedTypeRef> {
        return computeAdditionalSupertypesAbstract(resolvedSupertypes).map {
            vsApi {
                it.constructClassLikeType(emptyArray(), isNullable = false).toFirResolvedTypeRefVS()
            }
        }
    }
}
