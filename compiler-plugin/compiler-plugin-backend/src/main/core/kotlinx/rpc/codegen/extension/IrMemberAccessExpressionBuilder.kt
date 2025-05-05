/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import kotlinx.rpc.codegen.VersionSpecificApi
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.types.IrType

class IrMemberAccessExpressionData(
    val dispatchReceiver: IrExpression?,
    val extensionReceiver: IrExpression?,
    val typeArguments: List<IrType>,
    val valueArguments: List<IrExpression>,
)

class IrMemberAccessExpressionBuilder(private val vsApi: VersionSpecificApi) {
    var dispatchReceiver: IrExpression? = null
    var extensionReceiver: IrExpression? = null

    private var valueArguments: List<IrExpression> = emptyList()
    private var typeArguments: List<IrType> = emptyList()

    fun types(builder: TypeBuilder.() -> Unit) {
        TypeBuilder().builder()
    }

    fun values(builder: ValueBuilder.() -> Unit) {
        ValueBuilder().builder()
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

inline fun IrMemberAccessExpression<*>.arguments(vsApi: VersionSpecificApi, builder: IrMemberAccessExpressionBuilder.() -> Unit) {
    IrMemberAccessExpressionBuilder(vsApi).apply(builder).build().let { data ->
        with(vsApi) {
            data.buildFor(this@arguments)
        }
    }
}
