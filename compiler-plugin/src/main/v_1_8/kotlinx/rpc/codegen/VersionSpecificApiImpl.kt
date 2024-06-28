/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.addExtensionReceiver
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.builders.declarations.IrFieldBuilder
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
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
import org.jetbrains.kotlin.platform.js.isJs

object VersionSpecificApiImpl : VersionSpecificApi {
    override fun isJs(platform: TargetPlatform?): Boolean {
        return platform.isJs()
    }
    override var IrFieldBuilder.modalityVS: Modality
        get() = undefinedAPI()
        set(_) {}

    override var IrCall.originVS: IrStatementOrigin?
        get() = origin
        set(_) {}

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

    override fun IrValueParameter.copyToVS(
        irFunction: IrFunction,
        origin: IrDeclarationOrigin,
    ): IrValueParameter {
        return copyTo(irFunction)
    }

    override fun IrSimpleFunction.addExtensionReceiverVS(type: IrType, origin: IrDeclarationOrigin): IrValueParameter {
        return addExtensionReceiver(type, origin)
    }
}
