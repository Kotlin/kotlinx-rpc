/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

internal fun KotlinProjectExtension.optInForRPCApi() {
    sourceSets.all {
        languageSettings.optIn("kotlinx.rpc.internal.InternalRPCApi")
        languageSettings.optIn("kotlinx.rpc.internal.ExperimentalRPCApi")
    }
}
