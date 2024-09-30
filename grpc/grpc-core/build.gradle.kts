/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.conventions.kmp)
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
                implementation("io.grpc:grpc-util:1.67.1")
                implementation("io.grpc:grpc-stub:1.67.1")
                implementation("io.grpc:grpc-protobuf:1.67.1")
                implementation("io.grpc:grpc-kotlin-stub:1.3.1")
                implementation("com.google.protobuf:protobuf-java-util:3.24.1")
                implementation("com.google.protobuf:protobuf-kotlin:3.24.1")
            }
        }
    }
}
