/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt:all")

package kotlinx.rpc.codegen

import com.google.devtools.ksp.symbol.KSDeclaration

sealed class ACodeGenerationException(
    initMessage: String? = null,
    declaration: KSDeclaration? = null,
) : Exception() {
    override val message: String = run {
        val serviceInfo = declaration?.qualifiedName?.let { " [${it.asString()}]" } ?: ""

        "[RPC Codegen]$serviceInfo $initMessage"
    }
}

class CodeGenerationException(message: String, declaration: KSDeclaration? = null) :
    ACodeGenerationException(message, declaration)

class MutableFieldCodeGenerationException(declaration: KSDeclaration? = null) :
    ACodeGenerationException("RPC Service field can not be mutable", declaration)

class FieldExtensionReceiverCodeGenerationException(declaration: KSDeclaration? = null) :
    ACodeGenerationException("RPC Service field can not have extension receiver", declaration)

class ForbiddenFieldTypeCodeGenerationException(declaration: KSDeclaration? = null) : ACodeGenerationException(
    initMessage = "Only Flow, SharedFlow and StateFlow fields are allowed in a RemoteService",
    declaration = declaration
)

class InvalidFieldNameCodeGenerationException(declaration: KSDeclaration? = null) :
    ACodeGenerationException("RPC Service field's name cannot contain \$ symbol", declaration)

class InvalidMethodNameCodeGenerationException(declaration: KSDeclaration? = null) :
    ACodeGenerationException("RPC Service method's name cannot contain \$ symbol", declaration)

class UnresolvedMethodReturnTypeCodeGenerationException(declaration: KSDeclaration? = null) :
    ACodeGenerationException("Failed to resolve method's returnType", declaration)

class MethodDefaultImplementationCodeGenerationException(declaration: KSDeclaration? = null) :
    ACodeGenerationException("RPC Service method can not have default implementation", declaration)

class AbsentSuspendMethodModifierCodeGenerationException(declaration: KSDeclaration? = null) :
    ACodeGenerationException("RPC Service method must have 'suspend' modifier", declaration)

class ForbiddenMethodModifierCodeGenerationException(declaration: KSDeclaration? = null) :
    ACodeGenerationException(
        initMessage = "RPC Service method can not have any of these modifiers: ${RPCSymbolProcessor.DENY_LIST_FUNCTION_MODIFIERS.joinToString()}",
        declaration = declaration
    )

class MethodExtensionReceiverCodeGenerationException(declaration: KSDeclaration? = null) :
    ACodeGenerationException("RPC Service method can not have extension receiver", declaration)

class MethodTypeParametersCodeGenerationException(declaration: KSDeclaration? = null) :
    ACodeGenerationException("RPC Service method can not have type parameters", declaration)

class AbsentQualifiedNameCodeGenerationException(declaration: KSDeclaration? = null) :
    ACodeGenerationException("Expected service qualified name", declaration)

class AbsentShortNameCodeGenerationException(declaration: KSDeclaration? = null) :
    ACodeGenerationException("Expected function argument name", declaration)

class ForbiddenServiceInterfaceModifierCodeGenerationException(declaration: KSDeclaration? = null) :
    ACodeGenerationException(
        initMessage = "RPC Service interface can not have any of these modifiers: ${RPCSymbolProcessor.DENY_LIST_SERVICE_MODIFIERS.joinToString()}",
        declaration = declaration
    )

class ServiceInterfaceTypeParametersCodeGenerationException(declaration: KSDeclaration? = null) :
    ACodeGenerationException("RPC Service interface can not have type parameters", declaration)

inline fun <reified E> codegenError(declaration: KSDeclaration? = null): Nothing {
    codegenError(E::class.java, declaration)
}

fun <E> codegenError(errorClass: Class<E>, declaration: KSDeclaration? = null): Nothing {
    throw errorClass.constructors.first().newInstance(declaration) as Exception
}

fun codegenError(message: String, declaration: KSDeclaration? = null): Nothing {
    throw CodeGenerationException(message, declaration)
}
