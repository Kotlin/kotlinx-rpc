/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("FunctionName")

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.extension.IrMemberAccessExpressionData
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
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
import org.jetbrains.kotlin.platform.TargetPlatform
import kotlin.reflect.KClass

@Suppress("detekt.LongParameterList")
interface VersionSpecificApi {
    fun isJs(platform: TargetPlatform?): Boolean
    fun isWasm(platform: TargetPlatform?): Boolean

    fun referenceClass(context: IrPluginContext, packageName: String, name: String): IrClassSymbol?

    fun referenceFunctions(
        context: IrPluginContext,
        packageName: String,
        name: String,
    ): Collection<IrSimpleFunctionSymbol>

    fun IrValueParameter.copyToVS(
        irFunction: IrFunction,
        origin: IrDeclarationOrigin = this.origin,
    ): IrValueParameter

    var IrFieldBuilder.isFinalVS: Boolean

    var IrCall.originVS: IrStatementOrigin?

    val IrConstructor.parametersVS: List<IrValueParameter>

    val IrConstructorCall.argumentsVS: List<IrExpression?>

    fun IrType.isNullableVS(): Boolean

    val messageCollectorKey: CompilerConfigurationKey<MessageCollector>

    companion object {
        lateinit var INSTANCE: VersionSpecificApi
    }

    fun IrCallImplVS(
        startOffset: Int,
        endOffset: Int,
        type: IrType,
        symbol: IrSimpleFunctionSymbol,
        typeArgumentsCount: Int,
        valueArgumentsCount: Int,
        origin: IrStatementOrigin? = null,
        superQualifierSymbol: IrClassSymbol? = null,
    ): IrCallImpl

    fun IrConstructorCallImplVS(
        startOffset: Int,
        endOffset: Int,
        type: IrType,
        symbol: IrConstructorSymbol,
        typeArgumentsCount: Int,
        valueArgumentsCount: Int,
        constructorTypeArgumentsCount: Int,
        origin: IrStatementOrigin? = null,
        source: SourceElement = SourceElement.NO_SOURCE,
    ): IrConstructorCallImpl

    fun IrFunction.valueParametersVS(): List<IrValueParameter>
    var IrFunction.dispatchReceiverParameterVS: IrValueParameter?

    fun IrMemberAccessExpressionData.buildFor(access: IrMemberAccessExpression<*>)

    fun IrConstructorCall.valueArgumentAt(index: Int): IrExpression?

    fun <T : Any> IrExpression.asConstValue(clazz: KClass<T>): T?
}

@Suppress("unused")
fun undefinedAPI(): Nothing = error("This API is not defined in current Kotlin version")
