/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.test

/**
 * Returns a test-process environment variable, or `null` if it isn't set.
 * Returns `null` in browsers and on Wasm/WASI.
 */
public expect fun environmentVariable(name: String): String?
