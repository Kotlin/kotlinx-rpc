/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.fir.symbols.impl.FirTypeParameterSymbol
import org.jetbrains.kotlin.fir.types.ConeTypeParameterType

internal actual fun ConeTypeParameterType.typeParameterSymbolCompat(): FirTypeParameterSymbol? {
    return lookupTag?.typeParameterSymbol
}
