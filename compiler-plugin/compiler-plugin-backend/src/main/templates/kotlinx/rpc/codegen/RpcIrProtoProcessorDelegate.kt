/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.extension.RpcIrContext
import kotlinx.rpc.codegen.extension.RpcIrProtoProcessor
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
//##csm RpcIrProtoProcessorDelegate.kt-import
//##csm specific=[2.0.0...2.1.21]
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
//##csm /specific
//##csm default
import org.jetbrains.kotlin.ir.visitors.IrTransformer
//##csm /default
//##csm /RpcIrProtoProcessorDelegate.kt-import

//##csm RpcIrProtoProcessorDelegate
//##csm specific=[2.0.0...2.1.21]
internal class RpcIrProtoProcessorDelegate(
    val processor: RpcIrProtoProcessor,
) : IrElementTransformer<RpcIrContext> {
    override fun visitClass(declaration: IrClass, data: RpcIrContext): IrStatement {
        processor.visitClass(declaration, data)

        return super.visitClass(declaration, data)
    }
}
//##csm /specific
//##csm default
internal class RpcIrProtoProcessorDelegate(
    val processor: RpcIrProtoProcessor,
) : IrTransformer<RpcIrContext>() {
    override fun visitClass(declaration: IrClass, data: RpcIrContext): IrStatement {
        processor.visitClass(declaration, data)

        return super.visitClass(declaration, data)
    }
}
//##csm /default
//##csm /RpcIrProtoProcessorDelegate
