/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.kotlin.dsl.version
import kotlinx.rpc.protoc.*

plugins {
    kotlin("jvm") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin") version "<rpc-version>"
}

rpc {
    protoc()
}

// Top-level dependencies block — exercises the generated `proto` configuration.
dependencies {
    proto("com.example.top:protos:1.0") {
        exclude(group = "com.example.top", module = "transitive-main")
    }
}

// Kotlin source-set dependencies block — exercises the new
// `KotlinDependencyHandler.protoImport(String, ExternalModuleDependency.() -> Unit)` extension.
kotlin.sourceSets.test {
    dependencies {
        protoImport("com.example.dsl:protos:1.0") {
            exclude(group = "com.example.dsl", module = "transitive-test")
        }
    }
}

tasks.register("verifyExcludes") {
    val mainProtoDeps = configurations.named("proto").map { it.dependencies.toList() }
    val testImportDeps = configurations.named("testProtoImport").map { it.dependencies.toList() }
    doLast {
        val mainProto = mainProtoDeps.get()
        check(mainProto.size == 1) { "Expected 1 'proto' dependency, got: $mainProto" }
        val mainDep = mainProto.single() as ExternalModuleDependency
        check(mainDep.group == "com.example.top" && mainDep.name == "protos" && mainDep.version == "1.0") {
            "Unexpected 'proto' dependency coordinates: ${mainDep.group}:${mainDep.name}:${mainDep.version}"
        }
        check(mainDep.excludeRules.any { it.group == "com.example.top" && it.module == "transitive-main" }) {
            "Expected exclude rule on 'proto' dependency; got: " +
                mainDep.excludeRules.map { "${it.group}:${it.module}" }
        }

        val testImport = testImportDeps.get()
        check(testImport.size == 1) { "Expected 1 'testProtoImport' dependency, got: $testImport" }
        val testDep = testImport.single() as ExternalModuleDependency
        check(testDep.group == "com.example.dsl" && testDep.name == "protos" && testDep.version == "1.0") {
            "Unexpected 'testProtoImport' dependency coordinates: ${testDep.group}:${testDep.name}:${testDep.version}"
        }
        check(testDep.excludeRules.any { it.group == "com.example.dsl" && it.module == "transitive-test" }) {
            "Expected exclude rule on 'testProtoImport' dependency; got: " +
                testDep.excludeRules.map { "${it.group}:${it.module}" }
        }
    }
}
