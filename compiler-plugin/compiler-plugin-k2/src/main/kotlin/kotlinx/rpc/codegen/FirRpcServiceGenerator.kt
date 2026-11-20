/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.common.RpcClassId
import kotlinx.rpc.codegen.common.RpcNames
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.DirectDeclarationsAccess
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
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
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
                generateRpcServiceStubClass(owner as FirRegularClassSymbol)
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
            return setOf(
                RpcNames.SERVICE_DESCRIPTOR_PROPERTY_NAME,
                RpcNames.SERVICE_FACTORY_PROPERTY_NAME,
            )
        }

        return super.getCallableNamesForClass(classSymbol, context)
    }

    override fun generateProperties(
        callableId: CallableId,
        context: MemberGenerationContext?,
    ): List<FirPropertySymbol> {
        val owner = (context?.owner as FirRegularClassSymbol?)
            ?.takeIf { it.generatedRpcServiceStubKey == null && it.isRpcServiceInterface() }
            ?: return super.generateProperties(callableId, context)

        val protocol = owner.rpcAnnotation(session, FirRpcPredicates.rpcMeta)?.protocolType(session)
            ?: return super.generateProperties(callableId, context)

        val (key, returnType) = when (callableId.callableName) {
            RpcNames.SERVICE_FACTORY_PROPERTY_NAME -> {
                FirRpcServiceFactoryPropertyKey to owner.getFactoryClass().constructClassLikeType(arrayOf(protocol, owner.defaultType()))
            }
            RpcNames.SERVICE_DESCRIPTOR_PROPERTY_NAME -> {
                FirRpcServiceDescriptorPropertyKey to RpcClassId.rpcServiceDescriptor.constructClassLikeType(arrayOf(protocol, owner.defaultType()))
            }
            else -> return super.generateProperties(callableId, context)
        }

        return listOf(createOverrideProperty(owner, key, callableId.callableName, returnType))
    }

    private fun createOverrideProperty(
        owner: FirClassSymbol<*>,
        key: GeneratedDeclarationKey,
        name: Name,
        returnType: ConeKotlinType,
    ): FirPropertySymbol {
        return createMemberProperty(
            owner = owner,
            key = key,
            name = name,
            returnType = returnType,
            isVal = true,
            hasBackingField = false,
        ) {
            modality = Modality.OPEN
            visibility = Visibilities.Public
            status {
                isOverride = true
            }
        }.symbol
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
    private fun generateRpcServiceStubClass(owner: FirRegularClassSymbol): FirRegularClassSymbol? {
        val protocol = owner.rpcAnnotation(session, FirRpcPredicates.rpcMeta)?.protocolType(session)
            ?: return null

        return createNestedClass(owner, RpcNames.SERVICE_STUB_NAME, RpcGeneratedStubKey(owner.name)) {
            visibility = Visibilities.Public
            modality = Modality.FINAL

            typeParameter(Name.identifier("T"), config = {
                listOfNotNull(
                    RpcClassId.rpcFlowResponseCaller.takeIf { owner.containsFunction(isSuspend = false) },
                    RpcClassId.rpcUnaryResponseCaller.takeIf { owner.containsFunction(isSuspend = true) },
                ).forEach { caller ->
                    bound { caller.constructClassLikeType(arrayOf(protocol)) }
                }
            })
        }.apply {
            markAsDeprecatedHidden(session)
        }.symbol
    }

    // TODO
    /**
     * Reads only the source declarations of the class.
     * `declarationsVS` also gets the generated declarations, so it calls this generator again for the same class,
     * and the recursion causes a `StackOverflowError`.
     */
    @OptIn(SymbolInternals::class, DirectDeclarationsAccess::class)
    private fun FirRegularClassSymbol.containsFunction(isSuspend: Boolean): Boolean {
        return fir.declarations
            .map { it.symbol }
            .filterIsInstance<FirNamedFunctionSymbol>()
            .any { it.rawStatus.isSuspend == isSuspend }
    }

    private fun FirRegularClassSymbol.getFactoryClass(): ClassId {
        val containsFlowResponse = containsFunction(isSuspend = false)
        val containsUnaryResponse = containsFunction(isSuspend = true)

        return when {
            containsFlowResponse && containsUnaryResponse -> RpcClassId.rpcServiceStubFactoryEitherResponse
            containsFlowResponse -> RpcClassId.rpcServiceStubFactoryFlowResponse
            containsUnaryResponse -> RpcClassId.rpcServiceStubFactoryUnaryResponse
            else -> RpcClassId.rpcServiceStubFactory
        }
    }
}
