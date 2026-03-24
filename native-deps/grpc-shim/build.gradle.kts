/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
plugins {
    base
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.conventions.kmp) apply false
    alias(libs.plugins.conventions.jvm) apply false
}

group = "org.jetbrains.kotlinx"
val grpcVersion = providers.gradleProperty("grpcVersion").get()
val shimVersion = providers.gradleProperty("shimVersion").get()
version = "$grpcVersion-$shimVersion"

allprojects {
    group = rootProject.group
    version = rootProject.version

    plugins.withId("maven-publish") {
        extensions.configure<PublishingExtension> {
            repositories {
                maven {
                    name = "verification"
                    url = uri(rootProject.layout.buildDirectory.dir("verification-repo"))
                }
            }
        }
    }
}

tasks.named("assemble") {
    dependsOn(":core:assemble")
    dependsOn(":annotation:assemble")
    dependsOn(":tests:assemble")
}

tasks.register("publishAllPublicationsToVerificationRepository") {
    group = "publishing"
    dependsOn(":core:publishAllPublicationsToVerificationRepository")
    dependsOn(":annotation:publishAllPublicationsToVerificationRepository")
}

tasks.register("publishAllPublicationsToBuildRepoRepository") {
    group = "publishing"
    dependsOn(":core:publishAllPublicationsToBuildRepoRepository")
    dependsOn(":annotation:publishAllPublicationsToBuildRepoRepository")
}

tasks.register("publishToBuildRepo") {
    group = "publishing"
    dependsOn("publishAllPublicationsToBuildRepoRepository")
}

val verifyUnsupportedApiNegative = tasks.register("verifyUnsupportedApiNegative") {
    group = "verification"
    dependsOn(":tests:negativeTest")
}

val verifyUnsupportedApiPositive = tasks.register("verifyUnsupportedApiPositive") {
    group = "verification"
    dependsOn(":tests:positiveTest")
}

val verifyUnsupportedApiScope = tasks.register("verifyUnsupportedApiScope") {
    group = "verification"
    dependsOn(":tests:scopeTest")
}

val verifyUnsupportedApiArtifact = tasks.register("verifyUnsupportedApiArtifact") {
    group = "verification"
    dependsOn(":tests:artifactTest")
}

tasks.register("verifyGrpcShimUnsupportedApiOptIn") {
    group = "verification"
    dependsOn(
        verifyUnsupportedApiNegative,
        verifyUnsupportedApiPositive,
        verifyUnsupportedApiScope,
        verifyUnsupportedApiArtifact,
    )
}

tasks.named("check") {
    dependsOn("verifyGrpcShimUnsupportedApiOptIn")
}
