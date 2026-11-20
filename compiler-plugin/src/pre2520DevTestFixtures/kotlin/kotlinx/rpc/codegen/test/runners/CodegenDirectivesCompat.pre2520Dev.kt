/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.test.runners

import org.jetbrains.kotlin.test.directives.CodegenTestDirectives
import org.jetbrains.kotlin.test.directives.model.SimpleDirective

internal actual val ignoreDexingDirective: SimpleDirective
    get() = CodegenTestDirectives.IGNORE_DEXING
