/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest
import org.jetbrains.kotlin.utils.fileUtils.withReplacedExtensionOrNull
import java.nio.file.Files

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.rpc)
    alias(libs.plugins.atomicfu)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":kotlinx-rpc-runtime:kotlinx-rpc-runtime-api"))

                implementation(project(":kotlinx-rpc-utils"))
                implementation(project(":kotlinx-rpc-runtime:kotlinx-rpc-runtime-serialization"))

                api(libs.coroutines.core)
                implementation(libs.serialization.core)
                implementation(libs.kotlin.reflect)

                implementation(project(":kotlinx-rpc-runtime:kotlinx-rpc-runtime-logging"))
            }
        }
        commonTest {
            dependencies {
                implementation(project(":kotlinx-rpc-runtime:kotlinx-rpc-runtime-client"))
                implementation(project(":kotlinx-rpc-runtime:kotlinx-rpc-runtime-server"))
                implementation(project(":kotlinx-rpc-runtime:kotlinx-rpc-runtime-serialization"))

                implementation(libs.kotlin.test)
                implementation(libs.coroutines.test)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.slf4j.api)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(project(":kotlinx-rpc-runtime:kotlinx-rpc-runtime-test"))
                implementation(project(":kotlinx-rpc-runtime:kotlinx-rpc-runtime-serialization:kotlinx-rpc-runtime-serialization-json"))
                implementation(project(":kotlinx-rpc-runtime:kotlinx-rpc-runtime-serialization:kotlinx-rpc-runtime-serialization-cbor"))
                implementation(project(":kotlinx-rpc-runtime:kotlinx-rpc-runtime-serialization:kotlinx-rpc-runtime-serialization-protobuf"))

                implementation(libs.logback.classic)
            }
        }
    }
}

tasks.withType<KotlinJvmTest> {
    environment("LIBRARY_VERSION", libs.versions.kotlinx.rpc.get())
}

val resourcesPath = projectDir.resolve("src/jvmTest/resources")
val tmpExt = "tmp"
val goldExt = "gold"

tasks.named("clean") {
    doLast {
        resourcesPath.walk().forEach {
            if (it.isFile && it.extension == tmpExt) {
                it.delete()
            }
        }
    }
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

evaluationDependsOn(":kotlinx-rpc-runtime:kotlinx-rpc-runtime-test")

// otherwise it complains and fails the build on 1.8.*
val jsProductionLibraryCompileSync: TaskProvider<Task> = project(":kotlinx-rpc-runtime:kotlinx-rpc-runtime-test")
    .tasks.named("jsProductionLibraryCompileSync")

tasks.named("jsBrowserTest") {
    dependsOn(jsProductionLibraryCompileSync)
}

tasks.named("jsNodeTest") {
    dependsOn(jsProductionLibraryCompileSync)
}
