/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import util.*
import util.applyDokka

plugins {
    id("conventions-common")
    id("org.jetbrains.kotlin.multiplatform")
}

configure<KotlinMultiplatformExtension> {
    optInForRpcApi()

    explicitApi()
}

withKotlinConfig {
    configureKotlin()
    configureJs()
    configureWasm()
}

configureJvm(isKmp = true)

applyDokka()
