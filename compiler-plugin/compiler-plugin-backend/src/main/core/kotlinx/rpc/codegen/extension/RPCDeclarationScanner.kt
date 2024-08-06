/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package kotlinx.rpc.codegen.extension

import kotlinx.rpc.codegen.common.RpcNames
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.dumpKotlinLike

/**
 * This class scans user declared RPC service
 * and returns all necessary information for code generation by [RPCStubGenerator].
 *
 * Some checks are preformed during scanning,
 * but all user-friendly errors are expected to be thrown by frontend plugins
 */
internal object RPCDeclarationScanner {
    fun scanServiceDeclaration(service: IrClass, ctx: RPCIrContext): ServiceDeclaration {
        var stubClass: IrClass? = null

        val declarations = service.declarations.memoryOptimizedMap { declaration ->
            when (declaration) {
                is IrSimpleFunction -> {
                    if (declaration.isFakeOverride) {
                        return@memoryOptimizedMap null
                    }

                    ServiceDeclaration.Method(
                        function = declaration,
                        arguments = declaration.valueParameters.memoryOptimizedMap { param ->
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

                is IrClass -> {
                    if (declaration.name == RpcNames.SERVICE_STUB_NAME) {
                        stubClass = declaration
                        return@memoryOptimizedMap null
                    }

                    unsupportedDeclaration(service, declaration)
                }

                else -> {
                    unsupportedDeclaration(service, declaration)
                }
            }
        }

        val stubClassNotNull = stubClass
            ?: error("Expected ${RpcNames.SERVICE_STUB_NAME} nested declaration in ${service.name}")

        return ServiceDeclaration(
            service = service,
            stubClass = stubClassNotNull,
            methods = declarations.filterIsInstance<ServiceDeclaration.Method>(),
            fields = declarations.filterIsInstance<ServiceDeclaration.FlowField>(),
        )
    }
}

private fun unsupportedDeclaration(service: IrClass, declaration: IrDeclaration): Nothing {
    error("Unsupported declaration in RPC interface ${service.name}: ${declaration.dumpKotlinLike()}")
}
