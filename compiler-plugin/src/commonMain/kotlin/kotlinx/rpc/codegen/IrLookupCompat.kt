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

/**
 * @param from the file that references the class, or `null` for a builtin class.
 * Kotlin versions before 2.3.20 ignore it.
 */
internal expect fun IrPluginContext.findClassCompat(classId: ClassId, from: IrFile?): IrClassSymbol?

/**
 * @param from the file that references the functions, or `null` for builtin functions.
 * Kotlin versions before 2.3.20 ignore it.
 */
internal expect fun IrPluginContext.findFunctionsCompat(
    callableId: CallableId,
    from: IrFile?,
): Collection<IrSimpleFunctionSymbol>
