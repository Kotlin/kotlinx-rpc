/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.kotlin.dsl.version
import kotlinx.rpc.protoc.*

plugins {
    kotlin("multiplatform") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin") version "<rpc-version>"
}

kotlin {
    jvm()

    sourceSets {
        // Kotlin source-set dependencies block — exercises the new
        // `KotlinDependencyHandler.protoImport(String, ExternalModuleDependency.() -> Unit)` extension.
        commonMain {
            dependencies {
                protoImport("com.example.dsl:protos:1.0") {
                    exclude(group = "com.example.dsl", module = "transitive-import")
                }
            }
        }
    }
}

rpc {
    protoc()
}

// Top-level dependencies block — exercises the generated `commonMainProto` configuration.
dependencies {
    commonMainProto("com.example.top:protos:1.0") {
        exclude(group = "com.example.top", module = "transitive-main")
    }
}

tasks.register("verifyExcludes") {
    val mainProtoDeps = configurations.named("commonMainProto").map { it.dependencies.toList() }
    val importDeps = configurations.named("commonMainProtoImport").map { it.dependencies.toList() }
    doLast {
        val mainProto = mainProtoDeps.get()
        check(mainProto.size == 1) { "Expected 1 'commonMainProto' dependency, got: $mainProto" }
        val mainDep = mainProto.single() as ExternalModuleDependency
        check(mainDep.group == "com.example.top" && mainDep.name == "protos" && mainDep.version == "1.0") {
            "Unexpected 'commonMainProto' coordinates: ${mainDep.group}:${mainDep.name}:${mainDep.version}"
        }
        check(mainDep.excludeRules.any { it.group == "com.example.top" && it.module == "transitive-main" }) {
            "Expected exclude rule on 'commonMainProto' dependency; got: " +
                mainDep.excludeRules.map { "${it.group}:${it.module}" }
        }

        val import = importDeps.get()
        check(import.size == 1) { "Expected 1 'commonMainProtoImport' dependency, got: $import" }
        val importDep = import.single() as ExternalModuleDependency
        check(importDep.group == "com.example.dsl" && importDep.name == "protos" && importDep.version == "1.0") {
            "Unexpected 'commonMainProtoImport' coordinates: " +
                "${importDep.group}:${importDep.name}:${importDep.version}"
        }
        check(importDep.excludeRules.any { it.group == "com.example.dsl" && it.module == "transitive-import" }) {
            "Expected exclude rule on 'commonMainProtoImport' dependency; got: " +
                importDep.excludeRules.map { "${it.group}:${it.module}" }
        }
    }
}
