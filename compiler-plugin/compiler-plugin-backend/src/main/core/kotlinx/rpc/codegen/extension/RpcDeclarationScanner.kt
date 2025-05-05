/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import kotlinx.rpc.codegen.VersionSpecificApi
import kotlinx.rpc.codegen.common.RpcNames
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.fir.java.enhancement.FirEmptyJavaDeclarationList.declarations
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.dumpKotlinLike

/**
 * This class scans user declared RPC service
 * and returns all necessary information for code generation by [RpcStubGenerator].
 *
 * Some checks are preformed during scanning,
 * but all user-friendly errors are expected to be thrown by frontend plugins
 */
internal object RpcDeclarationScanner {
    fun scanServiceDeclaration(service: IrClass, ctx: RpcIrContext, logger: MessageCollector): ServiceDeclaration {
        var stubClass: IrClass? = null

        val declarations = service.declarations.memoryOptimizedMap { declaration ->
            when (declaration) {
                is IrSimpleFunction -> {
                    if (declaration.isFakeOverride) {
                        return@memoryOptimizedMap null
                    }

                    ServiceDeclaration.Method(
                        function = declaration,
                        arguments = ctx.versionSpecificApi.run {
                            declaration.valueParametersVS().memoryOptimizedMap { param ->
                                ServiceDeclaration.Method.Argument(param, param.type)
                            }
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
                        else -> return@memoryOptimizedMap unsupportedDeclaration(service, declaration, logger)
                    }

                    ServiceDeclaration.FlowField(declaration, flowType)
                }

                is IrClass -> {
                    if (declaration.name == RpcNames.SERVICE_STUB_NAME) {
                        stubClass = declaration
                        return@memoryOptimizedMap null
                    }

                    unsupportedDeclaration(service, declaration, logger)
                }

                else -> {
                    unsupportedDeclaration(service, declaration, logger)
                }
            }
        }

        val stubClassNotNull = stubClass
            ?: error("Expected generated stub class to be present in ${service.name.asString()}. FIR failed to do so.")

        return ServiceDeclaration(
            service = service,
            stubClass = stubClassNotNull,
            methods = declarations.filterIsInstance<ServiceDeclaration.Method>(),
            fields = declarations.filterIsInstance<ServiceDeclaration.FlowField>(),
        )
    }
}

private fun unsupportedDeclaration(service: IrClass, declaration: IrDeclaration, logger: MessageCollector): Nothing? {
    logger.report(
        severity = CompilerMessageSeverity.LOGGING,
        message = "Unsupported declaration in RemoteService interface ${service.name}: ${declaration.dumpKotlinLike()}",
    )

    return null
}
