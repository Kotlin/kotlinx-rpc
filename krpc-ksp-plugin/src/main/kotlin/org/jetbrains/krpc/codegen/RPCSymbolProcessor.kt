/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt:all")

package org.jetbrains.krpc.codegen

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*

class RPCSymbolProcessor(
    private val env: SymbolProcessorEnvironment
) : SymbolProcessor {
    private var services: Sequence<RPCServiceDeclaration> = emptySequence()

    override fun finish() {
        val codegen = RPCClientServiceGenerator(env.codeGenerator)

        services.forEach {
            codegen.generate(it)
        }
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val context = RPCSymbolProcessorContext(resolver)

        services = sequence {
            resolver.getAllFiles().forEach { file ->
                file.declarations.forEach { declaration ->
                    if (declaration is KSClassDeclaration && declaration.classKind == ClassKind.INTERFACE && declaration.superTypes.find { it.resolve() == context.rpcType } != null) {
                        yield(handleService(declaration, file, context))
                    }
                }
            }
        }

        return emptyList()
    }

    private fun handleService(
        serviceDeclaration: KSClassDeclaration,
        file: KSFile,
        context: RPCSymbolProcessorContext,
    ): RPCServiceDeclaration {
        if (serviceDeclaration.typeParameters.isNotEmpty()) {
            codegenError<ServiceInterfaceTypeParametersCodeGenerationException>(serviceDeclaration)
        }

        if (serviceDeclaration.modifiers.intersect(DENY_LIST_SERVICE_MODIFIERS).isNotEmpty()) {
            codegenError<ForbiddenServiceInterfaceModifierCodeGenerationException>(serviceDeclaration)
        }

        val processedFunctions = serviceDeclaration.getDeclaredFunctions().toList().map {
            handleServiceFunction(it, context)
        }

        val processedProperties = serviceDeclaration.getDeclaredProperties().toList().map {
            handleServiceField(it, context)
        }

        return RPCServiceDeclaration(
            declaration = serviceDeclaration,
            simpleName = serviceDeclaration.simpleName.asString(),
            fullName = serviceDeclaration.qualifiedName?.asString()
                ?: codegenError<AbsentQualifiedNameCodeGenerationException>(serviceDeclaration),
            functions = processedFunctions,
            fields = processedProperties,
            file = file,
        )
    }

    private fun handleServiceFunction(
        functionDeclaration: KSFunctionDeclaration,
        context: RPCSymbolProcessorContext,
    ): RPCServiceDeclaration.Function {
        if (functionDeclaration.typeParameters.isNotEmpty()) {
            codegenError<MethodTypeParametersCodeGenerationException>(functionDeclaration)
        }

        if (functionDeclaration.extensionReceiver != null) {
            codegenError<MethodExtensionReceiverCodeGenerationException>(functionDeclaration)
        }

        if (functionDeclaration.modifiers.intersect(DENY_LIST_FUNCTION_MODIFIERS).isNotEmpty()) {
            codegenError<ForbiddenMethodModifierCodeGenerationException>(functionDeclaration)
        }

        if (!functionDeclaration.modifiers.contains(Modifier.SUSPEND)) {
            codegenError<AbsentSuspendMethodModifierCodeGenerationException>(functionDeclaration)
        }

        if (!functionDeclaration.isAbstract) {
            codegenError<MethodDefaultImplementationCodeGenerationException>(functionDeclaration)
        }

        val returnType = functionDeclaration.returnType?.resolve()
            ?: codegenError<UnresolvedMethodReturnTypeCodeGenerationException>(functionDeclaration)

        val name = functionDeclaration.simpleName.getShortName()

        if (name.contains("$")) {
            codegenError<InvalidMethodNameCodeGenerationException>(functionDeclaration)
        }

        return RPCServiceDeclaration.Function(
            name = name,
            argumentTypes = functionDeclaration.parameters.map {
                handleFunctionArgument(
                    functionDeclaration,
                    it,
                    context
                )
            },
            returnType = returnType,
        )
    }

    private fun handleFunctionArgument(
        functionDeclaration: KSFunctionDeclaration,
        argument: KSValueParameter,
        context: RPCSymbolProcessorContext,
    ): RPCServiceDeclaration.Function.Argument {
        val type = argument.type.resolve()

        return RPCServiceDeclaration.Function.Argument(
            name = argument.name?.getShortName()
                ?: codegenError<AbsentShortNameCodeGenerationException>(functionDeclaration),
            type = type,
            isVararg = argument.isVararg,
            isContextual = type.declaration.flowTypeOrNull(context) != null,
        )
    }

    private fun handleServiceField(
        propertyDeclaration: KSPropertyDeclaration,
        context: RPCSymbolProcessorContext
    ): RPCServiceDeclaration.FlowField {
        if (propertyDeclaration.isMutable) {
            codegenError<MutableFieldCodeGenerationException>(propertyDeclaration)
        }

        if (propertyDeclaration.extensionReceiver != null) {
            codegenError<FieldExtensionReceiverCodeGenerationException>(propertyDeclaration)
        }

        val type = propertyDeclaration.type.resolve()

        val flowType = type.declaration.flowTypeOrNull(context)
            ?: codegenError<ForbiddenFieldTypeCodeGenerationException>(propertyDeclaration)

        val isEager = propertyDeclaration.annotations.any { it.annotationType.resolve() == context.rpcEagerProperty }

        val name = propertyDeclaration.simpleName.asString()

        if (name.contains("$")) {
            codegenError<InvalidFieldNameCodeGenerationException>(propertyDeclaration)
        }

        return RPCServiceDeclaration.FlowField(name, type, flowType, isEager)
    }

    private fun KSDeclaration.flowTypeOrNull(context: RPCSymbolProcessorContext): RPCServiceDeclaration.FlowField.Type? {
        return when (this) {
            context.plainFlowType.declaration -> RPCServiceDeclaration.FlowField.Type.Plain
            context.sharedFlowType.declaration -> RPCServiceDeclaration.FlowField.Type.Shared
            context.stateFlowType.declaration -> RPCServiceDeclaration.FlowField.Type.State
            else -> null
        }
    }

    companion object {
        internal val DENY_LIST_SERVICE_MODIFIERS = setOf(
            Modifier.PRIVATE,
            Modifier.SEALED,
            Modifier.INTERNAL,
            Modifier.EXPECT,
            Modifier.ACTUAL,
            Modifier.EXTERNAL
        )

        internal val DENY_LIST_FUNCTION_MODIFIERS = setOf(
            Modifier.PRIVATE,
            Modifier.INTERNAL,
            Modifier.INFIX,
            Modifier.OPERATOR,
            Modifier.EXPECT,
            Modifier.ACTUAL,
            Modifier.FINAL,
            Modifier.TAILREC,
        )
    }
}
