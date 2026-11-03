/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import kotlinx.rpc.codegen.common.RpcClassId
import kotlinx.rpc.codegen.common.RpcNames
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.expressions.impl.IrGetObjectValueImpl
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isInterface
import org.jetbrains.kotlin.ir.util.parentAsClass

private typealias StubGeneratorFactory = (ServiceDeclaration, RpcIrContext, MessageCollector) -> RpcStubGenerator

internal class RpcIrServiceProcessor(
    private val logger: MessageCollector,
) {
    fun visitClass(declaration: IrClass, data: RpcIrContext) {
        if (!declaration.isInterface) {
            return
        }

        val stubGenerator = declaration.stubGeneratorFactory() ?: return
        processService(declaration, data, stubGenerator)
    }

    private fun IrClass.stubGeneratorFactory(): StubGeneratorFactory? {
        return when {
            hasAnnotation(RpcClassId.krpcAnnotation) -> ::KrpcStubGenerator
            hasAnnotation(RpcClassId.grpcAnnotation) -> ::GrpcStubGenerator
            isRpcService() -> ::RpcStubGenerator
            else -> null
        }
    }

    private fun IrClass.isRpcService(): Boolean {
        return annotations.any { annotation ->
            annotation.symbol.owner.parentAsClass.hasAnnotation(RpcClassId.rpcMetaAnnotation)
        }
    }

    private fun processService(service: IrClass, context: RpcIrContext, stubGenerator: StubGeneratorFactory) {
        val declaration = RpcDeclarationScanner.scanServiceDeclaration(service, context, logger)
        stubGenerator(declaration, context, logger).generate()
        service.implementGetDescriptor(declaration, context)
    }

    private fun IrClass.implementGetDescriptor(
        declaration: ServiceDeclaration,
        ctx: RpcIrContext,
    ) {
        val descriptor = declaration.stubClass.companionObject()
            ?: error("Expected companion object in ${declaration.stubClass.name}")

        val property = declarations.filterIsInstance<IrProperty>().singleOrNull {
            it.name == RpcNames.SERVICE_DESCRIPTOR_PROPERTY_NAME && !it.isFakeOverride
        } ?: error("Expected a generated '${RpcNames.SERVICE_DESCRIPTOR_PROPERTY_NAME}' property in ${name.asString()}")

        val getter = property.getter
            ?: error("Expected a getter for '${RpcNames.SERVICE_DESCRIPTOR_PROPERTY_NAME}' in ${name.asString()}")

        // fir2ir creates a backing field for the generated property, but interfaces can't have fields
        property.backingField = null

        getter.body = irBuilder(ctx.pluginContext, getter.symbol).irBlockBody {
            +irReturn(IrGetObjectValueImpl(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = descriptor.symbol.defaultType,
                symbol = descriptor.symbol,
            ))
        }
    }
}
