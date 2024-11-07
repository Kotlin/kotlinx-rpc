/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.addExtensionReceiver
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.ir.builders.declarations.IrFieldBuilder
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.copyTo
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
        set(value) { origin = value }

    override var IrConstructor.isPrimaryVS: Boolean
        get() = isPrimary
        set(value) {
            isPrimary = value
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
        name: String
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
        return addExtensionReceiver(type, origin)
    }

    override val messageCollectorKey: CompilerConfigurationKey<MessageCollector>
        get() = CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY
}
