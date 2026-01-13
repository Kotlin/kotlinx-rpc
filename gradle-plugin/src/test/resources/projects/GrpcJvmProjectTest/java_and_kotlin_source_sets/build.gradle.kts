/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.kotlin.dsl.version
import kotlinx.rpc.protoc.*

plugins {
    kotlin("jvm") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin") version "<rpc-version>"
}

sourceSets {
    main {
        proto {
            plugin { getByName("myPlugin") }
            exclude("no.proto")
        }
    }

    test {
        proto {
            exclude("no2.proto")
        }
    }
}

kotlin {
    sourceSets.main {
        proto {
            plugin { getByName("myPlugin2") }
        }
    }
}

rpc {
    protoc {
        plugins {
            create("myPlugin") {
                local {
                    executor("some")
                }
            }

            create("myPlugin2") {
                local {
                    executor("some2")
                }
            }
        }
    }
}
