/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.types.builder.FirResolvedTypeRefBuilder

interface FirVersionSpecificApi {
    fun ConeKotlinType.toFirResolvedTypeRefVS(
        source: KtSourceElement? = null,
        delegatedTypeRef: FirTypeRef? = null,
    ): FirResolvedTypeRef

    var FirResolvedTypeRefBuilder.coneTypeVS: ConeKotlinType
}

fun <T> vsApi(body: FirVersionSpecificApi.() -> T) : T {
    val klass = Class.forName("kotlinx.rpc.codegen.FirVersionSpecificApiImpl")
    return (klass.kotlin.objectInstance as FirVersionSpecificApi).body()
}
