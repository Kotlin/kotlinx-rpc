package org.jetbrains.krpc.codegen

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*

private val ws = Regex("\\s")

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
        val rpc = resolver.getClassDeclarationByName("org.jetbrains.krpc.RPC")?.asType(typeArguments = emptyList())
            ?: codegenError("Could not find org.jetbrains.krpc.RPC interface")

        services = sequence {
            resolver.getAllFiles().forEach { file ->
                file.declarations.forEach { declaration ->
                    if (declaration is KSClassDeclaration && declaration.classKind == ClassKind.INTERFACE && declaration.superTypes.find { it.resolve() == rpc } != null) {
                        yield(processService(serviceDeclaration = declaration, file = file))
                    }
                }
            }
        }

        return emptyList()
    }

    private fun processService(serviceDeclaration: KSClassDeclaration, file: KSFile): RPCServiceDeclaration {
        if (serviceDeclaration.getDeclaredProperties().count() != 0) {
            codegenError("RPC Service can not have declared properties", serviceDeclaration)
        }

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
            processServiceFunction(it)
        }

        return RPCServiceDeclaration(
            simpleName = serviceDeclaration.simpleName.asString(),
            fullName = serviceDeclaration.qualifiedName?.asString() ?: codegenError(
                "Expected service qualified name",
                serviceDeclaration
            ),
            functions = processedFunctions,
            file = file,
        )
    }

    private fun processServiceFunction(functionDeclaration: KSFunctionDeclaration): RPCServiceDeclaration.Function {
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

        return RPCServiceDeclaration.Function(
            name = functionDeclaration.simpleName.getShortName(),
            argumentTypes = functionDeclaration.parameters.map { processFunctionArgument(functionDeclaration, it) },
            returnType = functionDeclaration.returnType?.resolve(), // todo validation
        )
    }

    private fun processFunctionArgument(
        functionDeclaration: KSFunctionDeclaration,
        argument: KSValueParameter
    ): RPCServiceDeclaration.Function.Argument {
        return RPCServiceDeclaration.Function.Argument(
            name = argument.name?.getShortName() ?: codegenError(
                "Expected function argument name",
                functionDeclaration
            ),
            type = argument.type.resolve(),
            isVararg = argument.isVararg,
        )
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
