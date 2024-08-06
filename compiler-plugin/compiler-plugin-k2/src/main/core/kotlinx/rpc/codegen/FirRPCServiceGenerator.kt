/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.common.ClassDeclarations
import kotlinx.rpc.codegen.common.RpcNames
import kotlinx.rpc.codegen.common.rpcMethodClassName
import kotlinx.rpc.codegen.common.rpcMethodName
import kotlinx.rpc.codegen.serialization.addAnnotation
import kotlinx.rpc.codegen.serialization.generateCompanionDeclaration
import kotlinx.rpc.codegen.serialization.generateSerializerImplClass
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.declarations.utils.isInterface
import org.jetbrains.kotlin.fir.declarations.utils.visibility
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.extensions.NestedClassGenerationContext
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.plugin.*
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.*
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.platform.isJs
import org.jetbrains.kotlin.platform.isWasm
import org.jetbrains.kotlin.platform.konan.isNative
import org.jetbrains.kotlinx.serialization.compiler.fir.SerializationFirResolveExtension
import org.jetbrains.kotlinx.serialization.compiler.fir.SerializationFirSupertypesExtension
import org.jetbrains.kotlinx.serialization.compiler.resolve.SerialEntityNames
import org.jetbrains.kotlinx.serialization.compiler.resolve.SerializationPackages

/**
 * What is happening here:
 *
 * ## General idea
 *
 * [getNestedClassifiersNames] should return a set of [Name]s to generate for.
 * For these names [generateNestedClassLikeDeclaration] can generate some nested classes,
 * which is what we need.
 *
 * But the catch is that we cannot say for sure, if we need to generate a class
 * while in [getNestedClassifiersNames], but if we do not return anything
 * [generateNestedClassLikeDeclaration] will not be called.
 * We need to generate a class if only the current declaration is an RPC interface
 * (inherits kotlinx.rpc.RPC). There is no resolved supertypes in [getNestedClassifiersNames],
 * But, if the potentially generated class is not referenced anywhere,
 * then [generateNestedClassLikeDeclaration] will already have supertypes resolved,
 * so we can use this info to check the actual supertypes for RPC interface.
 *
 * So we always return a class name that may be generated.
 * And then, in [generateNestedClassLikeDeclaration] we do the actual check with the resolved supertypes
 * and generate a class if needed, otherwise returning null.
 *
 * ## Usage of kotlinx.serialization plugin
 *
 * Here is one more tricky part.
 *
 * We generate classes that are marked `@Serializable`.
 * In that case, the serialization plugin will not be able to pick up those classes and process them accordingly.
 *
 * That's why we have an instance of this plugin, which we call only on our generated classes - [serializationExtension]
 *
 * Not all the methods that we would like to call are public, so we access them via reflection:
 * - [generateCompanionDeclaration]
 * - [generateSerializerImplClass]
 *
 * This is basically copying the behavior of the actual plugin but isolated only for our generated classes.
 */
