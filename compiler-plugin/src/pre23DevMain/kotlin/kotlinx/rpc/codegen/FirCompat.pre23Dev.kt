/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.toRegularClassSymbol
import org.jetbrains.kotlin.fir.resolve.toRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirTypeRef

internal actual fun FirTypeRef.toRegularClassSymbolCompat(session: FirSession): FirRegularClassSymbol? {
    return toRegularClassSymbol(session)
}

internal actual fun ConeKotlinType.toRegularClassSymbolCompat(session: FirSession): FirRegularClassSymbol? {
    return toRegularClassSymbol(session)
}
