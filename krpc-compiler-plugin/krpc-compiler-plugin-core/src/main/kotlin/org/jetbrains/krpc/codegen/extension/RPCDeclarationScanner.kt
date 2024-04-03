/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.codegen.extension

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.packageFqName

internal object RPCDeclarationScanner {
    fun scan(service: IrClass, ctx: RPCIrContext): ServiceDeclaration {
        val declarations = service.declarations.memoryOptimizedMap { declaration ->
            when (declaration) {
                is IrSimpleFunction -> {
                    if (declaration.isFakeOverride) {
                        return@memoryOptimizedMap null
                    }

                    ServiceDeclaration.Method(
                        function = declaration,
                        argumentTypes = declaration.valueParameters.memoryOptimizedMap { param ->
                            ServiceDeclaration.Method.Argument(param, param.type)
                        },
                    )
                }

                is IrProperty -> {
                    if (declaration.isFakeOverride) {
                        return@memoryOptimizedMap null
                    }

                    val symbol = declaration.getter!!.returnType.classOrNull

                    val flowType = when (symbol) {
                        ctx.flow -> ServiceDeclaration.FlowField.Kind.Plain
                        ctx.sharedFlow -> ServiceDeclaration.FlowField.Kind.Shared
                        ctx.stateFlow -> ServiceDeclaration.FlowField.Kind.State
                        else -> unsupportedDeclaration(service, declaration)
                    }

                    ServiceDeclaration.FlowField(declaration, flowType)
                }

                else -> {
                    unsupportedDeclaration(service, declaration)
                }
            }
        }

        val stubClass = ctx.getIrClassSymbol(
            packageName = service.packageFqName!!.asString(),
            name = "${service.name.asString()}$STUB_SUFFIX",
        )

        return ServiceDeclaration(
            service = service,
            stubClass = stubClass.owner,
            methods = declarations.filterIsInstance<ServiceDeclaration.Method>(),
            fields = declarations.filterIsInstance<ServiceDeclaration.FlowField>(),
        )
    }
}

private fun unsupportedDeclaration(service: IrClass, declaration: IrDeclaration): Nothing {
    error("Unsupported declaration in RPC interface ${service.name}: ${declaration.dumpKotlinLike()}")
}
