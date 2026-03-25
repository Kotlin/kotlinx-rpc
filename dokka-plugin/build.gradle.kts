/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "org.jetbrains.kotlinx"
version = libs.versions.kotlinx.rpc.get()

logger.lifecycle("[Dokka Plugin] kotlinx.rpc project version: $version, Kotlin version: ${libs.versions.kotlin.lang.get()}")

val generatedResDir = layout.buildDirectory.dir("generated/rpc-dokka-config")

val generateRpcDokkaConfig by tasks.registering {
    val coreVersion = libs.versions.kotlinx.rpc.get()
    val grpcDevVersion = libs.versions.grpc.dev.get()
    val outputDir = generatedResDir.get().asFile
    inputs.property("coreVersion", coreVersion)
    inputs.property("grpcDevVersion", grpcDevVersion)
    outputs.dir(outputDir)
    doLast {
        outputDir.mkdirs()
        outputDir.resolve("rpc-dokka-config.properties")
            .writeText("coreVersion=$coreVersion\ngrpcDevVersion=$grpcDevVersion")
    }
}

tasks.processResources {
    dependsOn(generateRpcDokkaConfig)
}

sourceSets.main {
    resources.srcDir(generatedResDir)
}

dependencies {
    compileOnly(libs.dokka.core)
    compileOnly(libs.dokka.base)

    testImplementation(kotlin("test"))
    testImplementation(libs.dokka.base)
    testImplementation(libs.dokka.test.api)
    testImplementation(libs.dokka.base.test.utils)
    testImplementation(libs.dokka.analysis.kotlin.symbols)
}
