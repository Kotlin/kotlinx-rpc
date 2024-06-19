/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import util.optInForRPCApi

plugins {
    id("conventions-common")
    id("org.jetbrains.kotlin.jvm")
    id("conventions-kotlin-version-jvm")
}

configure<KotlinJvmProjectExtension> {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }

    optInForRPCApi()

    explicitApi()
}
