/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(InternalRpcApi::class)

import kotlinx.rpc.internal.InternalRpcApi
import kotlinx.rpc.internal.configureLocalProtocGenDevelopmentDependency

plugins {
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.kotlinx.rpc)
}

kotlin {
    sourceSets {
        main {
            dependencies {
                implementation(projects.grpc.grpcCore)
                implementation(projects.protobuf.protobufCore)

                implementation(libs.grpc.netty)
            }
        }
    }
}

rpc {
    protoc()
}

protoSourceSets {
    main {
        proto {

        }
    }
}

configureLocalProtocGenDevelopmentDependency("Main")


tasks.register<Jar>("testServicesJar") {
    archiveBaseName.set("testServices")
    archiveVersion.set("")

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(
        configurations.runtimeClasspath.map { prop ->
            prop.map { if (it.isDirectory()) it else zipTree(it) }
        }
    )

    with(tasks.jar.get())
}