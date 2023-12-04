package org.jetbrains.krpc.codegen

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.platform.js.isJs

object VersionSpecificApiImpl : VersionSpecificApi {
    override fun isJs(platform: TargetPlatform?): Boolean {
        return platform.isJs()
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
}
