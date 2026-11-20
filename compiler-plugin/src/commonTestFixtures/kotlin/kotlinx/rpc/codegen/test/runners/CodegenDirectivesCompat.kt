/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.test.runners

import org.jetbrains.kotlin.test.directives.model.SimpleDirective

/**
 * Disables the D8 dex check that the JVM codegen test infrastructure runs on the compiled test classes.
 */
internal expect val ignoreDexingDirective: SimpleDirective
