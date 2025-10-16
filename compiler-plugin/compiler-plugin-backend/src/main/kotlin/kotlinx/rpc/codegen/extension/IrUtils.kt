/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import org.jetbrains.kotlin.ir.IrBuiltIns
import org.jetbrains.kotlin.ir.builders.declarations.IrFieldBuilder
import org.jetbrains.kotlin.ir.builders.declarations.addDefaultGetter
import org.jetbrains.kotlin.ir.builders.declarations.buildField
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrExpressionBody
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrClassifierSymbol
import org.jetbrains.kotlin.ir.symbols.IrPropertySymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.IrTypeArgument
import org.jetbrains.kotlin.ir.types.IrTypeProjection
import org.jetbrains.kotlin.ir.types.SimpleTypeNullability
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.ir.types.impl.makeTypeProjection
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.types.Variance

fun IrClassifierSymbol.typeWith(type: IrType, variance: Variance): IrType {
    return IrSimpleTypeImpl(
        classifier = this,
        nullability = SimpleTypeNullability.NOT_SPECIFIED,
        arguments = listOf(makeTypeProjection(type, variance)),
        annotations = emptyList(),
    )
}

val IrProperty.getterOrFail: IrSimpleFunction
    get() {
        return getter ?: error("'getter' should be present, but was null: ${dump()}")
    }

fun IrProperty.addDefaultGetter(
    parentClass: IrClass,
    builtIns: IrBuiltIns,
    configure: IrSimpleFunction.() -> Unit = {},
) {
    addDefaultGetter(parentClass, builtIns)

    getterOrFail.apply {
        dispatchReceiverParameter!!.origin = IrDeclarationOrigin.DEFINED

        configure()
    }
}

// A collection of functions that proved to be useful,
// but appeared only in the latest Kotlin versions.
// Copied and placed here as is

val IrTypeArgument.typeOrFail: IrType
    get() {
        require(this is IrTypeProjection) {
            "Type argument should be of type `IrTypeProjection`, but was `${this::class}` instead"
        }

        return this.type
    }

// originally named 'addBackingField'
inline fun IrProperty.addBackingFieldUtil(builder: IrFieldBuilder.() -> Unit = {}): IrField {
    return factory.buildField {
        name = this@addBackingFieldUtil.name
        origin = IrDeclarationOrigin.PROPERTY_BACKING_FIELD
        builder()
    }.also { field ->
        this@addBackingFieldUtil.backingField = field
        field.correspondingPropertySymbol = this@addBackingFieldUtil.symbol
        field.parent = this@addBackingFieldUtil.parent
    }
}

inline fun <T, R> Collection<T>.memoryOptimizedMap(transform: (T) -> R): List<R> {
    return mapTo(ArrayList<R>(size), transform).compactIfPossible()
}

inline fun <T, R> Collection<T>.memoryOptimizedMapIndexed(transform: (index: Int, T) -> R): List<R> {
    return mapIndexedTo(ArrayList<R>(size), transform).compactIfPossible()
}

fun <T> List<T>.compactIfPossible(): List<T> =
    when (size) {
        0 -> emptyList()
        1 -> listOf(first())
        else -> apply {
            if (this is ArrayList<*>) trimToSize()
        }
    }

@Suppress("EXTENSION_SHADOWED_BY_MEMBER") // TODO(KTIJ-26314): Remove this suppression
fun IrFactory.createExpressionBody(expression: IrExpression): IrExpressionBody =
    createExpressionBody(expression.startOffset, expression.endOffset, expression)

fun IrClassSymbol.findPropertyByName(name: String): IrPropertySymbol? =
    owner.properties.singleOrNull { it.name.asString() == name }?.symbol