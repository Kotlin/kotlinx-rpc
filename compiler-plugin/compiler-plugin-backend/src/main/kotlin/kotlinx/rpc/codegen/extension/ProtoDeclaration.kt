/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import org.jetbrains.kotlin.ir.declarations.IrClass

internal class ProtoDeclaration(
    val message: IrClass,
    val messageInternal: IrClass,
    val descriptor: IrClass,
)