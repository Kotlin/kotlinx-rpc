package org.jetbrains.krpc.codegen

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.impl.IrGetObjectValueImpl
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.irCall
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer

internal class RPCIrServiceInfoFunctionCallsTransformer(
    private val logger: MessageCollector,
) : IrElementTransformer<RPCIrTransformerContext> {
    override fun visitCall(expression: IrCall, data: RPCIrTransformerContext): IrElement {
        val call = super.visitCall(expression, data)

        return when {
            data.rpcServiceOfStubFunction.contains(expression.symbol) -> {
                tryReplace(expression, data.generatedRpcServiceOfFunctions, data)
            }

            data.rpcServiceMethodOfStubFunctions.contains(expression.symbol) -> {
                tryReplace(expression, data.generatedServiceMethodOfFunctions, data)
            }

            else -> call
        }
    }

    private fun tryReplace(expression: IrCall, replacements: List<IrSimpleFunction>, data: RPCIrTransformerContext): IrCall {
        return when (expression.valueArguments.size) {
            1 -> {
                val metadataObject = data.krpcMetadataObject
                val method = replacements.single { it.valueParameters.size == 1 }

                irCall(expression, method).apply {
                    dispatchReceiver = IrGetObjectValueImpl(startOffset, endOffset, metadataObject.owner.typeWith(), metadataObject)

                    putValueArgument(0, expression.getValueArgument(0))
                }
            }

            2 -> {
                val metadataObject = data.krpcMetadataObject
                val method = replacements.single { it.valueParameters.size == 2 }

                irCall(expression, method).apply {
                    dispatchReceiver = IrGetObjectValueImpl(startOffset, endOffset, metadataObject.owner.typeWith(), metadataObject)

                    putValueArgument(0, expression.getValueArgument(0))
                    putValueArgument(1, expression.getValueArgument(1))
                }
            }

            else -> error("Unexpected rpcServiceOfStubFunction call")
        }
    }
}
