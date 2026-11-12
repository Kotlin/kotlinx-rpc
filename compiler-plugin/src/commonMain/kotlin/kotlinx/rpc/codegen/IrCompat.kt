/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrMutableAnnotationContainer
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.types.IrType

/**
 * Creates an annotation call.
 * The result is an `IrAnnotation` from Kotlin 2.4.0, and an `IrConstructorCall` before it.
 */
internal expect fun irAnnotationCompat(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    symbol: IrConstructorSymbol,
    typeArgumentsCount: Int,
    constructorTypeArgumentsCount: Int,
): IrMemberAccessExpression<*>

/**
 * @param annotation a result of [irAnnotationCompat].
 */
internal expect fun IrMutableAnnotationContainer.addAnnotationCompat(annotation: IrMemberAccessExpression<*>)

/**
 * @param annotation a result of [irAnnotationCompat].
 */
internal expect fun IrPluginContext.addMetadataVisibleAnnotationCompat(
    declaration: IrDeclaration,
    annotation: IrMemberAccessExpression<*>,
)
