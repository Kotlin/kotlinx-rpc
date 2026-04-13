/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.kotlin.dsl.version
import kotlinx.rpc.protoc.*

plugins {
    kotlin("jvm") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin") version "<rpc-version>"
}

val protoImport by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

dependencies {
    protoImport(files("dependency-protos.zip"))
}

kotlin.sourceSets {
    main {
        proto {
            dependencyImports.from(protoImport)
        }
    }
}

rpc {
    protoc()
}
