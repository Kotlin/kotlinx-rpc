/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.file.Files

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.serialization)
    alias(libs.plugins.kotlinx.rpc)
    alias(libs.plugins.atomicfu)
}

tasks.withType<KotlinCompile> {
    if (this.name.contains("Test")) {
        compilerOptions {
            // for kotlin.time.Clock API
            languageVersion.set(KotlinVersion.KOTLIN_2_1)
            apiVersion.set(KotlinVersion.KOTLIN_2_1)
        }
    }
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.krpc.krpcCore)
                api(projects.krpc.krpcServer)
                api(projects.krpc.krpcClient)
                api(projects.krpc.krpcLogging)

                implementation(projects.krpc.krpcSerialization.krpcSerializationJson)

                implementation(libs.serialization.core)
                implementation(libs.kotlin.test)
                implementation(libs.coroutines.test)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.coroutines.debug)
                implementation(libs.kotlin.test.junit)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.krpc.krpcTest)
                implementation(projects.krpc.krpcSerialization.krpcSerializationJson)
                implementation(projects.krpc.krpcSerialization.krpcSerializationCbor)
                implementation(projects.krpc.krpcSerialization.krpcSerializationProtobuf)
                implementation(projects.krpc.krpcLogging)

                implementation(libs.coroutines.test)
                implementation(libs.kotlin.reflect)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.slf4j.api)
                implementation(libs.logback.classic)
                implementation(libs.coroutines.debug)
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

tasks.withType<KotlinJsTest> {
    onlyIf {
        // for some reason browser tests don't wait for the test to complete and end immediately
        // KRPC-166
        !targetName.orEmpty().endsWith("browser")
    }
}

tasks.register("moveToGold") {
    doLast {
        resourcesPath.walk().forEach {
            if (it.isFile && it.extension == tmpExt) {
                val gold = File(it.absolutePath.replace(".$tmpExt", ".$goldExt")).toPath()
                    ?: error("Expected file with replaced '.$tmpExt' extension to '.$goldExt' extension: $it")

                Files.write(gold, it.readBytes())
                it.delete()
            }
        }
    }
}
