package org.jetbrains.krpc.codegen

import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSType

class RPCServiceDeclaration(
    val simpleName: String,
    val fullName: String,
    val functions: List<Function>,
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
        )
    }
}