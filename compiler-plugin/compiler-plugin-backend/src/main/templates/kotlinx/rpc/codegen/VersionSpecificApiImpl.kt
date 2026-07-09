/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

//##csm VersionSpecificApiImpl.kt-import
//##csm specific=[2.2.0...2.3.*]
import kotlinx.rpc.codegen.extension.IrMemberAccessExpressionData
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CommonConfigurationKeys
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
import org.jetbrains.kotlin.ir.util.getValueArgument
import org.jetbrains.kotlin.ir.util.isNullable
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.isJs
import org.jetbrains.kotlin.platform.isWasm
import kotlin.reflect.KClass
import kotlin.reflect.safeCast
//##csm /specific
//##csm specific=[2.4.0...2.4.19]
import kotlinx.rpc.codegen.extension.IrMemberAccessExpressionData
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.ir.builders.declarations.IrFieldBuilder
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrAnnotation
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrConstKind
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrAnnotationImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.fromSymbolOwner
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.copyTo
import org.jetbrains.kotlin.ir.util.getValueArgument
import org.jetbrains.kotlin.ir.util.isNullable
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.isJs
import org.jetbrains.kotlin.platform.isWasm
import kotlin.reflect.KClass
import kotlin.reflect.safeCast
//##csm /specific
//##csm default
import kotlinx.rpc.codegen.extension.IrMemberAccessExpressionData
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.ir.builders.declarations.IrFieldBuilder
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrAnnotation
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrConstKind
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrAnnotationImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.fromSymbolOwner
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
import org.jetbrains.kotlin.platform.isJs
import org.jetbrains.kotlin.platform.isWasm
import kotlin.reflect.KClass
import kotlin.reflect.safeCast
//##csm /default
//##csm /VersionSpecificApiImpl.kt-import

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
        //##csm referenceBuiltInClass
        //##csm specific=[2.2.0...2.3.99]
        return context.referenceClass(
        //##csm /specific
        //##csm default
        return context.finderForBuiltins().findClass(
        //##csm /default
        //##csm /referenceBuiltInClass
            ClassId(
                FqName(packageName),
                FqName(name),
                false
            )
        )
    }

    override fun referenceClassVS(
        context: IrPluginContext,
        packageName: String,
        name: String,
        from: IrFile,
    ): IrClassSymbol? {
        //##csm referenceClass
        //##csm specific=[2.2.0...2.3.99]
        return context.referenceClass(
        //##csm /specific
        //##csm default
        return context.finderForSource(from).findClass(
        //##csm /default
        //##csm /referenceClass
            ClassId(
                FqName(packageName),
                FqName(name),
                false
            )
        )
    }

    override fun referenceBuiltinFunctionsVS(
        context: IrPluginContext,
        packageName: String,
        name: String,
    ): Collection<IrSimpleFunctionSymbol> {
        //##csm referenceBuiltinFunctions
        //##csm specific=[2.2.0...2.3.99]
        return context.referenceFunctions(
        //##csm /specific
        //##csm default
        return context.finderForBuiltins().findFunctions(
        //##csm /default
        //##csm /referenceBuiltinFunctions
            CallableId(
                FqName(packageName),
                Name.identifier(name),
            )
        )
    }

    override fun referenceFunctionsVS(
        context: IrPluginContext,
        packageName: String,
        name: String,
        from: IrFile,
    ): Collection<IrSimpleFunctionSymbol> {
        //##csm referenceFunctions
        //##csm specific=[2.2.0...2.3.99]
        return context.referenceFunctions(
        //##csm /specific
        //##csm default
        return context.finderForSource(from).findFunctions(
        //##csm /default
        //##csm /referenceFunctions
            CallableId(
                FqName(packageName),
                Name.identifier(name),
            )
        )
    }

    override fun IrValueParameter.copyToVS(irFunction: IrFunction, origin: IrDeclarationOrigin): IrValueParameter {
        return copyTo(irFunction, origin)
    }

    //##csm MessageCollectorAccess
    //##csm default
    @OptIn(org.jetbrains.kotlin.config.MessageCollectorAccess::class)
    //##csm /default
    //##csm specific=[2.2.0...2.4.19]
    //##csm /specific
    //##csm /MessageCollectorAccess
    override val messageCollectorKeyVS: CompilerConfigurationKey<MessageCollector>
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
            if (irExpression == null) {
                // unset on purpose, the callee's default value applies;
                // fake overrides carry defaults on their overridden declarations, so they are not checked
                val callee = (access.symbol.owner as? IrSimpleFunction)?.takeIf { !it.isFakeOverride }

                require(callee == null || callee.valueParametersVS().getOrNull(index)?.defaultValue != null) {
                    "Unset argument at index $index for '${callee?.name}': " +
                            "only parameters with default values can be skipped"
                }
            } else {
                access.arguments[offset + index] = irExpression
            }
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
        //##csm IrAnnotationVS
        //##csm specific=[2.2.0...2.3.99]
        return IrConstructorCallImplVS(
            startOffset = startOffset,
            endOffset = endOffset,
            type = type,
            symbol = symbol,
            typeArgumentsCount = typeArgumentsCount,
            constructorTypeArgumentsCount = constructorTypeArgumentsCount,
            valueArgumentsCount = valueArgumentsCount,
        )
        //##csm /specific
        //##csm default
        return IrAnnotationImpl.fromSymbolOwner(
            startOffset = startOffset,
            endOffset = endOffset,
            type = type,
            constructorSymbol = symbol,
        )
        //##csm /default
        //##csm /IrAnnotationVS
    }

    override fun IrMutableAnnotationContainer.addAnnotationVS(annotation: IrMemberAccessExpression<*>) {
        //##csm addAnnotationVS
        //##csm specific=[2.2.0...2.3.99]
        annotations += annotation as IrConstructorCallImpl
        //##csm /specific
        //##csm default
        annotations += annotation as IrAnnotationImpl
        //##csm /default
        //##csm /addAnnotationVS
    }

    override fun addMetadataVisibleAnnotationVS(
        context: IrPluginContext,
        declaration: IrDeclaration,
        annotation: IrMemberAccessExpression<*>,
    ) {
        //##csm addMetadataVisibleAnnotationVS
        //##csm specific=[2.2.0...2.3.99]
        context.metadataDeclarationRegistrar.addMetadataVisibleAnnotationsToElement(
            declaration,
            listOf(annotation as IrConstructorCall),
        )
        //##csm /specific
        //##csm default
        context.metadataDeclarationRegistrar.addMetadataVisibleAnnotationsToElement(
            declaration,
            listOf(annotation as IrAnnotationImpl),
        )
        //##csm /default
        //##csm /addMetadataVisibleAnnotationVS
    }

    override fun IrConstructorCall.valueArgumentAtVS(index: Int): IrExpression? {
        return arguments.getOrNull(index)
    }

    override fun IrMemberAccessExpression<*>.getValueArgumentVS(name: Name): IrExpression? {
        //##csm getValueArgumentVS
        //##csm specific=[2.2.0...2.4.19]
        return (this as IrConstructorCall).getValueArgument(name)
        //##csm /specific
        //##csm default
        val constructorCall = this as IrConstructorCall
        val parameterIndex = constructorCall.symbol.owner.parameters
            .firstOrNull { it.name == name }
            ?.indexInParameters
            ?: return null

        return constructorCall.argumentsVS.getOrNull(parameterIndex)
        //##csm /default
        //##csm /getValueArgumentVS
    }

    override fun <T : Any> IrExpression.asConstValueVS(clazz: KClass<T>): T? {
        return (this as? IrConst)?.value?.let { clazz.safeCast(it) }
    }
}
