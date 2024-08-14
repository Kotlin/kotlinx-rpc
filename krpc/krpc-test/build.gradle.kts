/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest
import org.jetbrains.kotlin.utils.fileUtils.withReplacedExtensionOrNull
import java.nio.file.Files

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.serialization)
    alias(libs.plugins.kotlinx.rpc)
    alias(libs.plugins.atomicfu)
}

kotlin {
    sourceSets {
        val jvmMain by getting {
            dependencies {
                api(projects.core)
                api(projects.krpc.krpcServer)
                api(projects.krpc.krpcClient)

                implementation(projects.krpc.krpcSerialization.krpcSerializationJson)

                implementation(libs.coroutines.core)
                implementation(libs.serialization.core)
                implementation(libs.kotlin.test)
                implementation(libs.kotlin.test.junit)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(projects.krpc.krpcTest)
                implementation(projects.krpc.krpcSerialization.krpcSerializationJson)
                implementation(projects.krpc.krpcSerialization.krpcSerializationCbor)
                implementation(projects.krpc.krpcSerialization.krpcSerializationProtobuf)
                implementation(projects.krpc.krpcLogging)

                implementation(projects.utils)

                implementation(libs.slf4j.api)
                implementation(libs.logback.classic)

                implementation(libs.coroutines.test)
                implementation(libs.coroutines.debug)
                implementation(libs.kotlin.reflect)
            }
        }
    }

    explicitApi = ExplicitApiMode.Disabled
}

tasks.withType<KotlinJvmTest> {
    environment("LIBRARY_VERSION", libs.versions.kotlinx.rpc.get())
}

val resourcesPath = projectDir.resolve("src/jvmTest/resources")
val tmpExt = "tmp"
val goldExt = "gold"

tasks.named<Delete>("clean") {
    delete(resourcesPath.walk().filter { it.isFile && it.extension == tmpExt }.toList())
}

tasks.create("moveToGold") {
    doLast {
        resourcesPath.walk().forEach {
            if (it.isFile && it.extension == tmpExt) {
                val gold = it.withReplacedExtensionOrNull(tmpExt, goldExt)?.toPath()
                    ?: error("Expected file with replaced '.$tmpExt' extension to '.$goldExt' extension: $it")

                Files.write(gold, it.readBytes())
                it.delete()
            }
        }
    }
}
