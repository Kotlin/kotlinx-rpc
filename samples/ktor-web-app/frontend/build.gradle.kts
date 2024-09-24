/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.kotlinx.rpc.platform)
}

kotlin {
    js(IR) {
        binaries.executable()

        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }

                val proxies = devServer?.proxy ?: mutableMapOf()
                proxies["/api"] = "http://localhost:8080"

                devServer = devServer?.copy(
                    port = 3000,
                    proxy = proxies
                )
            }
        }
    }

    sourceSets {
        jsMain {
            dependencies {
                implementation(projects.common)

                implementation(libs.kotlin.stdlib.js)
                implementation(libs.ktor.client.js)
                implementation(libs.ktor.client.websockets.js)
                implementation(libs.kotlinx.rpc.krpc.ktor.client)
                implementation(libs.kotlinx.rpc.krpc.serialization.json)

                implementation(project.dependencies.platform(libs.kotlin.wrappers.bom))
                implementation(libs.react)
                implementation(libs.react.dom)
                implementation(libs.emotion)
            }
        }
    }
}
