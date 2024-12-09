/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core)
                api(libs.coroutines.core)
            }
        }

        jvmMain {
            dependencies {
                implementation("io.grpc:grpc-util:1.68.2")
                implementation("io.grpc:grpc-stub:1.68.2")
                implementation("io.grpc:grpc-protobuf:1.68.2")
                implementation("io.grpc:grpc-kotlin-stub:1.4.1")
                implementation("com.google.protobuf:protobuf-java-util:4.28.2")
                implementation("com.google.protobuf:protobuf-kotlin:4.28.2")
            }
        }
    }
}
