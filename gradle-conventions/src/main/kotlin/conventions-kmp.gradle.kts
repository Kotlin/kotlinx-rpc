/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import util.configureJvm
import util.configureKotlin
import util.optInForRPCApi
import util.optionalProperty

plugins {
    id("conventions-common")
    id("org.jetbrains.kotlin.multiplatform")
}

configure<KotlinMultiplatformExtension> {
    optInForRPCApi()

    explicitApi()
}

val excludeJs: Boolean by optionalProperty()
val excludeJvm: Boolean by optionalProperty()
val excludeNative: Boolean by optionalProperty()

val kotlinVersion: KotlinVersion by extra

configureKotlin(
    kotlinVersion = kotlinVersion,
    jvm = !excludeJvm,
    js = !excludeJs,
    native = !excludeNative,
)

configureJvm(isKmp = true)
