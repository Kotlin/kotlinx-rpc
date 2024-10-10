/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.checkers.FirRpcDeclarationCheckers
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar

class FirRpcCheckers(session: FirSession) : FirAdditionalCheckersExtension(session) {
    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(FirRpcPredicates.rpc)
    }

    override val declarationCheckers: DeclarationCheckers = FirRpcDeclarationCheckers
}
