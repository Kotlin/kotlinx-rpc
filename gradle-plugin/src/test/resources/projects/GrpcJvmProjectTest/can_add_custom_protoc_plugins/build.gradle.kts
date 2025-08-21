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
            create("myPlugin") {
                local {
                    executor("path", "to", "protoc-gen-myplugin.exe")
                }

                options.put("hello", "world")
                options.put("foo", "bar")

                strategy = ProtocPlugin.Strategy.All
                includeImports = true
                includeWkt = false
                types = listOf("my.type.Yay")
                excludeTypes = listOf("my.type.Nope")
            }
            create("myRemotePlugin") {
                remote {
                    locator = "my.remote.plugin"
                }
            }
        }
    }
}

protoSourceSets {
    main {
        protocPlugin(rpc.grpc.protocPlugins.named("myPlugin"))
        protocPlugin(rpc.grpc.protocPlugins.named("myRemotePlugin"))
    }
}
