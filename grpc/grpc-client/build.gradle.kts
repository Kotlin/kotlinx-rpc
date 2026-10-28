/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import util.grpc.withGrpcClientTestServer
import util.other.localProperties
import util.targets.configureSwiftPMXcodeBuildConfiguration

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.kotlinx.rpc)
}

configureSwiftPMXcodeBuildConfiguration("Release")

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.grpc.grpcCore)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.coroutines.test)
                implementation(projects.tests.testProtos)
                implementation(projects.tests.testUtils)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.grpc.netty)
            }
        }

        iosTest {
            dependencies {
                implementation(projects.grpc.grpcMarshallerKotlinxSerialization)
                implementation(libs.serialization.json)
            }
        }

        nativeMain {
            dependencies {
                implementation(libs.atomicfu)
            }
        }
    }

    swiftPMDependencies {
        iosMinimumDeploymentTarget.set("18.0")
        discoverClangModulesImplicitly.set(false)
        val products = listOf(product("GrpcSwiftBridge", platforms = setOf(iOS())))

        val packagePath = project.localProperties.getProperty("kotlinx.rpc.grpc.swift.packagePath")
            ?.takeIf { it.isNotBlank() }
        if (packagePath == null) {
            swiftPackage(
                url = url("https://github.com/Kotlin/kotlinx-rpc-grpc-swift.git"),
                version = revision(libs.versions.grpc.swift.bridge.get()),
                products = products,
            )
        } else {
            // Use the local package instead of the published one.
            val directory = rootProject.file(packagePath)
            require(directory.resolve("Package.swift").isFile) {
                "No Package.swift found in $directory. " +
                    "Set kotlinx.rpc.grpc.swift.packagePath in local.properties to the Swift package directory."
            }
            localSwiftPackage(
                directory = layout.projectDirectory.dir(directory.absolutePath),
                products = products,
            )
        }
    }

}

tasks.withType<AbstractTestTask>().configureEach {
    withGrpcClientTestServer()
}
