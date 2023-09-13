package org.jetbrains.krpc.codegen

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated

class RPCSymbolProcessor : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        return emptyList()
    }
}
