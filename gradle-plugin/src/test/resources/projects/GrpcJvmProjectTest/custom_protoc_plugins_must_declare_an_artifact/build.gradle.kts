/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.kotlin.dsl.version
import kotlinx.rpc.proto.*

plugins {
    kotlin("jvm") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin")
}

rpc {
    grpc {
        protocPlugins {
            create("myPlugin") {}
        }
    }
}

protoSourceSets {
    main {
        protocPlugin(rpc.grpc.protocPlugins.named("myPlugin"))
    }
}
