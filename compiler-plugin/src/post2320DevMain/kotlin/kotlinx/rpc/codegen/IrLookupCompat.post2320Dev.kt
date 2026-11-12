/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId

internal actual fun IrPluginContext.findClassCompat(classId: ClassId, from: IrFile?): IrClassSymbol? {
    val finder = if (from == null) finderForBuiltins() else finderForSource(from)
    return finder.findClass(classId)
}

internal actual fun IrPluginContext.findFunctionsCompat(
    callableId: CallableId,
    from: IrFile?,
): Collection<IrSimpleFunctionSymbol> {
    val finder = if (from == null) finderForBuiltins() else finderForSource(from)
    return finder.findFunctions(callableId)
}
