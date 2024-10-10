/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers

import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirRegularClassChecker

object FirRpcDeclarationCheckers : DeclarationCheckers() {
    override val regularClassCheckers: Set<FirRegularClassChecker> = setOf(
        FirRpcAnnotationChecker,
    )
}
