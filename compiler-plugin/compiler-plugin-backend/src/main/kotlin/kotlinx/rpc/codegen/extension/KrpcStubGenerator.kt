/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */
package kotlinx.rpc.codegen.extension

import kotlinx.rpc.codegen.common.RpcClassId
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.declarations.addProperty
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrClassReference
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetObjectValueImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.deepCopyWithSymbols
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.file
import org.jetbrains.kotlin.ir.util.isObject
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.name.Name

private object KrpcDescriptor {
    const val SERIALIZERS = "serializers"
}

/**
 * Adds kRPC specifics on top of the base stub:
 * the descriptor additionally implements `KrpcServiceDescriptor` with prebuilt serializer instances
 * for types annotated with `@Serializable(with = ...)`.
 *
 * kotlinx.serialization is always available for kRPC services, as `krpc-core` exposes it as an `api` dependency.
 */
internal class KrpcStubGenerator(
    declaration: ServiceDeclaration,
    ctx: RpcIrContext,
    logger: MessageCollector,
) : RpcStubGenerator(declaration, ctx, logger) {
    override fun IrClass.generateAdditionalDescriptorContent() {
        generateSerializersProperty()
    }

    /**
     * Serializers from all `@Serializable(with = ...)` annotations
     * on the parameter and return types of the service's methods, mapped by their class.
     *
     * ```kotlin
     * override val serializers: Map<KClass<KSerializer<Any?>>, KSerializer<Any?>> = mapOf(
     *     Pair(<serializer-class-1>::class, <serializer-1-instance>),
     *     ...
     * )
     *
     * // when none:
     * override val serializers: Map<KClass<KSerializer<Any?>>, KSerializer<Any?>> = emptyMap()
     * ```
     *
     * Where:
     *  - `<serializer-k-instance>` - the object itself or a call to the no-arg primary constructor
     */
    private fun IrClass.generateSerializersProperty() {
        val interfaceProperty = ctx.properties.krpcServiceDescriptorSerializers
        val keyType = ctx.kSerializerAnyNullableKClass(declaration.stubClass.file)
        val valueType = ctx.kSerializerAnyNullable(declaration.stubClass.file)
        val mapType = ctx.irBuiltIns.mapClass.typeWith(keyType, valueType)

        val serializerReferences = declaration.methods
            .flatMap { method -> method.arguments.map { it.type } + method.unwrappedReturnType() }
            .flatMap { type -> type.annotations.filter { it.type.isSerializableAnnotation() }.map { it to type } }
            .map { (annotation, type) ->
                vsApi { annotation.argumentsVS }.singleOrNull() as? IrClassReference
                    ?: error("Expected single not null value parameter of KSerializer::class for @Serializable annotation on type '${type.dumpKotlinLike()}'")
            }
            // TODO: one instance per serializer class is shared by all methods of the service,
            //  before it was one instance per annotated type. Check if this is fine for stateful serializers.
            .distinctBy { it.classType.classOrNull }

        addProperty {
            name = Name.identifier(KrpcDescriptor.SERIALIZERS)
            visibility = DescriptorVisibilities.PUBLIC
            modality = Modality.FINAL
        }.apply {
            overriddenSymbols = listOf(interfaceProperty)

            addBackingFieldUtil {
                visibility = DescriptorVisibilities.PRIVATE
                type = mapType
                vsApi { isFinalVS = true }
            }.apply {
                initializer = factory.createExpressionBody(
                    irMapOf(
                        keyType = keyType,
                        valueType = valueType,
                        elements = serializerReferences.memoryOptimizedMap { reference ->
                            reference.deepCopyWithSymbols() to reference.classType.irCreateInstance()
                        },
                    )
                )
            }

            addDefaultGetter(this@generateSerializersProperty, ctx.irBuiltIns) {
                visibility = DescriptorVisibilities.PUBLIC
                overriddenSymbols = listOf(interfaceProperty.owner.getterOrFail.symbol)
            }
        }
    }

    private fun IrType.irCreateInstance(): IrExpression {
        val classSymbol =
            classOrNull ?: error("Expected class type for type to create instance '${dumpKotlinLike()}'")

        return if (classSymbol.owner.isObject) {
            IrGetObjectValueImpl(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = this,
                symbol = classSymbol,
            )
        } else {
            val constructor = classSymbol.owner.primaryConstructor
                ?: error("Expected primary constructor for a serializer '${dumpKotlinLike()}'")

            if (vsApi { constructor.parametersVS.isNotEmpty() }) {
                error(
                    "Primary constructor for a serializer '${dumpKotlinLike()}' can't have parameters: " +
                        vsApi { constructor.parametersVS }.joinToString { it.dumpKotlinLike() }
                )
            }

            IrConstructorCallImpl(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = this,
                symbol = constructor.symbol,
                typeArgumentsCount = constructor.typeParameters.size,
                constructorTypeArgumentsCount = constructor.typeParameters.size,
            ).apply {
                arguments {
                    types {
                        repeat(classSymbol.owner.typeParameters.size) {
                            +ctx.anyNullable
                        }
                    }
                }
            }
        }
    }

    private fun IrType.isSerializableAnnotation(): Boolean =
        classOrNull?.owner?.classId == RpcClassId.serializableAnnotation
}
