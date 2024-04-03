/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.codegen.extension

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.defaultType

class ServiceDeclaration(
    val service: IrClass,
    val stubClass: IrClass,
    val methods: List<Method>,
    val fields: List<FlowField>,
) {
    val simpleName: String = service.name.asString()
    val serviceType = service.defaultType

    class Method(
        val function: IrSimpleFunction,
        val argumentTypes: List<Argument>,
    ) {
        class Argument(
            val value: IrValueParameter,
            val type: IrType,
        )
    }

    class FlowField(
        val property: IrProperty,
        val flowKind: Kind,
    ) {
        enum class Kind {
            Plain, Shared, State;
        }
    }
}
