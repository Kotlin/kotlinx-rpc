/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(InternalRpcApi::class)

import kotlinx.rpc.internal.InternalRpcApi
import kotlinx.rpc.internal.configureLocalProtocGenDevelopmentDependency
import kotlinx.rpc.protoc.buf
import kotlinx.rpc.protoc.generate
import kotlinx.rpc.protoc.protoTasks
import org.jetbrains.kotlinx.library.api.watchdog.WatchdogChecks
import org.jetbrains.kotlinx.library.api.watchdog.WatchdogSeverity

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.protobuf.protobufLite)
            }
        }
    }
}

fun generatedCodeDir(sourceSetName: String): File = layout.projectDirectory
    .dir("src")
    .dir(sourceSetName)
    .dir("generated-code")
    .asFile

rpc {
    protoc {
        buf.generate {
            includeWkt = true

            comments {
                includeFileLevelComments = false
            }
        }
    }
}

apiWatchdog {
    ignore(
        check = WatchdogChecks.OPEN_API_WITHOUT_SUBCLASS_OPT_IN,
        whenAnnotatedWith = "kotlinx.rpc.protobuf.internal.GeneratedProtoMessage",
        recursive = true,
    )

    ignore(
        checks = listOf(
            WatchdogChecks.EXHAUSTIVE_PUBLIC_API,
            WatchdogChecks.STATEFUL_CLASS_WITHOUT_TO_STRING,
            WatchdogChecks.STATEFUL_CLASS_WITHOUT_EQUALS,
            WatchdogChecks.STATEFUL_CLASS_WITHOUT_HASH_CODE,
            WatchdogChecks.DATA_CLASS_PUBLIC_API,
        ),
        whenAnnotatedWith = "kotlinx.rpc.protobuf.internal.GeneratedProtoEnum",
        recursive = true,
    )

    ignore(
        checks = listOf(
            WatchdogChecks.EXHAUSTIVE_PUBLIC_API,
            WatchdogChecks.INLINE_FUNCTION_WITH_LOGIC,
        ),
        whenAnnotatedWith = "kotlinx.rpc.protobuf.internal.GeneratedProtoDeclaration",
    )

    nullableBooleanPublicApi = WatchdogSeverity.NONE
}

protoTasks.buf.generate.matchingKotlinSourceSet(kotlin.sourceSets.commonMain).configureEach {
    outputDirectory = generatedCodeDir(properties.sourceSetNames.single())
}

configureLocalProtocGenDevelopmentDependency("Main")
