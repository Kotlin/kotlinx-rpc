/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.conventions.jvm)
}

dependencies {
    implementation(projects.grpc.grpcServer)
    implementation(projects.tests.grpcBenchmarks.protos)
    implementation(libs.grpc.netty)
}
