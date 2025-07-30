/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.kotlin.dsl.version

plugins {
    kotlin("jvm") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin")
}

protoSourceSets {
    main {
        proto {
            exclude("exclude/**")
            exclude("exclude.proto")
        }
    }

    test {
        proto {
            include("include/**")
            include("include.proto")
            include("some/package/hello/world/file.proto")
        }
    }
}

rpc {
    grpc()
}
