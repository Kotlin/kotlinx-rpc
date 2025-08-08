/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import util.withKotlinJvmExtension
import util.withKotlinKmpExtension

fun KotlinProjectExtension.optInForRpcApi() {
    sourceSets.all {
        languageSettings.optIn("kotlinx.rpc.internal.utils.InternalRpcApi")
        languageSettings.optIn("kotlinx.rpc.internal.utils.ExperimentalRpcApi")
    }
}

withKotlinJvmExtension {
    optInForRpcApi()
}

withKotlinKmpExtension {
    optInForRpcApi()
}
