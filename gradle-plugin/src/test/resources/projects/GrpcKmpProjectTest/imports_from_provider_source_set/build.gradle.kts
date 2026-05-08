/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.kotlin.dsl.version

plugins {
    kotlin("multiplatform") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin") version "<rpc-version>"
}

kotlin {
    jvm()
    sourceSets {
        create("common2")
    }
}

rpc {
    protoc()
}

protoSourceSets {
    named("commonMain") {
        importsFrom(project.provider { protoSourceSets.getByName("common2") })
    }
}

dependencies {
    // Exercises ConfigurationCompat.extendsFromLazy through the public
    // importsFrom(Provider<ProtoSourceSet>) path. On Gradle 9.4+ this uses
    // Configuration.extendsFrom(Provider); on 8.8–9.3 it falls back to the
    // legacy ListProperty plumbing in DefaultProtoSourceSet.
    // String accessor — common2 source set is declared in this script,
    // so no precompiled Kotlin DSL accessor is generated.
    "common2ProtoImport"(files("common-2-import-dependency.zip"))
}
