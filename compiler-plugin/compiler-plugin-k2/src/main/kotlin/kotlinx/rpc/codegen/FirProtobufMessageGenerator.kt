/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.utils.isInterface
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.NestedClassGenerationContext
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.plugin.createCompanionObject
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames

class FirProtobufMessageGenerator(
    session: FirSession,
    @Suppress("unused")
    private val logger: MessageCollector,
) : FirDeclarationGenerationExtension(session) {
    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(FirRpcPredicates.generatedProtoMessage)
    }

    override fun getNestedClassifiersNames(
        classSymbol: FirClassSymbol<*>,
        context: NestedClassGenerationContext,
    ): Set<Name> {
        return when {
            classSymbol.isInterface &&
                    session.predicateBasedProvider
                        .matches(FirRpcPredicates.generatedProtoMessage, classSymbol)
                -> {
                setOf(SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT)
            }

            else -> {
                emptySet()
            }
        }
    }

    override fun generateNestedClassLikeDeclaration(
        owner: FirClassSymbol<*>,
        name: Name,
        context: NestedClassGenerationContext,
    ): FirClassLikeSymbol<*>? {
        return when {
            name == SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT -> {
                createCompanionObject(owner, FirGeneratedProtoMessageCompanionObject).symbol
            }

            else -> {
                error("Cannot run generation for ${owner.classId.createNestedClassId(name).asSingleFqName()}")
            }
        }
    }
}
