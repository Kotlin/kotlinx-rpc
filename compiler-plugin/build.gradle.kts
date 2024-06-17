/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import util.configureMetaTasks

val kotlinVersion: String by extra
val rpcVersion: String = libs.versions.kotlinx.rpc.get()

allprojects {
    group = "org.jetbrains.kotlinx"
    version = "$kotlinVersion-$rpcVersion"
}

plugins {
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.compiler.specific.module)
}

subprojects {
    afterEvaluate {
        configure<KotlinProjectExtension> {
            explicitApi = ExplicitApiMode.Disabled
        }
    }
}

configureMetaTasks("cleanTest", "test")
configureMetaTasks(tasks.matching { it.name.startsWith("publish") }.map { it.name })
