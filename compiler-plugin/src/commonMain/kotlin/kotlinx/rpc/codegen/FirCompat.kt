/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirTypeParameterSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.ConeTypeParameterType
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.name.Name

internal expect fun FirTypeRef.toRegularClassSymbolCompat(session: FirSession): FirRegularClassSymbol?

internal expect fun ConeKotlinType.toRegularClassSymbolCompat(session: FirSession): FirRegularClassSymbol?

/**
 * @param session Kotlin versions from 2.4.0 ignore it.
 */
internal expect fun FirAnnotation.getKClassArgumentCompat(name: Name, session: FirSession): ConeKotlinType?

/**
 * @param session Kotlin versions from 2.4.0 ignore it.
 */
internal expect fun FirAnnotation.getBooleanArgumentCompat(name: Name, session: FirSession): Boolean?

/**
 * @param session Kotlin versions from 2.4.0 ignore it.
 */
internal expect fun FirAnnotation.getStringArgumentCompat(name: Name, session: FirSession): String?

internal expect fun ConeTypeParameterType.typeParameterSymbolCompat(): FirTypeParameterSymbol?
