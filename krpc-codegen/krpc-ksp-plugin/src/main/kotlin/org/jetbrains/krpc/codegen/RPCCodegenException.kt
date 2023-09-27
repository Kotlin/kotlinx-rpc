package org.jetbrains.krpc.codegen

import com.google.devtools.ksp.symbol.KSDeclaration

class RPCCodegenException(
    initMessage: String? = null,
    declaration: KSDeclaration? = null,
) : RuntimeException() {
    private val serviceInfo = declaration?.qualifiedName?.let {
        "[${it.asString()}]"
    } ?: ""

    override val message: String = "[RPC Codegen] $serviceInfo $initMessage"
}

fun codegenError(message: String? = null, service: KSDeclaration? = null): Nothing {
    throw RPCCodegenException(message, service)
}
