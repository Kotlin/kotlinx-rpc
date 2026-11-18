/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfigurationKey

/**
 * From Kotlin 2.4.20, the key needs the `MessageCollectorAccess` opt-in. Earlier versions don't have the marker.
 */
internal expect val messageCollectorKeyCompat: CompilerConfigurationKey<MessageCollector>
