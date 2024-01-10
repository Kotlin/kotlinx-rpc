/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

internal fun KotlinProjectExtension.optInForInternalKRPCApi() {
    sourceSets.all {
        languageSettings.optIn("org.jetbrains.krpc.internal.InternalKRPCApi")
    }
}
