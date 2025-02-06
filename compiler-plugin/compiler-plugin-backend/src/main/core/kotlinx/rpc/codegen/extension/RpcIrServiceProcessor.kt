/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import kotlinx.rpc.codegen.common.RpcClassId
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isInterface
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer

internal class RpcIrServiceProcessor(
    @Suppress("unused")
    private val logger: MessageCollector,
) : IrElementTransformer<RpcIrContext> {
    override fun visitClass(declaration: IrClass, data: RpcIrContext): IrStatement {
        if (declaration.hasAnnotation(RpcClassId.rpcAnnotation) && declaration.isInterface) {
            processService(declaration, data)
        }

        return super.visitClass(declaration, data)
    }

    private fun processService(service: IrClass, context: RpcIrContext) {
        val declaration = RpcDeclarationScanner.scanServiceDeclaration(service, context, logger)
        RpcStubGenerator(declaration, context, logger).generate()
    }
}