class FirRPCServiceGenerator(
    session: FirSession,
    @Suppress("unused")
    private val logger: MessageCollector,
) : FirDeclarationGenerationExtension(session) {
    private val serializationExtension = SerializationFirResolveExtension(session)
    private val isJvmOrMetadata = !session.moduleData.platform.run { isJs() || isWasm() || isNative() }

    /**
     * Generates nested classifiers.
     *
     * They can be of three kinds:
     * - Nested Service Stub class.
     * In that case [classSymbol] will not have any RPC-generated [FirClassSymbol.origin].
     * Rge only check we do - is we check that the declaration is an interface,
     * and return [RpcNames.SERVICE_STUB_NAME].
     * We cannot be sure if the declaration is actually an RPC service,
     * because superTypes are not resolved during that stage.
     * We postpone this check until [generateNestedClassLikeDeclaration].
     *
     * - Companion object of the service stub and method classes.
     * If we generate this companion object, we will have [FirClassSymbol.origin]
     * of [classSymbol] be set to [RPCGeneratedStubKey],
     * because we are inside the previously generated service stub class.
     * The same goes for method classes too.
     * So we return [SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT]
     * and a list of method class names.
     *
     * - Inside method classes.
     * Method classes will too have their nested declarations.
     * We detect them by using [RPCGeneratedRpcMethodClassKey].
     * Nested declarations for these classes are provided by the serialization plugin.
     * These declarations can be of two types: serializable object and serializable classes.
     * In the case of objects,
     * the serialization plugin treats them like their own serializers,
     * so no extra nested declarations are necessary.
     * In the case of classes, serialization requires two additional nested declarations:
     * [SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT] and [SerialEntityNames.SERIALIZER_CLASS_NAME].
     */
    override fun getNestedClassifiersNames(
        classSymbol: FirClassSymbol<*>,
        context: NestedClassGenerationContext,
    ): Set<Name> {
        val rpcServiceStubKey = classSymbol.generatedRpcServiceStubKey
        val rpcMethodClassKey = classSymbol.generatedRpcMethodClassKey

        return when {
            rpcMethodClassKey != null -> {
                when {
                    !rpcMethodClassKey.isObject -> setOf(
                        SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT,
                        SerialEntityNames.SERIALIZER_CLASS_NAME,
                    )

                    // otherwise an object is generated instead of a class
                    // serialization plugin has other logic for such declarations
                    else -> emptySet()
                }
            }

            rpcServiceStubKey != null -> {
                rpcServiceStubKey.functions.map { it.name.rpcMethodClassName }.toSet() +
                        SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT
            }

            classSymbol.isInterface -> {
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
        context: NestedClassGenerationContext
    ): FirClassLikeSymbol<*>? {
        val rpcServiceStubKey = owner.generatedRpcServiceStubKey
        return when {
            rpcServiceStubKey != null && name == SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT -> {
                generateCompanionObjectForRpcServiceStub(owner)
            }

            rpcServiceStubKey != null -> {
                generateRpcMethodClass(owner, name, rpcServiceStubKey)
            }

            owner.generatedRpcMethodClassKey != null -> {
                generateNestedClassLikeDeclarationWithSerialization(owner, name)
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
     * Generates method class for the respectful function.
     * Class is marked `@Serializable` for the backend processing.
     */
    private fun generateRpcMethodClass(
        owner: FirClassSymbol<*>,
        name: Name,
        rpcServiceStubKey: RPCGeneratedStubKey,
    ): FirClassLikeSymbol<*> {
        val methodName = name.rpcMethodName
        val rpcMethod = rpcServiceStubKey.functions.single { it.name == methodName }
        val rpcMethodClassKey = RPCGeneratedRpcMethodClassKey(rpcMethod)
        val classKind = if (rpcMethodClassKey.isObject) ClassKind.OBJECT else ClassKind.CLASS

        val rpcMethodClass = createNestedClass(
            owner = owner,
            name = name,
            key = rpcMethodClassKey,
            classKind = classKind,
        ) {
            visibility = owner.visibility
            modality = Modality.FINAL
        }

        rpcMethodClass.addAnnotation(ClassDeclarations.serializableAnnotation, session)

        /**
         * Required to pass isSerializableObjectAndNeedsFactory check
         * from [SerializationFirSupertypesExtension].
         */
        if (!isJvmOrMetadata && rpcMethodClassKey.isObject) {
            rpcMethodClass.replaceSuperTypeRefs(createSerializationFactorySupertype())
        }

        return rpcMethodClass.symbol
    }

    /**
     * Instead of [SerializationFirSupertypesExtension]
     *
     * Also, it is not run for Companion objects, as [serializationExtension] does it when needed.
     */
    private fun createSerializationFactorySupertype(): List<FirResolvedTypeRef> {
        val serializerFactoryClassId = ClassId(
            SerializationPackages.internalPackageFqName,
            SerialEntityNames.SERIALIZER_FACTORY_INTERFACE_NAME,
        )

        val ref = serializerFactoryClassId
            .constructClassLikeType(emptyArray(), false)
            .toFirResolvedTypeRef()

        return listOf(ref)
    }

    /**
     * Mirrors [generateNestedClassLikeDeclaration] from [serializationExtension].
     */
    private fun generateNestedClassLikeDeclarationWithSerialization(
        owner: FirClassSymbol<*>,
        name: Name,
    ): FirClassLikeSymbol<*>? {
        if (owner !is FirRegularClassSymbol) {
            error("Expected ${owner.name} to be FirRegularClassSymbol")
        }

        return when (name) {
            SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT -> serializationExtension.generateCompanionDeclaration(owner)
            SerialEntityNames.SERIALIZER_CLASS_NAME -> serializationExtension.generateSerializerImplClass(owner)
            else -> error("Can't generate class ${owner.classId.createNestedClassId(name).asSingleFqName()}")
        }
    }

    /**
     * Checks whether the [owner] class is actually an RPC service
     * (the supertypes are resolved at this stage,
     * as the [RpcNames.SERVICE_STUB_NAME] is not references anywhere)
     *
     * If the [owner] is an RPC service - generates its service stub.
     * Scrapes the functions from the [owner] to generate method classes.
     */
    private fun generateRpcServiceStubClass(owner: FirClassSymbol<*>): FirRegularClassSymbol? {
        owner.resolvedSuperTypes.find {
            it.classId == ClassDeclarations.rpcInterface
        } ?: return null

        @OptIn(SymbolInternals::class)
        val functions = owner.fir.declarations
            .filterIsInstance<FirFunction>()
            .map { it.symbol }

        return createNestedClass(owner, RpcNames.SERVICE_STUB_NAME, RPCGeneratedStubKey(owner.name, functions)) {
            visibility = owner.visibility
            modality = Modality.FINAL
        }.symbol
    }

    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
        val rpcMethodClassKey = classSymbol.generatedRpcMethodClassKey

        return when {
            rpcMethodClassKey != null -> {
                getCallableNamesForRpcMethodClass(classSymbol, context, rpcMethodClassKey)
            }

            classSymbol.isFromSerializationPlugin -> {
                serializationExtension.getCallableNamesForClass(classSymbol, context)
            }

            else -> {
                emptySet()
            }
        }
    }

    /**
     * If the method does not have any parameters, it is an object,
     * and its only callable names are constructor, and the ones provided by the [serializationExtension].
     * Otherwise, the callable names are the names of the method parameters and the constructor.
     */
    private fun getCallableNamesForRpcMethodClass(
        classSymbol: FirClassSymbol<*>,
        context: MemberGenerationContext,
        rpcMethodClassKey: RPCGeneratedRpcMethodClassKey,
    ): Set<Name> {
        return if (rpcMethodClassKey.isObject) {
            // add .serializer() method for a serializable object
            serializationExtension.getCallableNamesForClass(classSymbol, context)
        } else {
            rpcMethodClassKey.rpcMethod.valueParameterSymbols.map { it.name }.toSet()
        } + SpecialNames.INIT

        // ^ init is necessary either way, as serialization does not add it for a serializable object
    }

    override fun generateConstructors(context: MemberGenerationContext): List<FirConstructorSymbol> {
        val rpcMethodClassKey = context.owner.generatedRpcMethodClassKey
        return when {
            rpcMethodClassKey != null -> generateConstructorsForRpcMethodClass(context, rpcMethodClassKey)
            context.owner.isFromSerializationPlugin -> serializationExtension.generateConstructors(context)
            else -> emptyList()
        }
    }

    /**
     * An object needs only a default private constructor.
     *
     * A regular class constructor requires also value parameters of the respectful method.
     */
    private fun generateConstructorsForRpcMethodClass(
        context: MemberGenerationContext,
        rpcMethodClassKey: RPCGeneratedRpcMethodClassKey,
    ): List<FirConstructorSymbol> {
        if (rpcMethodClassKey.isObject) {
            return createDefaultPrivateConstructor(context.owner, rpcMethodClassKey).symbol.let(::listOf)
        }

        return createConstructor(context.owner, rpcMethodClassKey) {
            visibility = Visibilities.Public

            rpcMethodClassKey.rpcMethod.valueParameterSymbols.forEach { valueParam ->
                valueParameter(
                    name = valueParam.name,
                    type = valueParam.resolvedReturnType,
                )
            }
        }.symbol.let(::listOf)
    }

    override fun generateProperties(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirPropertySymbol> {
        context ?: return emptyList()

        val owner = context.owner
        val rpcMethodClassKey = owner.generatedRpcMethodClassKey

        return when {
            rpcMethodClassKey != null -> {
                generatePropertiesForRpcMethodClass(callableId, owner, rpcMethodClassKey)
            }

            owner.isFromSerializationPlugin -> {
                serializationExtension.generateProperties(callableId, context)
            }

            else -> {
                emptyList()
            }
        }
    }

    private fun generatePropertiesForRpcMethodClass(
        callableId: CallableId,
        owner: FirClassSymbol<*>,
        rpcMethodClassKey: RPCGeneratedRpcMethodClassKey,
    ): List<FirPropertySymbol> {
        val valueParam = rpcMethodClassKey.rpcMethod.valueParameterSymbols.find {
            it.name == callableId.callableName
        } ?: return emptyList()

        return createMemberProperty(
            owner = owner,
            key = rpcMethodClassKey,
            name = callableId.callableName,
            returnType = valueParam.resolvedReturnType,
        ).apply {
            if (valueParam.resolvedReturnType.requiresContextual()) {
                addAnnotation(ClassDeclarations.contextualAnnotation, session)
            }
        }.symbol.let(::listOf)
    }

    private fun ConeKotlinType.requiresContextual(): Boolean {
        return when (classId) {
            ClassDeclarations.flow, ClassDeclarations.sharedFlow, ClassDeclarations.stateFlow -> true
            else -> false
        }
    }

    /**
     * Processes serialization related cases from [getCallableNamesForClass].
     */
    override fun generateFunctions(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirNamedFunctionSymbol> {
        val owner = context?.owner ?: return emptyList()

        return when {
            owner.isFromSerializationPlugin || owner.generatedRpcMethodClassKey?.isObject == true -> {
                serializationExtension.generateFunctions(callableId, context)
            }

            else -> {
                emptyList()
            }
        }
    }
}
