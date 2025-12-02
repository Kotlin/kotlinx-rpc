/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.kotlin.dsl.version
import kotlinx.rpc.protoc.proto

plugins {
    kotlin("jvm") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin")
}

sourceSets {
    main {
        proto {
            exclude("no.proto")
        }
    }

    test {
        proto {
            exclude("no2.proto")
        }
    }
}

rpc {
    protoc()
}
