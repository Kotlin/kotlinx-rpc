/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.test

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js

@OptIn(ExperimentalWasmJsInterop::class)
public actual fun environmentVariable(name: String): String? =
    js("typeof process !== 'undefined' && process.env && process.env[name] != null ? process.env[name] : null")
