/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(InternalRpcApi::class)

import kotlinx.rpc.internal.InternalRpcApi
import kotlinx.rpc.internal.configureLocalProtocGenDevelopmentDependency
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
    alias(libs.plugins.kotlinx.benchmark)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.protobuf.protobuf)
                implementation(libs.kotlinx.benchmark.runtime)
            }
        }

        jvmMain {
            dependencies {
                // The JMH generator loads the benchmark classes from the compile classpath.
                // ProtoGrpcMarshaller of protobuf-lite refers to protobuf-javalite types, which protobuf-lite
                // doesn't expose.
                compileOnly(libs.protobuf.javalite)
            }
        }
    }

    explicitApi = ExplicitApiMode.Disabled
}

val hostNativeTarget: String? = run {
    val os = providers.systemProperty("os.name").get().lowercase()
    val arm = providers.systemProperty("os.arch").get() in setOf("aarch64", "arm64")
    when {
        os.contains("mac") && arm -> "macosArm64"
        os.contains("linux") && arm -> "linuxArm64"
        os.contains("linux") -> "linuxX64"
        else -> null
    }
}

// Results go to results/<label>/main/<timestamp>/, which git ignores. Set the label with -Pprotobuf.bench.label=<label>.
val benchmarkLabel: String = providers.gradleProperty("protobuf.bench.label").getOrElse("local")

benchmark {
    reportsDir = layout.projectDirectory.dir("results").dir(benchmarkLabel).asFile.absolutePath

    configurations {
        named("main") {
            warmups = 3
            iterations = 5
            iterationTime = 1
            iterationTimeUnit = "s"
            mode = "avgt"
            outputTimeUnit = "us"
            reportFormat = "json"
        }
    }

    targets {
        register("jvm")
        hostNativeTarget?.let { register(it) }
    }
}

configureLocalProtocGenDevelopmentDependency("Main")
