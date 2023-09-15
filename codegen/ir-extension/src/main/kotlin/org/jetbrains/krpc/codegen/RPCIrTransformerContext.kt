package org.jetbrains.krpc.codegen

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

internal class RPCIrTransformerContext(
    private val logger: MessageCollector,
    private val pluginContext: IrPluginContext,
    private val options: RPCOptions,
) {
    private val rpcStubCallAnnotation = getRpcClassDeclaration("RPCStubCall")

    val rpcServiceOfStubFunction = getRpcFunctionDeclaration("rpcServiceOf", "org.jetbrains.krpc.client") { symbol ->
        symbol.owner.hasAnnotation(rpcStubCallAnnotation)
    }
    val rpcServiceMethodOfStubFunctions = getRpcFunctionDeclaration("serviceMethodOf", "org.jetbrains.krpc.server") { symbol ->
        symbol.owner.hasAnnotation(rpcStubCallAnnotation)
    }

    val krpcMetadataObject = getRpcClassDeclaration("kRPCMetadata_${options.moduleId}")

    val generatedRpcServiceOfFunctions = krpcMetadataObject.owner.functions.filter {
        it.name.asString() == "rpcServiceOf"
    }.toList().takeIf { it.isNotEmpty() } ?: error("Expected 'rpcServiceOf' functions in kRPCMetadata_${options.moduleId} object")

    val generatedServiceMethodOfFunctions = krpcMetadataObject.owner.functions.filter {
        it.name.asString() == "serviceMethodOf"
    }.toList().takeIf { it.isNotEmpty() } ?: error("Expected 'serviceMethodOf' functions in kRPCMetadata_${options.moduleId} object")

    fun generatedClientClassType(serviceDeclaration: IrClass): IrClassSymbol {
        val name = serviceDeclaration.kotlinFqName.shortName()
        return getRpcClassDeclaration("${name}Client")
    }

    private fun getRpcClassDeclaration(name: String, packageName: String = "org.jetbrains.krpc"): IrClassSymbol {
        return getClassDeclaration(packageName, name)
    }

    private fun getClassDeclaration(packageName: String, name: String): IrClassSymbol {
        return getClassDeclarationOrNull(packageName, name) ?: error("Unable to find $packageName.$name declaration")
    }

    private fun getClassDeclarationOrNull(packageName: String, name: String): IrClassSymbol? {
        return pluginContext.referenceClass(
            ClassId(
                FqName(packageName),
                FqName(name),
                false
            )
        )
    }

    private fun getRpcFunctionDeclaration(
        name: String,
        packageName: String = "org.jetbrains.krpc",
        filter: (IrSimpleFunctionSymbol) -> Boolean
    ): Set<IrSimpleFunctionSymbol> {
        return getTopLevelFunctionDeclaration(packageName = packageName, name, filter)
    }

    @Suppress("SameParameterValue")
    private fun getTopLevelFunctionDeclaration(
        packageName: String,
        name: String,
        filter: (IrSimpleFunctionSymbol) -> Boolean
    ): Set<IrSimpleFunctionSymbol> {
        return pluginContext.referenceFunctions(CallableId(FqName(packageName), Name.identifier(name))).filter(filter).toSet()
    }
}
