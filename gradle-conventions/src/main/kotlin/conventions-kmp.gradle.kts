/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import util.configureKotlin
import util.optInForInternalKRPCApi
import util.optionalProperty

plugins {
    id("conventions-common")
    id("org.jetbrains.kotlin.multiplatform")
}

configure<KotlinMultiplatformExtension> {
    optInForInternalKRPCApi()

    explicitApi()
}

val excludeJs: Boolean by optionalProperty()
val excludeJvm: Boolean by optionalProperty()
val excludeNative: Boolean by optionalProperty()

configureKotlin(
    jvm = !excludeJvm,
    js = !excludeJs,
    native = !excludeNative,
)
