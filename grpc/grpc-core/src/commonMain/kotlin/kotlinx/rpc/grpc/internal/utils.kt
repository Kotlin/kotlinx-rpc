/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public fun internalError(message: String): Nothing {
    error("Unexpected internal error: $message. Please, report the issue here: https://github.com/Kotlin/kotlinx-rpc/issues/new?template=bug_report.md")
}