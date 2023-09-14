package org.jetbrains.krpc.codegen

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.backend.js.utils.typeArguments
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.impl.IrGetObjectValueImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class IrExtension(private val logger: MessageCollector) : IrGenerationExtension {
    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext,
    ) {
        moduleFragment.transform(IrRpcServiceOfCallsTransformer(IrTransformerContext(pluginContext)), Unit)
    }
}

private class IrTransformerContext(val pluginContext: IrPluginContext) {
    val rpcStubCallAnnotation = getRpcClassDeclaration("RPCStubCall")

    val rpcServiceOfStubFunction = getRpcFunctionDeclaration("rpcServiceOf", "org.jetbrains.krpc.client") { symbol ->
        symbol.owner.hasAnnotation(rpcStubCallAnnotation)
    }
    val rpcServiceMethodOfStubFunction = getRpcFunctionDeclaration("serviceMethodOf", "org.jetbrains.krpc.server") { symbol ->
        symbol.owner.hasAnnotation(rpcStubCallAnnotation)
    }

    fun generatedClientClassType(serviceDeclaration: IrClass): IrClassSymbol {
        val name = serviceDeclaration.kotlinFqName.shortName()
        return getRpcClassDeclaration("${name}Client")
    }

    private fun getRpcClassDeclaration(name: String, packageName: String = "org.jetbrains.krpc"): IrClassSymbol {
        return getClassDeclaration(packageName, name)
    }

    @Suppress("SameParameterValue")
    private fun getClassDeclaration(packageName: String, name: String): IrClassSymbol {
        return pluginContext.referenceClass(
            ClassId(
                FqName(packageName),
                FqName(name),
                false
            )
        ) ?: error("Unable to find $packageName.$name declaration")
    }

    private fun getRpcFunctionDeclaration(
        name: String,
        packageName: String = "org.jetbrains.krpc",
        single: (IrSimpleFunctionSymbol) -> Boolean
    ): IrSimpleFunctionSymbol {
        return getTopLevelFunctionDeclaration(packageName = packageName, name, single)
    }

    @Suppress("SameParameterValue")
    private fun getTopLevelFunctionDeclaration(
        packageName: String,
        name: String,
        single: (IrSimpleFunctionSymbol) -> Boolean
    ): IrSimpleFunctionSymbol {
        return pluginContext.referenceFunctions(CallableId(FqName(packageName), Name.identifier(name)))
            .singleOrNull(single)
            ?: error("Unable to find single top-level $packageName.$name function")
    }
}

private class IrRpcServiceOfCallsTransformer(private val context: IrTransformerContext) : IrElementTransformer<Unit> {

    override fun visitCall(expression: IrCall, data: Unit): IrElement {
        val call = super.visitCall(expression, data)

        return when (expression.symbol) {
            this.context.rpcServiceOfStubFunction -> {
                val serviceClassSymbol = expression.typeArguments.first()?.classOrNull
                    ?: error("Expected class type parameter")

                val generatedClientClass = context.generatedClientClassType(serviceClassSymbol.owner)

                irConstructorCall(expression, generatedClientClass.constructors.first()).apply {
                    putValueArgument(0, expression.getValueArgument(0))
                }
            }

            this.context.rpcServiceMethodOfStubFunction -> {
                val serviceClassSymbol = expression.typeArguments.first()?.classOrNull
                    ?: error("Expected class type parameter")

                val generatedClientClass = context.generatedClientClassType(serviceClassSymbol.owner)
                val generatedClientClassCompanionObject = generatedClientClass.owner.companionObject()
                    ?: error("Expected generatedClientClassCompanionObject for ${generatedClientClass.owner.name.asString()}")

                val methodName = generatedClientClassCompanionObject.getSimpleFunction("methodType")
                    ?: error("Expected methodName function for ${generatedClientClassCompanionObject.kotlinFqName.asString()}")

                irCall(expression, methodName).apply {
                    dispatchReceiver = IrGetObjectValueImpl(startOffset, endOffset, generatedClientClassCompanionObject.typeWith(), generatedClientClassCompanionObject.symbol)

                    putValueArgument(0, expression.getValueArgument(0))
                }
            }

            else -> call
        }
    }
}
