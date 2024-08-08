/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.serialization

import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlinx.serialization.compiler.fir.SerializationFirResolveExtension

internal fun SerializationFirResolveExtension.generateSerializerImplClass(
    owner: FirRegularClassSymbol,
): FirClassLikeSymbol<*> {
    return callPrivateMethod("generateSerializerImplClass", owner)
}

internal fun SerializationFirResolveExtension.generateCompanionDeclaration(
    owner: FirRegularClassSymbol,
): FirRegularClassSymbol? {
    return callPrivateMethod("generateCompanionDeclaration", owner)
}

private fun <T> SerializationFirResolveExtension.callPrivateMethod(name: String, arg: Any?): T {
    val method = this::class.java.declaredMethods.find { it.name == name }
        ?: error("Expected method with name $name in SerializationFirResolveExtension")

    method.isAccessible = true

    @Suppress("UNCHECKED_CAST")
    return method.invoke(this, arg) as T
}
