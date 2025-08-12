/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.kotlin.dsl.version
import kotlinx.rpc.protoc.*
import kotlinx.rpc.buf.*
import java.io.File
import kotlin.time.Duration.Companion.seconds

plugins {
    kotlin("jvm") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin")
}

rpc {
    protoc {
        buf {
            configFile = File("some.buf.yaml")
            logFormat = BufExtension.LogFormat.Json
            timeout = 60.seconds

            generate {
                includeImports = true
                includeWkt = true
                errorFormat = BufGenerateExtension.ErrorFormat.Json
            }
        }
    }
}
