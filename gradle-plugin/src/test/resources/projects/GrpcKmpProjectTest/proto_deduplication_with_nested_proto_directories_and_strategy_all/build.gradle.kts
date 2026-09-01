/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.rpc.buf.*
import kotlinx.rpc.protoc.*

plugins {
    kotlin("multiplatform") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin") version "<rpc-version>"
}

kotlin {
    jvm()
}

rpc {
    protoc {
        buf {
            generate {
                includeImports = true
            }
        }

        plugins {
            kotlinMultiplatform {
                strategy = ProtocPlugin.Strategy.All
            }

            grpcKotlinMultiplatform {
                strategy = ProtocPlugin.Strategy.All
            }
        }
    }
}
