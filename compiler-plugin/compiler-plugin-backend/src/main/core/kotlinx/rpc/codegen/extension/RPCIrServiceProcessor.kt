/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import kotlinx.rpc.codegen.common.RpcClassId
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer

internal class RPCIrServiceProcessor(
    @Suppress("unused")
    private val logger: MessageCollector,
) : IrElementTransformer<RPCIrContext> {
    override fun visitClass(declaration: IrClass, data: RPCIrContext): IrStatement {
        if (declaration.hasAnnotation(RpcClassId.rpcAnnotation)) {
            processService(declaration, data)
        }

        return super.visitClass(declaration, data)
    }

    private fun processService(service: IrClass, context: RPCIrContext) {
        val declaration = RPCDeclarationScanner.scanServiceDeclaration(service, context, logger)
        RPCStubGenerator(declaration, context, logger).generate()
    }
}
