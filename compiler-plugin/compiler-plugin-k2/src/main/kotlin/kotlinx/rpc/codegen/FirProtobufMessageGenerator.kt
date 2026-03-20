/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.common.ProtoNames
import org.jetbrains.kotlin.KtFakeSourceElementKind
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fakeElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.caches.FirCache
import org.jetbrains.kotlin.fir.caches.firCachesFactory
import org.jetbrains.kotlin.fir.caches.getValue
import org.jetbrains.kotlin.fir.declarations.utils.isInterface
import org.jetbrains.kotlin.fir.extensions.*
import org.jetbrains.kotlin.fir.plugin.createCompanionObject
import org.jetbrains.kotlin.fir.plugin.createMemberFunction
import org.jetbrains.kotlin.fir.plugin.createMemberProperty
import org.jetbrains.kotlin.fir.plugin.createNestedClass
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames

class FirProtobufMessageGenerator(
    session: FirSession,
    @Suppress("unused")
    private val logger: MessageCollector,
) : FirDeclarationGenerationExtension(session) {

    private class GeneratedNames(
        val propertyNames: Map<Name, FirPropertySymbol> = emptyMap(),
        val functionNames: Map<Name, FirPropertySymbol> = emptyMap(),
    )

    private val messageCallablesCache: FirCache<FirClassSymbol<*>, GeneratedNames, Nothing?> =
        session.firCachesFactory.createCache { messageClassSymbol: FirClassSymbol<*>, _ ->
            val propertyNames = mutableMapOf<Name, FirPropertySymbol>()
            val functionNames = mutableMapOf<Name, FirPropertySymbol>()
            vsApi {
                messageClassSymbol.forAllCallablesVS(session) { it ->
                    if (it is FirPropertySymbol) {
                        propertyNames[it.name] = it
                        // construct clear<PropertyName> function
                        val functionName = Name.identifier(
                            "clear${
                            it.name.asString()
                                .replaceFirstChar { char -> char.uppercase() }
                        }")
                        functionNames[functionName] = it
                    }
                }
            }
            GeneratedNames(propertyNames, functionNames)
        }

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
                setOf(
                    SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT,
                    ProtoNames.MESSAGE_BUILDER_NAME,
                )
            }

            else -> {
                super.getNestedClassifiersNames(classSymbol, context)
            }
        }
    }

    override fun generateNestedClassLikeDeclaration(
        owner: FirClassSymbol<*>,
        name: Name,
        context: NestedClassGenerationContext,
    ): FirClassLikeSymbol<*>? {
        if (
            !owner.isInterface ||
            !session.predicateBasedProvider.matches(FirRpcPredicates.generatedProtoMessage, owner)
        ) {
            return super.generateNestedClassLikeDeclaration(owner, name, context)
        }

        return when (name) {
            ProtoNames.MESSAGE_BUILDER_NAME -> {
                createNestedClass(
                    owner = owner,
                    name = name,
                    key = FirGeneratedProtoMessageBuilderKey(owner),
                    classKind = ClassKind.INTERFACE,
                ) {
                    visibility = Visibilities.Public
                    modality = Modality.ABSTRACT
                    superType { owner.defaultType() }
                    vsApi {
                        sourceVS = owner.source?.fakeElement(KtFakeSourceElementKind.PluginGenerated)
                    }
                }.symbol
            }

            SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT -> {
                createCompanionObject(owner, FirGeneratedProtoMessageCompanionObject).symbol
            }

            else -> {
                super.generateNestedClassLikeDeclaration(owner, name, context)
            }
        }
    }

    override fun getCallableNamesForClass(
        classSymbol: FirClassSymbol<*>,
        context: MemberGenerationContext
    ): Set<Name> {
        val messageClassSymbol = classSymbol.generatedProtoMessageBuilderKey?.message
            ?: return super.getCallableNamesForClass(classSymbol, context)

        val generatedNames = messageCallablesCache.getValue(messageClassSymbol)
        return generatedNames.propertyNames.keys + generatedNames.functionNames.keys
    }

    override fun generateProperties(
        callableId: CallableId,
        context: MemberGenerationContext?,
    ): List<FirPropertySymbol> {
        context ?: return super.generateProperties(callableId, context)

        val messageClassSymbol = context.owner.generatedProtoMessageBuilderKey?.message
            ?: return super.generateProperties(callableId, context)

        val property = messageCallablesCache.getValue(messageClassSymbol)
            .propertyNames[callableId.callableName]
            ?: return super.generateProperties(callableId, context)

        return listOf(
            createMemberProperty(
                owner = context.owner,
                key = FirGeneratedProtoMessageBuilderPropertyKey,
                name = callableId.callableName,
                returnType = property.resolvedReturnType,
                isVal = false,
                hasBackingField = false,
            ) {
                modality = Modality.ABSTRACT
                visibility = Visibilities.Public
                status {
                    isOverride = true
                }
                vsApi {
                    sourceVS = property.source?.fakeElement(KtFakeSourceElementKind.PluginGenerated)
                }
            }.symbol
        )
    }

    override fun generateFunctions(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirNamedFunctionSymbol> {
        context ?: return super.generateFunctions(callableId, context)

        val messageClassSymbol = context.owner.generatedProtoMessageBuilderKey?.message
            ?: return super.generateFunctions(callableId, context)

        val property = messageCallablesCache.getValue(messageClassSymbol)
            .functionNames[callableId.callableName] ?: return super.generateFunctions(callableId, context)

        return listOf(
            createMemberFunction(
                owner = context.owner,
                key = FirGeneratedProtoMessageBuilderFunctionKey,
                name = callableId.callableName,
                returnType = session.builtinTypes.unitType.coneType
            ) {
                visibility = Visibilities.Public
                modality = Modality.ABSTRACT
                vsApi {
                    sourceVS = property.source?.fakeElement(KtFakeSourceElementKind.PluginGenerated)
                }
            }.symbol
        )
    }
}
