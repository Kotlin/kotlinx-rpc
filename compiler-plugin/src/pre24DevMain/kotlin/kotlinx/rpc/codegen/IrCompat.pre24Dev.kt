/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrMutableAnnotationContainer
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.types.IrType

internal actual fun irAnnotationCompat(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    symbol: IrConstructorSymbol,
    typeArgumentsCount: Int,
    constructorTypeArgumentsCount: Int,
): IrMemberAccessExpression<*> {
    return IrConstructorCallImpl(
        startOffset = startOffset,
        endOffset = endOffset,
        type = type,
        symbol = symbol,
        typeArgumentsCount = typeArgumentsCount,
        constructorTypeArgumentsCount = constructorTypeArgumentsCount,
    )
}

internal actual fun IrMutableAnnotationContainer.addAnnotationCompat(annotation: IrMemberAccessExpression<*>) {
    annotations += annotation as IrConstructorCallImpl
}

internal actual fun IrPluginContext.addMetadataVisibleAnnotationCompat(
    declaration: IrDeclaration,
    annotation: IrMemberAccessExpression<*>,
) {
    metadataDeclarationRegistrar.addMetadataVisibleAnnotationsToElement(
        declaration,
        listOf(annotation as IrConstructorCall),
    )
}
