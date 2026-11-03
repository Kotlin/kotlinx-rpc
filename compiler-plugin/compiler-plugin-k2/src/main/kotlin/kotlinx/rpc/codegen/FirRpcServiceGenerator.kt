/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.common.RpcNames
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.utils.isInterface
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.extensions.NestedClassGenerationContext
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.plugin.createCompanionObject
import org.jetbrains.kotlin.fir.plugin.createMemberProperty
import org.jetbrains.kotlin.fir.plugin.createNestedClass
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames

class FirRpcServiceGenerator(
    session: FirSession,
    @Suppress("unused")
    private val logger: MessageCollector,
) : FirDeclarationGenerationExtension(session) {
    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(FirRpcPredicates.rpcMeta)
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

            classSymbol.isRpcServiceInterface() -> {
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

    override fun getCallableNamesForClass(
        classSymbol: FirClassSymbol<*>,
        context: MemberGenerationContext,
    ): Set<Name> {
        if (classSymbol.generatedRpcServiceStubKey == null && classSymbol.isRpcServiceInterface()) {
            return setOf(RpcNames.SERVICE_DESCRIPTOR_PROPERTY_NAME)
        }

        return super.getCallableNamesForClass(classSymbol, context)
    }

    override fun generateProperties(
        callableId: CallableId,
        context: MemberGenerationContext?,
    ): List<FirPropertySymbol> {
        val owner = context?.owner
        if (owner == null || callableId.callableName != RpcNames.SERVICE_DESCRIPTOR_PROPERTY_NAME
            || owner.generatedRpcServiceStubKey != null || !owner.isRpcServiceInterface()
        ) {
            return super.generateProperties(callableId, context)
        }

        val descriptorClass = owner.rpcAnnotation(session, FirRpcPredicates.rpcMeta)
            ?.classFromRpcMeta(session, RpcNames.RPC_META_DESCRIPTOR_CLASS)
            ?: return super.generateProperties(callableId, context)

        val descriptorType = descriptorClass.classId?.constructClassLikeType(arrayOf(owner.defaultType()))
            ?: return super.generateProperties(callableId, context)

        return listOf(
            createMemberProperty(
                owner = owner,
                key = FirRpcServiceDescriptorPropertyKey,
                name = callableId.callableName,
                returnType = descriptorType,
                isVal = true,
                hasBackingField = false,
            ) {
                modality = Modality.OPEN
                visibility = Visibilities.Public
                status {
                    isOverride = true
                }
            }.symbol
        )
    }

    private fun FirClassSymbol<*>.isRpcServiceInterface(): Boolean {
        return isInterface && session.predicateBasedProvider.matches(FirRpcPredicates.rpcMeta, this)
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
