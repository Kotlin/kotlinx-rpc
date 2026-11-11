/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.extension.IrMemberAccessExpressionData
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.ir.builders.declarations.IrFieldBuilder
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
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
import kotlin.reflect.KClass
import kotlin.reflect.safeCast

object VersionSpecificApiImpl : VersionSpecificApi {
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

    override val IrConstructor.parametersVS: List<IrValueParameter>
        get() = parameters

    override val IrConstructorCall.argumentsVS: List<IrExpression?>
        get() = arguments.toList()

    override fun IrType.isNullableVS(): Boolean {
        return isNullable()
    }

    override fun referenceBuiltinClassVS(
        context: IrPluginContext,
        packageName: String,
        name: String
    ): IrClassSymbol? {
        return context.findClassCompat(ClassId(FqName(packageName), FqName(name), false), from = null)
    }

    override fun referenceClassVS(
        context: IrPluginContext,
        packageName: String,
        name: String,
        from: IrFile,
    ): IrClassSymbol? {
        return context.findClassCompat(ClassId(FqName(packageName), FqName(name), false), from)
    }

    override fun referenceBuiltinFunctionsVS(
        context: IrPluginContext,
        packageName: String,
        name: String,
    ): Collection<IrSimpleFunctionSymbol> {
        return context.findFunctionsCompat(CallableId(FqName(packageName), Name.identifier(name)), from = null)
    }

    override fun referenceFunctionsVS(
        context: IrPluginContext,
        packageName: String,
        name: String,
        from: IrFile,
    ): Collection<IrSimpleFunctionSymbol> {
        return context.findFunctionsCompat(CallableId(FqName(packageName), Name.identifier(name)), from)
    }

    override fun IrValueParameter.copyToVS(irFunction: IrFunction, origin: IrDeclarationOrigin): IrValueParameter {
        return copyTo(irFunction, origin)
    }

    override val messageCollectorKeyVS: CompilerConfigurationKey<MessageCollector>
        get() = messageCollectorKeyCompat

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

    override var IrFunction.dispatchReceiverParameterVS: IrValueParameter?
        get() = dispatchReceiverParameter
        set(value) {
            if (value != null) {
                parameters += value
            }
        }

    override fun IrMemberAccessExpressionData.buildForVS(access: IrMemberAccessExpression<*>) {
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

    override fun IrAnnotationVS(
        startOffset: Int,
        endOffset: Int,
        type: IrType,
        symbol: IrConstructorSymbol,
        typeArgumentsCount: Int,
        valueArgumentsCount: Int,
        constructorTypeArgumentsCount: Int,
    ): IrMemberAccessExpression<*> {
        return irAnnotationCompat(startOffset, endOffset, type, symbol, typeArgumentsCount, constructorTypeArgumentsCount)
    }

    override fun IrMutableAnnotationContainer.addAnnotationVS(annotation: IrMemberAccessExpression<*>) {
        addAnnotationCompat(annotation)
    }

    override fun addMetadataVisibleAnnotationVS(
        context: IrPluginContext,
        declaration: IrDeclaration,
        annotation: IrMemberAccessExpression<*>,
    ) {
        context.addMetadataVisibleAnnotationCompat(declaration, annotation)
    }

    override fun IrConstructorCall.valueArgumentAtVS(index: Int): IrExpression? {
        return arguments.getOrNull(index)
    }

    override fun IrMemberAccessExpression<*>.getValueArgumentVS(name: Name): IrExpression? {
        val constructorCall = this as IrConstructorCall
        val parameterIndex = constructorCall.symbol.owner.parameters
            .firstOrNull { it.name == name }
            ?.indexInParameters
            ?: return null

        return constructorCall.argumentsVS.getOrNull(parameterIndex)
    }

    override fun <T : Any> IrExpression.asConstValueVS(clazz: KClass<T>): T? {
        return (this as? IrConst)?.value?.let { clazz.safeCast(it) }
    }
}
