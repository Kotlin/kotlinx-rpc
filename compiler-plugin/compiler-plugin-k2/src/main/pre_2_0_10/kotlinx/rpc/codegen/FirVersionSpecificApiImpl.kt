/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.types.toFirResolvedTypeRef

@Suppress("unused")
object FirVersionSpecificApiImpl : FirVersionSpecificApi {
    override fun ConeKotlinType.toFirResolvedTypeRefVS(
        source: KtSourceElement?,
        delegatedTypeRef: FirTypeRef?,
    ): FirResolvedTypeRef {
        return toFirResolvedTypeRef(source, delegatedTypeRef)
    }
}
