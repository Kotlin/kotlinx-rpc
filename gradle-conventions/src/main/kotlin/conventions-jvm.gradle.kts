/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import util.configureJvm
import util.optInForRpcApi

plugins {
    id("conventions-common")
    id("org.jetbrains.kotlin.jvm")
}

java {
    withSourcesJar()
}

configure<KotlinJvmProjectExtension> {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }

    optInForRpcApi()

    explicitApi()
}

configureJvm(isKmp = false)
