/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.krpc)
}

dependencies {
    implementation(project(":krpc-runtime:krpc-runtime-client"))
    implementation(project(":krpc-runtime:krpc-runtime-server"))

    implementation(project(":tests:codegen-tests:codegen-tests-mpp"))

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.coroutines.core)
    implementation(libs.serialization.core)

    testImplementation(libs.kotlin.test)
}
