/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.configureMetaTasks

val kotlinVersion: KotlinVersion by extra
val rpcVersion: String = libs.versions.kotlinx.rpc.get()

allprojects {
    group = "org.jetbrains.kotlinx"
    version = "$kotlinVersion-$rpcVersion"
}

configureMetaTasks("clean", "cleanTest", "test")
configureMetaTasks(tasks.matching { it.name.startsWith("publish") }.map { it.name })
