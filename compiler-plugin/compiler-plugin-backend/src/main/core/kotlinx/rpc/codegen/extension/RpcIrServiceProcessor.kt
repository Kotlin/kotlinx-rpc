/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import kotlinx.rpc.codegen.common.RpcClassId
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isInterface

internal class RpcIrServiceProcessor(
    @Suppress("unused")
    private val logger: MessageCollector,
) {
    fun visitClass(declaration: IrClass, data: RpcIrContext) {
        if (declaration.hasAnnotation(RpcClassId.rpcAnnotation) && declaration.isInterface) {
            processService(declaration, data)
        }
    }

    private fun processService(service: IrClass, context: RpcIrContext) {
        val declaration = RpcDeclarationScanner.scanServiceDeclaration(service, context, logger)
        RpcStubGenerator(declaration, context, logger).generate()
    }
}
