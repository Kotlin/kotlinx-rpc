/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.conventions.protoc.gen)
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "kotlinx.rpc.protoc.gen.grpc.MainKt"
    }
}

dependencies {
    implementation(projects.common)
}
