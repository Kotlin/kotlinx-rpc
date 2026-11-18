/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.types.IrType

class IrMemberAccessExpressionData(
    val dispatchReceiver: IrExpression?,
    val extensionReceiver: IrExpression?,
    val typeArguments: List<IrType>,
    val valueArguments: List<IrExpression>,
)

class IrMemberAccessExpressionBuilder {
    var dispatchReceiver: IrExpression? = null
    var extensionReceiver: IrExpression? = null

    private var valueArguments: List<IrExpression> = emptyList()
    private var typeArguments: List<IrType> = emptyList()

    val typeBuilder = TypeBuilder()

    fun types(builder: TypeBuilder.() -> Unit) {
        typeBuilder.builder()
    }

    val valueBuilder = ValueBuilder()

    fun values(builder: ValueBuilder.() -> Unit) {
        valueBuilder.builder()
    }

    inner class TypeBuilder {
        operator fun IrType.unaryPlus() {
            typeArguments += this
        }
    }
    
    inner class ValueBuilder {
        operator fun IrExpression.unaryPlus() {
            valueArguments += this
        }
    }

    fun build(): IrMemberAccessExpressionData = IrMemberAccessExpressionData(
        dispatchReceiver,
        extensionReceiver,
        typeArguments,
        valueArguments
    )
}

inline fun IrMemberAccessExpression<*>.arguments(builder: IrMemberAccessExpressionBuilder.() -> Unit) {
    IrMemberAccessExpressionBuilder().apply(builder).build().applyTo(this)
}

/**
 * Sets the arguments of [access] in the order of its parameters:
 * the dispatch receiver, the extension receiver, and then the value arguments.
 */
fun IrMemberAccessExpressionData.applyTo(access: IrMemberAccessExpression<*>) {
    var offset = if (dispatchReceiver != null) {
        access.arguments[0] = dispatchReceiver
        1
    } else {
        0
    }

    offset += if (extensionReceiver != null) {
        access.arguments[offset] = extensionReceiver
        1
    } else {
        0
    }

    valueArguments.forEachIndexed { index, irExpression ->
        access.arguments[offset + index] = irExpression
    }

    typeArguments.forEachIndexed { index, irType ->
        access.typeArguments[index] = irType
    }
}
