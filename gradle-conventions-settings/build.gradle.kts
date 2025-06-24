/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    `kotlin-dsl`
}

dependencies {
    // https://stackoverflow.com/questions/76713758/use-version-catalog-inside-precompiled-gradle-plugin
    api(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
