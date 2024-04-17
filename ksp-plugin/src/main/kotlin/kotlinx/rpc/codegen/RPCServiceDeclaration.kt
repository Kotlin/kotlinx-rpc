/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSType

class RPCServiceDeclaration(
    private val declaration: KSDeclaration,
    val simpleName: String,
    val fullName: String,
    val packageName: String,
    val functions: List<Function>,
    val fields: List<FlowField>,
    val file: KSFile,
) {
    class Function(
        val name: String,
        val argumentTypes: List<Argument>,
        val returnType: KSType,
    ) {
        class Argument(
            val name: String,
            val type: KSType,
            val isVararg: Boolean,
            val isContextual: Boolean,
        )

        fun collectRootImports(): List<KSDeclaration> {
            return (argumentTypes.map { it.type } + returnType).mapNotNull { it.asRootType() }
        }
    }

    class FlowField(
        val name: String,
        val type: KSType,
        val flowType: Type,
        val isEager: Boolean,
    ) {
        enum class Type {
            Plain, Shared, State;
        }

        fun collectRootImports(): List<KSDeclaration> {
            return listOfNotNull(type.asRootType())
        }
    }

    fun collectRootImports(): List<KSDeclaration> {
        return functions.flatMap { it.collectRootImports() } +
                fields.flatMap { it.collectRootImports() } +
                listOfNotNull(declaration.asRootType())
    }
}

private fun KSType.asRootType(): KSDeclaration? {
    return declaration.asRootType()
}

private fun KSDeclaration.asRootType(): KSDeclaration? {
    return if (this.packageName.asString().isBlank()) this else null
}
