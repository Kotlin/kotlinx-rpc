/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.getBooleanArgument
import org.jetbrains.kotlin.fir.declarations.getKClassArgument
import org.jetbrains.kotlin.fir.declarations.getStringArgument
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.name.Name

internal actual fun FirAnnotation.getKClassArgumentCompat(name: Name, session: FirSession): ConeKotlinType? {
    return getKClassArgument(name)
}

internal actual fun FirAnnotation.getBooleanArgumentCompat(name: Name, session: FirSession): Boolean? {
    return getBooleanArgument(name)
}

internal actual fun FirAnnotation.getStringArgumentCompat(name: Name, session: FirSession): String? {
    return getStringArgument(name)
}
