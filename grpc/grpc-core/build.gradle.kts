/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.internal.extensions.stdlib.capitalized
import util.createCInterop

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core)
                api(projects.utils)
                api(libs.coroutines.core)
            }
        }

        jvmMain {
            dependencies {
                api(libs.grpc.util)
                api(libs.grpc.stub)
                api(libs.grpc.protobuf)
                api(libs.grpc.kotlin.stub)
                api(libs.protobuf.java.util)
                api(libs.protobuf.kotlin)
            }
        }
    }

    val grpcppCLib = projectDir.resolve("../grpcpp-c")

    fun findProgram(name: String) = org.gradle.internal.os.OperatingSystem.current().findInPath(name)
    val checkBazel by tasks.registering {
        doLast {
            val bazelPath = findProgram("bazel")
            if (bazelPath != null) {
                println("bazel: $bazelPath")
            } else {
                throw GradleException("'bazel' not found on PATH. Please install Bazel (https://bazel.build/).")
            }
        }
    }

    val buildGrpcppCLib = tasks.register<Exec>("buildGrpcppCLib") {
        group = "build"
        workingDir = grpcppCLib
        commandLine("bash", "-c", "bazel build :grpcpp_c_static --config=release")
        inputs.files(fileTree(grpcppCLib) { exclude("bazel-*/**") })
        outputs.dir(grpcppCLib.resolve("bazel-bin"))

        dependsOn(checkBazel)
    }

    createCInterop("libgrpcpp_c", sourceSet = "src/nativeMain") { target ->
        includeDirs(grpcppCLib.resolve("include"))
        extraOpts(
            "-libraryPath", "${grpcppCLib.resolve("bazel-out/darwin_arm64-opt/bin")}"
        )
        includeDirs(
            grpcppCLib.resolve("include"),
            grpcppCLib.resolve("bazel-grpcpp-c/external/grpc+/include")
        )

        tasks.named("cinteropLibgrpcpp_c${target.capitalized()}") {
            dependsOn(buildGrpcppCLib)
        }

    }

}
