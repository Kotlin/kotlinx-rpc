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
    // null entries are skipped, leaving the argument unset (the callee's default value applies)
    val valueArguments: List<IrExpression?>,
)

class IrMemberAccessExpressionBuilder {
    var dispatchReceiver: IrExpression? = null
    var extensionReceiver: IrExpression? = null

    private var valueArguments: List<IrExpression?> = emptyList()
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

        /**
         * Leaves the argument at the current position unset.
         * Only valid for parameters with a default value.
         */
        fun skip() {
            valueArguments += null
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
    IrMemberAccessExpressionBuilder().apply(builder).build().let { data ->
        with(vsApi) {
            data.buildForVS(this@arguments)
        }
    }
}
