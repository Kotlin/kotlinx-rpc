/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.conventions.jvm)
}

allprojects {
    group = "org.jetbrains.kotlinx"
    version = rootProject.libs.versions.kotlinx.rpc.get()
}

logger.lifecycle("[Protoc Gen] kotlinx.rpc project version: $version")
