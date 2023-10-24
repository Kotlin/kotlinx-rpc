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
                        yield(processService(declaration, file, context))
                    }
                }
            }
        }

        return emptyList()
    }

    private fun processService(
        serviceDeclaration: KSClassDeclaration,
        file: KSFile,
        context: RPCSymbolProcessorContext,
    ): RPCServiceDeclaration {
        if (serviceDeclaration.typeParameters.isNotEmpty()) {
            codegenError("RPC Service can not have type parameters", serviceDeclaration)
        }

        if (serviceDeclaration.modifiers.intersect(DENY_LIST_SERVICE_MODIFIERS).isNotEmpty()) {
            codegenError(
                "RPC Service can not have any of these modifiers: ${DENY_LIST_SERVICE_MODIFIERS.joinToString()}",
                serviceDeclaration
            )
        }

        val processedFunctions = serviceDeclaration.getDeclaredFunctions().toList().map {
            processServiceFunction(it, context)
        }

        val processedProperties = serviceDeclaration.getDeclaredProperties().toList().map {
            processServiceProperty(it, context)
        }

        return RPCServiceDeclaration(
            simpleName = serviceDeclaration.simpleName.asString(),
            fullName = serviceDeclaration.qualifiedName?.asString() ?: codegenError(
                "Expected service qualified name",
                serviceDeclaration
            ),
            functions = processedFunctions,
            properties = processedProperties,
            file = file,
        )
    }

    private fun processServiceFunction(
        functionDeclaration: KSFunctionDeclaration,
        context: RPCSymbolProcessorContext,
    ): RPCServiceDeclaration.Function {
        if (functionDeclaration.typeParameters.isNotEmpty()) {
            codegenError("RPC Service function can not have type parameters", functionDeclaration)
        }

        if (functionDeclaration.extensionReceiver != null) {
            codegenError("RPC Service function can not have extension receiver", functionDeclaration)
        }

        if (functionDeclaration.modifiers.intersect(DENY_LIST_FUNCTION_MODIFIERS).isNotEmpty()) {
            codegenError(
                "RPC Service function can not have any of these modifiers: ${DENY_LIST_FUNCTION_MODIFIERS.joinToString()}",
                functionDeclaration
            )
        }

        if (!functionDeclaration.modifiers.contains(Modifier.SUSPEND)) {
            codegenError("RPC Service function must have 'suspend' modifier")
        }

        if (!functionDeclaration.isAbstract) {
            codegenError("RPC Service function can not have default implementation", functionDeclaration)
        }

        val returnType = functionDeclaration.returnType?.resolve()
            ?: error("Failed to resolve returnType for $functionDeclaration")

        val name = functionDeclaration.simpleName.getShortName()

        if (name.contains("$")) {
            codegenError("RPC Service method's name cannot contain \$ symbol", functionDeclaration)
        }

        return RPCServiceDeclaration.Function(
            name = name,
            argumentTypes = functionDeclaration.parameters.map { processFunctionArgument(functionDeclaration, it, context) },
            returnType = returnType,
        )
    }

    private fun processFunctionArgument(
        functionDeclaration: KSFunctionDeclaration,
        argument: KSValueParameter,
        context: RPCSymbolProcessorContext,
    ): RPCServiceDeclaration.Function.Argument {
        val type = argument.type.resolve()

        return RPCServiceDeclaration.Function.Argument(
            name = argument.name?.getShortName() ?: codegenError(
                message = "Expected function argument name",
                service = functionDeclaration,
            ),
            type = type,
            isVararg = argument.isVararg,
            isContextual = type.declaration.flowTypeOrNull(context) != null,
        )
    }

    private fun processServiceProperty(
        propertyDeclaration: KSPropertyDeclaration,
        context: RPCSymbolProcessorContext
    ): RPCServiceDeclaration.FlowProperty {
        if (propertyDeclaration.isMutable) {
            codegenError("RPC Service field can not be mutable", propertyDeclaration)
        }

        if (propertyDeclaration.extensionReceiver != null) {
            codegenError("RPC Service field can not have extension receiver", propertyDeclaration)
        }

        val type = propertyDeclaration.type.resolve()

        val flowType = type.declaration.flowTypeOrNull(context)
            ?: codegenError("Only Flow, SharedFlow and StateFlow properties are allowed in RPC Service interfaces", propertyDeclaration)

        val isEager = propertyDeclaration.annotations.any { it.annotationType.resolve() == context.rpcEagerProperty }

        val name = propertyDeclaration.simpleName.asString()

        if (name.contains("$")) {
            codegenError("RPC Service field's name cannot contain \$ symbol", propertyDeclaration)
        }

        return RPCServiceDeclaration.FlowProperty(name, type, flowType, isEager)
    }

    private fun KSDeclaration.flowTypeOrNull(context: RPCSymbolProcessorContext): RPCServiceDeclaration.FlowProperty.Type? {
        return when (this) {
            context.plainFlowType.declaration -> RPCServiceDeclaration.FlowProperty.Type.Plain
            context.sharedFlowType.declaration -> RPCServiceDeclaration.FlowProperty.Type.Shared
            context.stateFlowType.declaration -> RPCServiceDeclaration.FlowProperty.Type.State
            else -> null
        }
    }

    companion object {
        private val DENY_LIST_SERVICE_MODIFIERS = setOf(
            Modifier.PRIVATE,
            Modifier.SEALED,
            Modifier.INTERNAL,
            Modifier.EXPECT,
            Modifier.ACTUAL,
            Modifier.EXTERNAL
        )

        private val DENY_LIST_FUNCTION_MODIFIERS = setOf(
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
