/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.common.RpcNames
import org.jetbrains.kotlin.KtFakeSourceElementKind
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fakeElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.builder.buildOuterClassTypeParameterRef
import org.jetbrains.kotlin.fir.declarations.builder.buildRegularClass
import org.jetbrains.kotlin.fir.declarations.impl.FirResolvedDeclarationStatusImpl
import org.jetbrains.kotlin.fir.declarations.origin
import org.jetbrains.kotlin.fir.declarations.utils.isInterface
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotation
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotationArgumentMapping
import org.jetbrains.kotlin.fir.extensions.*
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.plugin.*
import org.jetbrains.kotlin.fir.scopes.kotlinScopeProvider
import org.jetbrains.kotlin.fir.symbols.impl.*
import org.jetbrains.kotlin.fir.toEffectiveVisibility
import org.jetbrains.kotlin.fir.toFirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.constructClassType
import org.jetbrains.kotlin.fir.types.toLookupTag
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.name.StandardClassIds
import kotlin.collections.set

class FirRpcServiceGenerator(
    session: FirSession,
    @Suppress("unused")
    private val logger: MessageCollector,
) : FirDeclarationGenerationExtension(session) {
    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(FirRpcPredicates.rpc)
    }

    /**
     * Generates nested classifiers.
     *
     * They can be of two kinds:
     * - Nested Service Stub class.
     * In that case [classSymbol] will not have any RPC-generated [FirClassSymbol.origin].
     * The only check we do - presence of the `@Rpc` annotation and return [RpcNames.SERVICE_STUB_NAME].
     *
     * - Companion object of the service stub
     * If we generate this companion object, we will have [FirClassSymbol.origin]
     * of [classSymbol] be set to [RpcGeneratedStubKey],
     * because we're inside the previously generated service stub class.
     * So we return [SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT]
     * and a list of method class names.
     */
    override fun getNestedClassifiersNames(
        classSymbol: FirClassSymbol<*>,
        context: NestedClassGenerationContext,
    ): Set<Name> {
        val rpcServiceStubKey = classSymbol.generatedRpcServiceStubKey

        return when {
            rpcServiceStubKey != null -> {
                setOf(SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT)
            }

            classSymbol.isInterface && session.predicateBasedProvider.matches(FirRpcPredicates.rpc, classSymbol) -> {
                setOf(RpcNames.SERVICE_STUB_NAME)
            }

            else -> {
                emptySet()
            }
        }
    }

    /**
     * Handles class names provided by the [getNestedClassifiersNames] with the same cases.
     * For service stub classes checks if the generation is necessary (inside [generateRpcServiceStubClass]).
     */
    override fun generateNestedClassLikeDeclaration(
        owner: FirClassSymbol<*>,
        name: Name,
        context: NestedClassGenerationContext,
    ): FirClassLikeSymbol<*>? {
        val rpcServiceStubKey = owner.generatedRpcServiceStubKey
        return when {
            rpcServiceStubKey != null && name == SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT -> {
                generateCompanionObjectForRpcServiceStub(owner)
            }

            name == RpcNames.SERVICE_STUB_NAME -> {
                generateRpcServiceStubClass(owner)
            }

            else -> {
                error("Cannot run generation for ${owner.classId.createNestedClassId(name).asSingleFqName()}")
            }
        }
    }

    private fun generateCompanionObjectForRpcServiceStub(
        owner: FirClassSymbol<*>,
    ): FirClassLikeSymbol<*> {
        return createCompanionObject(owner, FirRpcServiceStubCompanionObject).symbol
    }

    /**
     * Generates [owner]'s service stub.
     * Scrapes the functions from the [owner] to generate method classes.
     */
    private fun generateRpcServiceStubClass(owner: FirClassSymbol<*>): FirRegularClassSymbol {
        return createNestedClass(owner, RpcNames.SERVICE_STUB_NAME, RpcGeneratedStubKey(owner.name)) {
            visibility = Visibilities.Public
            modality = Modality.FINAL
        }.apply {
            markAsDeprecatedHidden(session)
        }.symbol
    }
}
