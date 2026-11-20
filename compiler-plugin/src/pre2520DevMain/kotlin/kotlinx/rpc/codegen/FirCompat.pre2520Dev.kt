/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.resolve.fullyExpandedType
import org.jetbrains.kotlin.fir.types.ConeClassLikeType
import org.jetbrains.kotlin.name.ClassId

internal actual fun ConeClassLikeType.fullyExpandedClassIdCompat(session: FirSession): ClassId? {
    return fullyExpandedType(session).lookupTag.classId
}
