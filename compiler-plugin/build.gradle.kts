/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.configureMetaTasks

plugins {
    alias(libs.plugins.conventions.utils)
}

val kotlinVersion: String by extra
val rpcVersion: String = libs.versions.kotlinx.rpc.get()

allprojects {
    group = "org.jetbrains.kotlinx"
    version = "$kotlinVersion-$rpcVersion"
}

configureMetaTasks("cleanTest", "test")
configureMetaTasks(tasks.matching { it.name.startsWith("publish") }.map { it.name })
