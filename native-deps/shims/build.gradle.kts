/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.named

// All real logic lives in subprojects:
// - :kotlinx-rpc-grpc-core-shim publishes the gRPC native shim
// - :kotlinx-rpc-protobuf-shim publishes the protobuf native shim
// - :kotlinx-rpc-native-shims-annotation publishes the shared opt-in markers used by both shims
// - :klib-patcher is internal build tooling used only while building the shim KLIBs
// - :tests verifies the published-artifact behavior through throwaway consumer builds
plugins {
    base
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.conventions.jvm) apply false
}

group = "org.jetbrains.kotlinx"

allprojects {
    group = rootProject.group

    plugins.withId("maven-publish") {
        extensions.configure<PublishingExtension> {
            repositories {
                // Fixture tests publish shim artifacts here first and then consume them from isolated
                // throwaway builds, so this repository is part of the verification flow rather than the
                // normal external publishing story.
                maven {
                    name = "verification"
                    url = uri(rootProject.layout.buildDirectory.dir("verification-repo"))
                }
            }
        }
    }
}

tasks.named("check") {
    dependsOn(":tests:test")
}
