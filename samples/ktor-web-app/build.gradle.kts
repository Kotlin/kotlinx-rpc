/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.krpc) apply false
    alias(libs.plugins.krpc.platform) apply false
    alias(libs.plugins.ksp) apply false
}

allprojects {
    version = "0.1"

    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/krpc/maven")
        mavenCentral()
    }
}
