/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.kotlin.dsl.version
import kotlinx.rpc.protoc.*

plugins {
    kotlin("jvm") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin")
}

rpc {
    protoc {
        plugins {
            create("myPlugin") {
                local {
                    executor("path", "to", "protoc-gen-myplugin.exe")
                }

                options.put("hello", "world")
                options.put("foo", "bar")

                strategy.set(ProtocPlugin.Strategy.All)
                includeImports.set(true)
                includeWkt.set(false)
                types = listOf("my.type.Yay")
                excludeTypes = listOf("my.type.Nope")
            }
            create("myRemotePlugin") {
                remote {
                    locator = "my.remote.plugin"
                }
                options.put("hello", "world")
            }
        }
    }
}

kotlin.sourceSets {
    main.proto {
        plugin { getByName("myPlugin") }
        plugin({ options.put("only", "in main") }) { getByName("myRemotePlugin") }
    }
    test.proto {
        plugin({ options.put("only", "in test") }) { getByName("myRemotePlugin") }
    }
}
