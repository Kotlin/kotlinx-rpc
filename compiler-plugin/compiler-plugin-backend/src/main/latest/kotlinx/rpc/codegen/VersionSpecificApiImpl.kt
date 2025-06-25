/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.extension.IrMemberAccessExpressionData
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.createExtensionReceiver
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.ir.builders.declarations.IrFieldBuilder
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.copyTo
import org.jetbrains.kotlin.ir.util.isNullable
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.platform.isJs
import org.jetbrains.kotlin.platform.isWasm

object VersionSpecificApiImpl : VersionSpecificApi {
    override fun isJs(platform: TargetPlatform?): Boolean {
        return platform.isJs()
    }

    override fun isWasm(platform: TargetPlatform?): Boolean {
        return platform.isWasm()
    }

    override var IrFieldBuilder.isFinalVS: Boolean
        get() = isFinal
        set(value) {
            isFinal = value
        }

    override var IrCall.originVS: IrStatementOrigin?
        get() = origin
        set(value) {
            origin = value
        }

    override var IrConstructor.isPrimaryVS: Boolean
        get() = isPrimary
        set(value) {
            isPrimary = value
        }

    override val IrConstructor.parametersVS: List<IrValueParameter>
        get() = parameters

    override val IrConstructorCall.argumentsVS: List<IrExpression?>
        get() = arguments.toList()

    override fun IrType.isNullableVS(): Boolean {
        return isNullable()
    }

    override fun referenceClass(context: IrPluginContext, packageName: String, name: String): IrClassSymbol? {
        return context.referenceClass(
            ClassId(
                FqName(packageName),
                FqName(name),
                false
            )
        )
    }

    override fun referenceFunctions(
        context: IrPluginContext,
        packageName: String,
        name: String,
    ): Collection<IrSimpleFunctionSymbol> {
        return context.referenceFunctions(
            CallableId(
                FqName(packageName),
                Name.identifier(name),
            )
        )
    }

    override fun IrValueParameter.copyToVS(irFunction: IrFunction, origin: IrDeclarationOrigin): IrValueParameter {
        return copyTo(irFunction, origin)
    }

    override fun IrSimpleFunction.addExtensionReceiverVS(type: IrType, origin: IrDeclarationOrigin): IrValueParameter {
        return createExtensionReceiver(type, origin)
    }

    override val messageCollectorKey: CompilerConfigurationKey<MessageCollector>
        get() = CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY

    override fun IrCallImplVS(
        startOffset: Int,
        endOffset: Int,
        type: IrType,
        symbol: IrSimpleFunctionSymbol,
        typeArgumentsCount: Int,
        valueArgumentsCount: Int,
        origin: IrStatementOrigin?,
        superQualifierSymbol: IrClassSymbol?,
    ): IrCallImpl {
        return IrCallImpl(
            startOffset = startOffset,
            endOffset = endOffset,
            type = type,
            symbol = symbol,
            typeArgumentsCount = typeArgumentsCount,
            origin = origin,
            superQualifierSymbol = superQualifierSymbol,
        )
    }

    override fun IrConstructorCallImplVS(
        startOffset: Int,
        endOffset: Int,
        type: IrType,
        symbol: IrConstructorSymbol,
        typeArgumentsCount: Int,
        valueArgumentsCount: Int,
        constructorTypeArgumentsCount: Int,
        origin: IrStatementOrigin?,
        source: SourceElement,
    ): IrConstructorCallImpl {
        return IrConstructorCallImpl(
            startOffset = startOffset,
            endOffset = endOffset,
            type = type,
            symbol = symbol,
            typeArgumentsCount = typeArgumentsCount,
            constructorTypeArgumentsCount = constructorTypeArgumentsCount,
            origin = origin,
            source = source,
        )
    }

    override fun IrFunction.valueParametersVS(): List<IrValueParameter> {
        return parameters.filter { it.kind == IrParameterKind.Regular }
    }

    override val IrFunction.extensionReceiverParameterVS: IrValueParameter?
        get() = parameters.firstOrNull { it.kind == IrParameterKind.ExtensionReceiver }

    override var IrFunction.dispatchReceiverParameterVS: IrValueParameter?
        get() = dispatchReceiverParameter
        set(value) {
            if (value != null) {
                parameters += value
            }
        }

    override fun IrMemberAccessExpressionData.buildFor(access: IrMemberAccessExpression<*>) {
        var offset = if (dispatchReceiver != null) {
            access.arguments[0] = dispatchReceiver
            1
        } else {
            0
        }

        offset += if (extensionReceiver != null) {
            access.arguments[offset] = extensionReceiver
            1
        } else {
            0
        }

        valueArguments.forEachIndexed { index, irExpression ->
            access.arguments[offset + index] = irExpression
        }

        typeArguments.forEachIndexed { index, irType ->
            access.typeArguments[index] = irType
        }
    }
}
