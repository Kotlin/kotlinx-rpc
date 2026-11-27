/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.rpc.buf.*
import kotlinx.rpc.buf.tasks.BufLockTask
import kotlinx.rpc.buf.tasks.ProtobufGenerateTask
import kotlinx.rpc.protoc.*
import org.gradle.kotlin.dsl.version

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
            deps {
                module("buf.build/googleapis/googleapis")
            }
        }
    }
}

val protoArchive = tasks.register<Zip>("protoArchive") {
    from("protos")
    destinationDirectory.set(layout.buildDirectory.dir("protoArchive"))
}

dependencies {
    commonMainProto(files(protoArchive))
}

tasks.withType<BufLockTask>().configureEach {
    doFirst {
        if (!persistentLockFile.get().asFile.isFile) {
            throw GradleException("Buf Schema Registry is unavailable for $path.")
        }
    }
}

tasks.withType<ProtobufGenerateTask>().configureEach {
    configFile.set(file("buf-local.yaml"))
}
