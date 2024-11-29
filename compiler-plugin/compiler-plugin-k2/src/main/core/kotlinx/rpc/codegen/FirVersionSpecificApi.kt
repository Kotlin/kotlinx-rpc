/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.types.builder.FirResolvedTypeRefBuilder

interface FirVersionSpecificApi {
    fun ConeKotlinType.toFirResolvedTypeRefVS(
        source: KtSourceElement? = null,
        delegatedTypeRef: FirTypeRef? = null,
    ): FirResolvedTypeRef

    fun ConeKotlinType.toClassSymbolVS(
        session: FirSession,
    ): FirClassSymbol<*>?

    var FirResolvedTypeRefBuilder.coneTypeVS: ConeKotlinType
}

val vsApiClass by lazy {
    runCatching {
        Class.forName("kotlinx.rpc.codegen.FirVersionSpecificApiImpl")
    }.getOrNull()
}

inline fun <T> vsApi(body: FirVersionSpecificApi.() -> T): T {
    val kClass = vsApiClass?.kotlin ?: error("FirVersionSpecificApi is not present")
    return (kClass.objectInstance as FirVersionSpecificApi).body()
}
