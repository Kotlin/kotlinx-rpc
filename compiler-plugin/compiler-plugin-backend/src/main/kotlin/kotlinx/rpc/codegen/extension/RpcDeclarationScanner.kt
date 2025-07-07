/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import kotlinx.rpc.codegen.common.RpcNames
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.hasDefaultValue

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
                                ServiceDeclaration.Method.Argument(param, param.type, param.hasDefaultValue())
                            }
                        },
                    )
                }

                is IrProperty -> {
                    if (declaration.isFakeOverride) {
                        return@memoryOptimizedMap null
                    }

                    error(
                        "Fields are not supported in @Rpc services, this error should be caught by frontend. " +
                                "Please report this issue to the kotlinx-rpc maintainers."
                    )
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
            methods = declarations.filterNotNull(),
        )
    }
}

private fun unsupportedDeclaration(service: IrClass, declaration: IrDeclaration, logger: MessageCollector): Nothing? {
    logger.report(
        severity = CompilerMessageSeverity.LOGGING,
        message = "Unsupported declaration in @Rpc interface ${service.name}: ${declaration.dumpKotlinLike()}",
    )

    return null
}
