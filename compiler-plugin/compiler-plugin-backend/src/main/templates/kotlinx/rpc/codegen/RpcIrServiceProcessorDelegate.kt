/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.extension.RpcIrContext
import kotlinx.rpc.codegen.extension.RpcIrServiceProcessor
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
//##csm RpcIrServiceProcessorDelegate.kt-import
//##csm specific=[2.0.0...2.1.21]
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
//##csm /specific
//##csm default
import org.jetbrains.kotlin.ir.visitors.IrTransformer
//##csm /default
//##csm /RpcIrServiceProcessorDelegate.kt-import

//##csm RpcIrServiceProcessorDelegate
//##csm specific=[2.0.0...2.1.21]
internal class RpcIrServiceProcessorDelegate(
    val processor: RpcIrServiceProcessor,
) : IrElementTransformer<RpcIrContext> {
    override fun visitClass(declaration: IrClass, data: RpcIrContext): IrStatement {
        processor.visitClass(declaration, data)

        return super.visitClass(declaration, data)
    }
}
//##csm /specific
//##csm default
internal class RpcIrServiceProcessorDelegate(
    val processor: RpcIrServiceProcessor,
) : IrTransformer<RpcIrContext>() {
    override fun visitClass(declaration: IrClass, data: RpcIrContext): IrStatement {
        processor.visitClass(declaration, data)

        return super.visitClass(declaration, data)
    }
}
//##csm /default
//##csm /RpcIrServiceProcessorDelegate
