/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import kotlinx.rpc.codegen.common.RpcClassId
import kotlinx.rpc.codegen.common.RpcNames
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.Modality
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
import org.jetbrains.kotlin.ir.util.properties

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
        service.implementCompanionGetters(declaration, context)
        stubGenerator(declaration, context, logger).generate()
    }

    private fun IrClass.implementCompanionGetters(
        declaration: ServiceDeclaration,
        ctx: RpcIrContext,
    ) {
        val companion = declaration.stubClass.companionObject()
            ?: error("Expected companion object in ${declaration.stubClass.name}")

        listOf(
            RpcNames.SERVICE_DESCRIPTOR_PROPERTY_NAME,
            RpcNames.SERVICE_FACTORY_PROPERTY_NAME,
        ).forEach { propertyName ->
            val property = declarations.filterIsInstance<IrProperty>().singleOrNull {
                it.name == propertyName && !it.isFakeOverride
            } ?: error("Expected a generated '$propertyName' property in ${name.asString()}")

            // fir2ir creates a backing field for the generated property, but interfaces can't have fields
            property.backingField = null

            val rpcServiceProperty = ctx.rpcService.owner.properties.single { it.name == propertyName }
            if (rpcServiceProperty.symbol !in property.overriddenSymbols) {
                property.overriddenSymbols += rpcServiceProperty.symbol
            }

            val getter = property.getter
                ?: error("Expected a getter for '$propertyName' in ${name.asString()}")

            val rpcServiceGetter = rpcServiceProperty.getterOrFail.symbol
            if (rpcServiceGetter !in getter.overriddenSymbols) {
                getter.overriddenSymbols += rpcServiceGetter
            }

            property.modality = Modality.OPEN
            getter.modality = Modality.OPEN

            getter.body = irBuilder(ctx.pluginContext, getter.symbol).irBlockBody {
                +irReturn(IrGetObjectValueImpl(
                    startOffset = UNDEFINED_OFFSET,
                    endOffset = UNDEFINED_OFFSET,
                    type = companion.symbol.defaultType,
                    symbol = companion.symbol,
                ))
            }
        }
    }
}
