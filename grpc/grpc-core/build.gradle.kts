/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.rpc.buf.tasks.BufGenerateTask
import kotlinx.rpc.proto.kotlinMultiplatform
import org.gradle.internal.extensions.stdlib.capitalized
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.CInteropProcess

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
}



kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.core)
                api(projects.utils)
                api(libs.coroutines.core)

                implementation(libs.atomicfu)
                implementation(libs.kotlinx.io.core)
                implementation(libs.kotlinx.collections.immutable)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        jvmMain {
            dependencies {
                api(libs.grpc.api)
                api(libs.grpc.util)
                api(libs.grpc.stub)
                api(libs.grpc.protobuf)
                api("io.grpc:grpc-protobuf-lite:${libs.versions.grpc.asProvider().get()}")
                implementation(libs.grpc.kotlin.stub) // causes problems to jpms if api
                api(libs.protobuf.java.util)
                implementation(libs.protobuf.kotlin)
            }
        }

        jvmTest {
            dependencies {
                implementation(projects.grpc.grpcCore)
                implementation(libs.coroutines.core)
                implementation(libs.coroutines.test)

                implementation(libs.grpc.stub)
                implementation(libs.grpc.netty)
                implementation(libs.grpc.protobuf)
                implementation(libs.grpc.kotlin.stub)
                implementation(libs.protobuf.java.util)
                implementation(libs.protobuf.kotlin)
            }
        }
    }

    val grpcppCLib = projectDir.resolve("../grpcpp-c")

    // TODO: Replace function implementation, so it does not use an internal API
    fun findProgram(name: String) = org.gradle.internal.os.OperatingSystem.current().findInPath(name)
    val checkBazel by tasks.registering {
        doLast {
            val bazelPath = findProgram("bazel")
            if (bazelPath != null) {
                logger.debug("bazel: {}", bazelPath)
            } else {
                throw GradleException("'bazel' not found on PATH. Please install Bazel (https://bazel.build/).")
            }
        }
    }

    val buildGrpcppCLib = tasks.register<Exec>("buildGrpcppCLib") {
        group = "build"
        workingDir = grpcppCLib
        commandLine("bash", "-c", "bazel build :grpcpp_c_static :protowire_static --config=release")
        inputs.files(fileTree(grpcppCLib) { exclude("bazel-*/**") })
        outputs.dir(grpcppCLib.resolve("bazel-bin"))

        dependsOn(checkBazel)
    }


    targets.filterIsInstance<KotlinNativeTarget>().forEach {
        it.compilations.getByName("main") {
            cinterops {
                val libgrpcpp_c by creating {
                    includeDirs(
                        grpcppCLib.resolve("include"),
                        grpcppCLib.resolve("bazel-grpcpp-c/external/grpc+/include")
                    )
                    extraOpts(
                        "-libraryPath", "${grpcppCLib.resolve("bazel-out/darwin_arm64-opt/bin")}",
                    )
                }

                val interopTask = "cinterop${libgrpcpp_c.name.capitalized()}${it.targetName.capitalized()}"
                tasks.named(interopTask, CInteropProcess::class) {
                    dependsOn(buildGrpcppCLib)
                }


                val libprotowire by creating {
                    includeDirs(
                        grpcppCLib.resolve("include")
                    )
                    extraOpts(
                        "-libraryPath", "${grpcppCLib.resolve("bazel-out/darwin_arm64-opt/bin")}",
                    )
                }

                val libProtowireTask = "cinterop${libprotowire.name.capitalized()}${it.targetName.capitalized()}"
                tasks.named(libProtowireTask, CInteropProcess::class) {
                    dependsOn(buildGrpcppCLib)
                }

            }
        }
    }
}

protoSourceSets {
    jvmTest {
        proto {
            exclude("exclude/**")
        }
    }

    configureEach {
        proto {
            exclude("exclude/**")
        }
    }
}


rpc {
    grpc {
        val globalRootDir: String by extra

        protocPlugins.kotlinMultiplatform {
            local {
                javaJar("$globalRootDir/protoc-gen/build/libs/protoc-gen-$version-all.jar")
            }
        }

        project.tasks.withType<BufGenerateTask>().configureEach {

            // TODO: Remove this once we remove JVM generation
            // Set compile for common(native) option
            if (name.endsWith("CommonTest")) {
                protocPlugins.kotlinMultiplatform {
                    options.set(options.getOrElse(emptyMap()) + mapOf("targetMode" to "common"))
                }
            }

            if (name.endsWith("Test")) {
                dependsOn(gradle.includedBuild("protoc-gen").task(":jar"))
            }
        }
    }
}
