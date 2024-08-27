/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.isInterface
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer

internal class RPCIrServiceProcessor(
    @Suppress("unused")
    private val logger: MessageCollector,
) : IrElementTransformer<RPCIrContext> {
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun visitClass(declaration: IrClass, context: RPCIrContext): IrStatement {
        if (declaration.isInterface && declaration.superTypes.contains(context.rpc.defaultType)) {
            processService(declaration, context)
        }

        return super.visitClass(declaration, context)
    }

    private fun processService(service: IrClass, context: RPCIrContext) {
        val declaration = RPCDeclarationScanner.scanServiceDeclaration(service, context, logger)
        RPCStubGenerator(declaration, context, logger).generate()
    }
}
